package br.com.bonaldi.customcalendar.listeners

import br.com.bonaldi.customcalendar.models.day.CalendarDay

interface OnCalendarChangedListener {
    fun onSelectDates(list: List<CalendarDay>)
    fun onSelectDate(date: CalendarDay?)
    fun onMaxSelectionReach(selectedQuantity: Int)
}