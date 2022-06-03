package br.com.bonaldi.customcalendar.helpers

import br.com.bonaldi.customcalendar.helpers.IntHelper.orZero
import br.com.bonaldi.customcalendar.models.day.CalendarDay
import java.util.*

object DateHelper {

    fun Calendar.toCalendarDayInfo(): CalendarDay{
        return CalendarDay(
            day = get(Calendar.DAY_OF_MONTH),
            month = get(Calendar.MONTH),
            year = get(Calendar.YEAR),
        )
    }

    fun getFirstDateOfMonth(month: Int, year: Int): Calendar {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        return calendar
    }

    fun getTodayDate(): CalendarDay {
        val calendar = Calendar.getInstance()
        return CalendarDay(
            day = calendar.get(Calendar.DAY_OF_MONTH),
            month = calendar.get(Calendar.MONTH),
            year = calendar.get(Calendar.YEAR),
        )
    }

    fun CalendarDay.toCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(this.year.orZero(), this.month.orZero(), this.day.orZero())
        return calendar
    }

    fun CalendarDay.isToday(): Boolean {
        val calendar = Calendar.getInstance()
        return (this.year == calendar.get(Calendar.YEAR) && this.month == calendar.get(Calendar.MONTH) && this.day == calendar.get(Calendar.DAY_OF_MONTH))
    }
}