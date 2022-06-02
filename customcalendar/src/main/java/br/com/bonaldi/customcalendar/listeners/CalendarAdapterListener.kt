package br.com.bonaldi.customcalendar.listeners

import br.com.bonaldi.customcalendar.models.day.CalendarDayInfo
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum

interface CalendarAdapterListener {
    fun onSelectDates(list: List<CalendarDayInfo>)
    fun onSelectDate(date: CalendarDayInfo)
    fun getCalendarSelectionType(): CalendarSelectionTypeEnum
}