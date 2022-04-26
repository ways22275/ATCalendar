package com.example.amazingtalkcalendar.data.reposiroty

import com.example.amazingtalkcalendar.data.Resource
import com.example.amazingtalkcalendar.network.Service
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepository @Inject constructor(
  private val service: Service
) {

  suspend fun fetchSchedules(teacherName: String, startAt: String) = flow {
    emit(Resource.loading())
    val response = service.fetchSchedules(teacherName, startAt)
    if (!response.isSuccessful) {
      emit(Resource.error(response.errorBody().toString()))
    } else {
      emit(Resource.success(response.body()))
    }
  }
}