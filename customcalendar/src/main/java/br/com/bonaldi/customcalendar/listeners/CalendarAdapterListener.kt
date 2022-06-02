package br.com.bonaldi.customcalendar.listeners

import br.com.bonaldi.customcalendar.CalendarParams
import br.com.bonaldi.customcalendar.models.day.CalendarDayInfo
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum

interface CalendarAdapterListener: OnCalendarChangedListener {
    fun getCalendarSelectionType(): CalendarSelectionTypeEnum
    fun getMaxMultiSelectionDates(): Int?
    fun getCalendarParams(): CalendarParams
    fun getMaxDate(): CalendarDayInfo?
    fun getMinDate(): CalendarDayInfo
}