package com.example.amazingtalkcalendar.data.model

data class ScheduleTitleDto(
  val year: String,
  val firstDayOfWeek: String,
  val lastDayOfWeek: String,
  val daysOfWeek: List<String>
)
