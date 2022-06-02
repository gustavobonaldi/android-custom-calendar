package br.com.bonaldi.customcalendar.helpers

import br.com.bonaldi.customcalendar.helpers.IntHelper.orZero
import br.com.bonaldi.customcalendar.models.day.CalendarDayInfo
import java.util.*

object DateHelper {

    fun calendarToCalendarDayInfo(calendar: Calendar): CalendarDayInfo{
        return CalendarDayInfo(
            day = calendar.get(Calendar.DAY_OF_MONTH),
            month = calendar.get(Calendar.MONTH),
            year = calendar.get(Calendar.YEAR),
            timeInMillis = calendar.time.time
        )
    }

    fun getFirstDateOfMonth(month: Int): Calendar {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        return calendar
    }

    fun getTodayDate(): CalendarDayInfo {
        val calendar = Calendar.getInstance()
        return CalendarDayInfo(
            day = calendar.get(Calendar.DAY_OF_MONTH),
            month = calendar.get(Calendar.MONTH),
            year = calendar.get(Calendar.YEAR),
            timeInMillis = calendar.timeInMillis
        )
    }

    fun CalendarDayInfo.toCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(this.year.orZero(), this.month.orZero(), this.day.orZero())
        return calendar
    }
}