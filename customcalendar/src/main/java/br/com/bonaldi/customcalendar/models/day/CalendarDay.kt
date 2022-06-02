package br.com.bonaldi.customcalendar.models.day


sealed class CalendarDayListItem {
    abstract val viewType: CalendarMonthViewType
    abstract var isSelected: Boolean

    fun isSelectableCalendarDayItem() = viewType == CalendarMonthViewType.CALENDAR_DAY

    data class CalendarMonthName(
        val name: String,
        override val viewType: CalendarMonthViewType = CalendarMonthViewType.CALENDAR_MONTH_NAME,
        override var isSelected: Boolean = false
    ): CalendarDayListItem()

    data class CalendarDay(
        val dayInfo: CalendarDayInfo,
        override var isSelected: Boolean = false,
        override val viewType: CalendarMonthViewType = CalendarMonthViewType.CALENDAR_DAY
    ) : CalendarDayListItem()

    data class CalendarWeekDay(
        val name: String,
        override val viewType: CalendarMonthViewType = CalendarMonthViewType.CALENDAR_WEEK_DAY,
        override var isSelected: Boolean = false
    ) : CalendarDayListItem()

    data class CalendarEmptyWeekDay(
        override val viewType: CalendarMonthViewType = CalendarMonthViewType.CALENDAR_EMPTY_DAY,
        override var isSelected: Boolean = false
    ) : CalendarDayListItem()
}

enum class CalendarMonthViewType {
    CALENDAR_MONTH_NAME,
    CALENDAR_DAY,
    CALENDAR_WEEK_DAY,
    CALENDAR_EMPTY_DAY
}
