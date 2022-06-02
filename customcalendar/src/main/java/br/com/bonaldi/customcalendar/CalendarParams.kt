package br.com.bonaldi.customcalendar

import br.com.bonaldi.customcalendar.helpers.DateHelper
import br.com.bonaldi.customcalendar.models.day.CalendarDayInfo
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum
import br.com.bonaldi.customcalendar.models.enums.CalendarViewTypeEnum

data class CalendarParams(
    var typeParams: TypeParams = TypeParams(),
    var dateParams: DateParams = DateParams(),
    var preConfigParam: PreConfigParams = PreConfigParams()
)

data class TypeParams(
    var selectionType: CalendarSelectionTypeEnum = CalendarSelectionTypeEnum.SINGLE,
    var calendarViewType: CalendarViewTypeEnum = CalendarViewTypeEnum.LIST,
    var maxMultiSelectionDates: Int? = null
)

data class DateParams(
    var currentDate: CalendarDayInfo? = null,
    var minDate: CalendarDayInfo = DateHelper.getTodayDate(),
    var maxDate: CalendarDayInfo? = null,
)

data class PreConfigParams(
    var selectedDate: CalendarDayInfo? = null,
    var selectedDates: MutableList<CalendarDayInfo> = mutableListOf(),
)

data class ColorParams(
    val weekDayBackgroundColor: Int? = null,
    val weekDayTextColor: Int? = null,
    val dayBackgroundColor: Int? = null,
    val dayTextColor: Int? = null,
    val monthBackgroundColor: Int? = null,
    val monthTextColor: Int? = null
)
