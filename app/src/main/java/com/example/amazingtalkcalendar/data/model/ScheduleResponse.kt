package com.example.amazingtalkcalendar.data.model

data class ScheduleResponse(
  val available: List<Schedule>,
  val booked: List<Schedule>
)
