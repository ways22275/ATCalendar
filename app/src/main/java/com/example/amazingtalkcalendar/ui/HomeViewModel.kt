package com.example.amazingtalkcalendar.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amazingtalkcalendar.R
import com.example.amazingtalkcalendar.data.model.*
import com.example.amazingtalkcalendar.data.reposiroty.ScheduleRepository
import com.example.amazingtalkcalendar.utils.DateUtils
import com.example.amazingtalkcalendar.utils.DateUtils.EVENING_TIME
import com.example.amazingtalkcalendar.utils.DateUtils.NOON_TIME
import com.example.amazingtalkcalendar.utils.DateUtils.commonFullDateWithTimePattern
import com.example.amazingtalkcalendar.utils.DateUtils.commonFullTimePattern
import com.example.amazingtalkcalendar.utils.DateUtils.getDateDisplayName
import com.example.amazingtalkcalendar.utils.DateUtils.getDaysOfCurrentWeek
import com.example.amazingtalkcalendar.utils.DateUtils.getSpecificDate
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val repo: ScheduleRepository
) : ViewModel() {

  private val daysOfCurrentWeekList = MutableLiveData<MutableList<ScheduleTitleDto>>()
  private val daysOfCurrentWeekIndex = MutableLiveData(-1)
  private val daysOfCurrentWeek = Transformations.map(daysOfCurrentWeekIndex) { index ->
    val list = daysOfCurrentWeekList.value ?: emptyList()
    return@map when {
      list.isEmpty() -> {
        val scheduleDto = getDaysOfCurrentWeek(getSpecificDate())
        daysOfCurrentWeekList.value = list.toMutableList().apply { add(scheduleDto) }
        val startAt = DateUtils.getFormatUTCDateStringByLocalTime(
          DateUtils.getSpecificDateTime(
            LocalDate.parse(scheduleDto.firstDayOfWeek).atTime(0, 0, 0).format(commonFullTimePattern)
          )
        )
        fetchSchedules(startAt = startAt)
        scheduleDto
      }
      list.getOrNull(index) == null -> {
        val specificDate = getSpecificDate(list[(index - 1)].lastDayOfWeek)
        val scheduleDto = getDaysOfCurrentWeek(specificDate)
        daysOfCurrentWeekList.value = list.toMutableList().apply { add(scheduleDto) }
        val startAt = DateUtils.getFormatUTCDateStringByLocalTime(
          DateUtils.getSpecificDateTime(
            LocalDate.parse(scheduleDto.firstDayOfWeek).atTime(0, 0, 0).format(commonFullTimePattern)
          )
        )
        fetchSchedules(startAt = startAt)
        scheduleDto
      }
      else -> {
        list[index]
      }
    }
  }

  private val dateTimeStrToLocalDateTime: (ScheduleItem) -> LocalDateTime = {
    LocalDateTime.parse(it.startAt, DateUtils.utcFullTimePattern)
  }

  private val scheduleData = MutableLiveData<List<ScheduleSealed>>()

  val title = Transformations.map(daysOfCurrentWeek) {
    "${it.firstDayOfWeek} - ${it.lastDayOfWeek}"
  }
  val previousBtnEnabled = Transformations.map(daysOfCurrentWeekIndex) { index ->
    return@map (index - 1) >= 0
  }

  val subTitleList = Transformations.map(daysOfCurrentWeek) { scheduleTitleDto ->
    return@map scheduleTitleDto.daysOfWeek.map {
      DayOfCurrentWeek(
        originDate = LocalDate.parse(it).atTime(0, 0, 0).format(commonFullTimePattern),
        displayName = getDateDisplayName(it)
      )
    }
  }

  init {
    nextWeek()
  }

  fun nextWeek() {
    val index = daysOfCurrentWeekIndex.value ?: -1
    daysOfCurrentWeekIndex.value = index.plus(1)
  }

  fun previous() {
    val index = daysOfCurrentWeekIndex.value ?: -1
    if (index > 0) {
      daysOfCurrentWeekIndex.value = index.plus(-1)
    }
  }

  fun getScheduleByStartAt(currentDate: String): List<ScheduleSealed> {
    val list = scheduleData.value ?: emptyList()
    val specificList = list.filterIsInstance<ScheduleItem>().filter {
      val date = LocalDateTime.parse(currentDate, commonFullTimePattern).format(commonFullDateWithTimePattern)
      val max = LocalDateTime.parse(it.startAt, commonFullTimePattern).format(commonFullDateWithTimePattern)
      date == max
    }
    return mergeScheduleListByDividerItem(specificList)
  }

  private fun fetchSchedules(teacherName: String = "rebecca-shao", startAt: String) {
    viewModelScope.launch {
      val dataList = repo.fetchSchedules(teacherName, startAt).toList().last().data ?: return@launch
      val sortedDataList = (
          dataList.available.map {
            ScheduleItem(
              startAt = it.start,
              endAt = it.end,
              available = true
            )
          } + dataList.booked.map {
            ScheduleItem(
              startAt = it.start,
              endAt = it.end,
              available = false
            )
          })
        .sortedBy(dateTimeStrToLocalDateTime)

      val originList = scheduleData.value ?: emptyList()

      scheduleData.value = originList + expansionSchedule(sortedDataList)
    }
  }

  private fun expansionSchedule(scheduleList: List<ScheduleItem>): List<ScheduleSealed> {
    val resultList = mutableListOf<ScheduleSealed>()
    scheduleList.forEach {
      val dividedList = divideByHalfHour(it.available, it.startAt, it.endAt)
      if (dividedList.isNotEmpty()) {
        resultList.addAll(dividedList)
      }
    }
    return resultList
  }

  private fun divideByHalfHour(
    available: Boolean,
    start: String,
    end: String
  ): List<ScheduleSealed> {

    var startTimestamp = Timestamp.valueOf(start.replace("T", " ").replace("Z", "")).time
    val endTimestamp = Timestamp.valueOf(end.replace("T", " ").replace("Z", "")).time

    val duration = endTimestamp - startTimestamp
    if (duration < DateUtils.HALT_HOUR_MILE_SECOND) {
      return emptyList()
    }

    val total = (duration / DateUtils.HALT_HOUR_MILE_SECOND)
    val finishList = mutableListOf<ScheduleSealed>()

    for (i in 0 until total) {
      finishList.add(
        ScheduleItem(
          startAt = DateUtils.getFormatLocalDateStringByUTCTime(startTimestamp),
          endAt = DateUtils.getFormatLocalDateStringByUTCTime(startTimestamp + DateUtils.HALT_HOUR_MILE_SECOND),
          available = available
        )
      )
      startTimestamp += DateUtils.HALT_HOUR_MILE_SECOND
    }
    return finishList
  }

  private fun mergeScheduleListByDividerItem(list: List<ScheduleSealed>): List<ScheduleSealed> {
    val noonTime = LocalTime.parse(NOON_TIME)
    val eveningTime = LocalTime.parse(EVENING_TIME)
    val result = list.toMutableList()

    val morningItemIndex = result.indexOf(list.find {
      val scheduleItemTime = LocalDateTime.parse((it as ScheduleItem).startAt, commonFullTimePattern).toLocalTime()
      scheduleItemTime.isBefore(noonTime)
    })

    if (morningItemIndex != -1) {
      result.add(morningItemIndex, ScheduleDividerItem(context.getString(R.string.morning)))
    }

    val afternoonItemIndex = result.indexOf(list.find {
      if (it is ScheduleItem) {
        val scheduleItemTime = LocalDateTime.parse(it.startAt, commonFullTimePattern).toLocalTime()
        return@find when {
          scheduleItemTime == noonTime -> true
          (scheduleItemTime.isAfter(noonTime) && scheduleItemTime.isBefore(eveningTime)) -> true
          else -> false
        }
      } else {
        false
      }
    })

    if (afternoonItemIndex != -1) {
      result.add(afternoonItemIndex, ScheduleDividerItem(context.getString(R.string.evening)))
    }

    val nightItemIndex = result.indexOf(list.find {
      if (it is ScheduleItem) {
        val scheduleItemTime = LocalDateTime.parse((it as ScheduleItem).startAt, commonFullTimePattern).toLocalTime()
        return@find when {
          scheduleItemTime == eveningTime -> true
          scheduleItemTime.isAfter(eveningTime) -> true
          else -> false
        }
      } else {
        false
      }
    })

    if (nightItemIndex != -1) {
      result.add(nightItemIndex, ScheduleDividerItem(context.getString(R.string.night)))
    }

    return result
  }
}