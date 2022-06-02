package br.com.bonaldi.customcalendar.models.day


sealed class CalendarDayListItem {
    abstract val viewType: CalendarMonthViewType

    data class CalendarDay(
        val dayInfo: CalendarDayInfo,
        val isSelected: Boolean = false,
        override val viewType: CalendarMonthViewType = CalendarMonthViewType.CALENDAR_DAY
    ) : CalendarDayListItem()

    data class CalendarWeekDay(
        val name: String,
        override val viewType: CalendarMonthViewType = CalendarMonthViewType.CALENDAR_WEEK_DAY
    ) : CalendarDayListItem()

    data class CalendarEmptyWeekDay(
        override val viewType: CalendarMonthViewType = CalendarMonthViewType.CALENDAR_EMPTY_DAY
    ) : CalendarDayListItem()
}

enum class CalendarMonthViewType {
    CALENDAR_DAY,
    CALENDAR_WEEK_DAY,
    CALENDAR_EMPTY_DAY
}
