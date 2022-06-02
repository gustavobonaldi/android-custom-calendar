package br.com.bonaldi.customcalendar.models.day

data class CalendarDay(
    var day: Int?,
    var month: Int?,
    var year: Int?,
    var timeInMillis: Long?
)