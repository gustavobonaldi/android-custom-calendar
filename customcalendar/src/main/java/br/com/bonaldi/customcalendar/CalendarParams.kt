package br.com.bonaldi.customcalendar

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import br.com.bonaldi.customcalendar.helpers.DateHelper
import br.com.bonaldi.customcalendar.models.day.CalendarDay
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum

data class CalendarParams(
    var typeParams: TypeParams = TypeParams(),
    var dateParams: DateParams = DateParams(),
    var preConfigParam: PreConfigParams = PreConfigParams(),
    var colorParams: ColorParams = ColorParams()
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
