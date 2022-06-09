package br.com.bonaldi.customcalendar

import br.com.bonaldi.customcalendar.helpers.DateHelper
import br.com.bonaldi.customcalendar.models.day.CalendarDay
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum

data class CalendarParams(
    var typeParams: TypeParams = TypeParams(),
    var dateParams: DateParams = DateParams(),
    var preConfigParam: PreConfigParams = PreConfigParams(),
)

data class TypeParams(
    var selectionType: CalendarSelectionTypeEnum = CalendarSelectionTypeEnum.SINGLE,
    var maxMultiSelectionDates: Int? = null
)

data class DateParams(
    var currentDate: CalendarDay = DateHelper.getTodayDate(),
    var minDate: CalendarDay = DateHelper.getTodayDate(),
    var maxDate: CalendarDay? = null,
)

data class PreConfigParams(
    var selectedDate: CalendarDay? = null,
    var selectedDates: MutableList<CalendarDay> = mutableListOf(),
)
