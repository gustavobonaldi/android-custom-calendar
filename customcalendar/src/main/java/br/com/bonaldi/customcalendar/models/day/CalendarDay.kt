package br.com.bonaldi.customcalendar.models.day

import br.com.bonaldi.customcalendar.helpers.DateHelper.toCalendar
import java.text.SimpleDateFormat
import java.util.*

data class CalendarDay(
    var day: Int?,
    var month: Int?,
    var year: Int?
) {
    override fun toString(): String {
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return format.format(this.toCalendar().time)
    }
}