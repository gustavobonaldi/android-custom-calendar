package br.com.bonaldi.customcalendar.listeners

import br.com.bonaldi.customcalendar.models.day.CalendarDayInfo

interface CalendarAdapterListener {
    fun onSelectDates(list: List<CalendarDayInfo>)
    fun onSelectDate(date: CalendarDayInfo)
}