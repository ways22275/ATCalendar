package com.example.amazingtalkcalendar.network

import com.example.amazingtalkcalendar.data.model.ScheduleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {

  @GET("teachers/{teacherName}/schedule")
  suspend fun fetchSchedules(
    @Path("teacherName") teacherName: String,
    @Query("started_at") startedAt: String
  ): Response<ScheduleResponse>
}