package com.example.amazingtalkcalendar.data

data class Resource<T> (
  val status: Status,
  val data: T? = null,
  val message: String? = null
) {
  companion object {

    fun <T> loading(): Resource<T> {
      return Resource(status = Status.LOADING)
    }

    fun <T> success(data: T?): Resource<T> {
      return Resource(status = Status.SUCCESS, data = data)
    }

    fun <T> error(message: String?): Resource<T> {
      return Resource(status = Status.ERROR, message = message)
    }
  }
}

enum class Status {
  SUCCESS, ERROR, LOADING
}