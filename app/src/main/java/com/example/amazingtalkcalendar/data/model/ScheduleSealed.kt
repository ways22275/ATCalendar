package com.example.amazingtalkcalendar.data.model

data class ScheduleItem(
  val startAt: String,
  val endAt: String,
  val available: Boolean
): ScheduleSealed()

data class ScheduleDividerItem(
  val displayText: String
): ScheduleSealed()

sealed class ScheduleSealed
