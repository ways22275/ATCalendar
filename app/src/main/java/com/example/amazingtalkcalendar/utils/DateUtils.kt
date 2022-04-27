package com.example.amazingtalkcalendar.utils

import com.example.amazingtalkcalendar.data.model.ScheduleTitleDto
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

object DateUtils {

  const val HALT_HOUR_MILE_SECOND = 1800000

  const val NOON_TIME = "12:00"
  const val EVENING_TIME = "17:00"

  val utcFullTimePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
  val commonFullTimePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  val commonFullDateWithTimePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00")

  private val localZoneOffset = ZoneOffset.of(OffsetDateTime.now().offset.id)

  private val commonFullDatePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  private val commonDayOfWeekPatternWithUS = DateTimeFormatter.ofPattern("E, MMM dd")
  private val commonDayOfWeekPatternWithZH = DateTimeFormatter.ofPattern("E - dd")
  private val commonShortTimePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

  private val weekFields = WeekFields.ISO

  fun getSpecificTime(specificTimeStr: String? = null): LocalTime {
    return if (specificTimeStr.isNullOrEmpty()) {
      LocalTime.now()
    } else {
      LocalTime.parse(specificTimeStr, commonShortTimePattern)
    }
  }

  fun getSpecificDateTime(specificDateStr: String? = null): LocalDateTime {
    val now = LocalDateTime.now()
    return if (specificDateStr.isNullOrEmpty()) {
      now
    } else {
      LocalDateTime.parse(specificDateStr, commonFullTimePattern)
    }
  }

  fun getSpecificDate(specificDateStr: String? = null): LocalDate {
    return if (specificDateStr.isNullOrEmpty()) {
      LocalDate.now()
    } else {
      LocalDate.parse(specificDateStr, commonFullDatePattern)
    }
  }

  // TODO Unit test
  fun getFormatUTCDateStringByLocalTime(
    localDateTime: LocalDateTime,
    pattern: DateTimeFormatter = utcFullTimePattern
  ): String {
    return OffsetDateTime.of(localDateTime, localZoneOffset)
      .atZoneSameInstant(ZoneOffset.UTC)
      .format(pattern)
  }

  // TODO Unit test
  fun getFormatLocalDateStringByUTCTime(utcTime: Long): String {
    val instant = Instant.ofEpochMilli(utcTime)
    val offset = OffsetDateTime.now().offset
    val localDateTime = LocalDateTime.ofInstant(instant, offset)
    return OffsetDateTime.of(localDateTime, ZoneOffset.UTC).atZoneSameInstant(OffsetDateTime.now().offset)
      .format(commonFullTimePattern)
  }

  // TODO Unit test
  fun getDaysOfCurrentWeek(localDate: LocalDate): ScheduleTitleDto {
    var date = localDate.apply {
      with(weekFields.dayOfWeek(), 1)
    }
    val daysOfWeek = mutableListOf<String>().apply {
      for (i in 0..6) {
        add(date.format(commonFullDatePattern))
        date = date.plusDays(1)
      }
    }
    return ScheduleTitleDto(
      year = date.year.toString(),
      firstDayOfWeek = daysOfWeek[0],
      lastDayOfWeek = daysOfWeek[6],
      daysOfWeek = daysOfWeek
    )
  }

  // TODO Unit test
  fun getDateDisplayName(specificDateStr: String): String {
    val localDate = LocalDate.parse(specificDateStr)
    val pattern = if (Locale.getDefault() == Locale.US) {
      commonDayOfWeekPatternWithUS
    } else {
      commonDayOfWeekPatternWithZH
    }
    return localDate.format(pattern)
  }
}