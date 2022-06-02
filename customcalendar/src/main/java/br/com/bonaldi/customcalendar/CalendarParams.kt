package br.com.bonaldi.customcalendar

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import br.com.bonaldi.customcalendar.helpers.DateHelper
import br.com.bonaldi.customcalendar.models.day.CalendarDayInfo
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum
import br.com.bonaldi.customcalendar.models.enums.CalendarViewTypeEnum

data class CalendarParams(
    var typeParams: TypeParams = TypeParams(),
    var dateParams: DateParams = DateParams(),
    var preConfigParam: PreConfigParams = PreConfigParams(),
    var colorParams: ColorParams = ColorParams()
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
    @ColorInt var weekDayBackgroundColor: Int? = null,
    var weekDayTextColor: ColorStateList? = null,
    @ColorInt var monthBackgroundColor: Int? = null,
    var monthTextColor: ColorStateList? = null,
    @ColorInt var dayBackgroundColor: Int? = null,
    var dayTextColor: ColorStateList? = null,
    @ColorInt var selectedDayBackgroundColor: Int? = null,
    var selectedDayTextColor: ColorStateList? = null,
)
