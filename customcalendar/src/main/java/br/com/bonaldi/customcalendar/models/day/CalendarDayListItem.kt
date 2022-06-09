package br.com.bonaldi.customcalendar.models.day


sealed class CalendarDayListItem {
    abstract val viewType: CalendarViewType
    abstract var isSelected: Boolean

    fun isSelectableCalendarDayItem() = viewType == CalendarViewType.CALENDAR_DAY

    data class CalendarMonthHeaderItem(
        val name: String,
        override val viewType: CalendarViewType = CalendarViewType.CALENDAR_MONTH_NAME,
        override var isSelected: Boolean = false
    ): CalendarDayListItem()

    data class CalendarDayItem(
        val dayInfo: CalendarDay,
        val isEnabled: Boolean = true,
        override var isSelected: Boolean = false,
        override val viewType: CalendarViewType = CalendarViewType.CALENDAR_DAY
    ) : CalendarDayListItem()

    data class CalendarWeekDayItem(
        val name: String,
        override val viewType: CalendarViewType = CalendarViewType.CALENDAR_WEEK_DAY,
        override var isSelected: Boolean = false
    ) : CalendarDayListItem()

    data class CalendarEmptyWeekDayItem(
        override val viewType: CalendarViewType = CalendarViewType.CALENDAR_EMPTY_DAY,
        override var isSelected: Boolean = false
    ) : CalendarDayListItem()

    enum class CalendarViewType {
        CALENDAR_MONTH_NAME,
        CALENDAR_DAY,
        CALENDAR_WEEK_DAY,
        CALENDAR_EMPTY_DAY
    }
}
