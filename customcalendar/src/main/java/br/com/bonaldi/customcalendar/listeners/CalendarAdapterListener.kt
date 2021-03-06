package br.com.bonaldi.customcalendar.listeners

import br.com.bonaldi.customcalendar.CalendarParams
import br.com.bonaldi.customcalendar.models.day.CalendarDay
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum

interface CalendarAdapterListener: OnCalendarChangedListener {
    fun getCalendarSelectionType(): CalendarSelectionTypeEnum
    fun getMaxMultiSelectionDates(): Int?
    fun getCalendarParams(): CalendarParams
    fun getMaxDate(): CalendarDay?
    fun getMinDate(): CalendarDay
    fun getDisabledDays(): List<CalendarDay>
}