package br.com.bonaldi.customcalendar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.bonaldi.customcalendar.R
import br.com.bonaldi.customcalendar.databinding.CalendarDayItemBinding
import br.com.bonaldi.customcalendar.helpers.DateHelper.calendarToCalendarDayInfo
import br.com.bonaldi.customcalendar.helpers.DateHelper.getFirstDateOfMonth
import br.com.bonaldi.customcalendar.helpers.IntHelper.orZero
import br.com.bonaldi.customcalendar.models.day.CalendarDayListItem
import br.com.bonaldi.customcalendar.models.day.CalendarMonthViewType
import java.util.*

class MonthAdapter : ListAdapter<CalendarDayListItem, RecyclerView.ViewHolder>(MonthAdapterDiffer) {
    private var calendar: Calendar? = null
    private val dayList = mutableListOf<CalendarDayListItem>(
        CalendarDayListItem.CalendarWeekDay("D"),
        CalendarDayListItem.CalendarWeekDay("S"),
        CalendarDayListItem.CalendarWeekDay("T"),
        CalendarDayListItem.CalendarWeekDay("Q"),
        CalendarDayListItem.CalendarWeekDay("Q"),
        CalendarDayListItem.CalendarWeekDay("S"),
        CalendarDayListItem.CalendarWeekDay("S"),
    )

    fun setMonthItem(month: Int) {
        calendar = getFirstDateOfMonth(month)
        calendar?.let {
            mapDaysFromMonthAndAdd(it)
        }
        submitList(dayList)
    }

    private fun mapDaysFromMonthAndAdd(calendar: Calendar) {
        val currentDate = calendar.get(Calendar.DAY_OF_MONTH)
        val maxMonthDate = calendar.getMaximum(Calendar.DAY_OF_MONTH)
        addEmptyDatesRecursive(calendar)
        for (i in currentDate..maxMonthDate) {
            dayList.add(
                CalendarDayListItem.CalendarDay(
                    calendarToCalendarDayInfo(calendar),
                    isSelected = false
                )
            )
            calendar.add(Calendar.DATE, 1)
        }
    }

    private fun addEmptyDatesRecursive(calendar: Calendar) {
        val currentMonthDay = calendar.get(Calendar.DAY_OF_MONTH)
        val calendarStartWeekDay = calendar.get(Calendar.DAY_OF_WEEK)
        val addedDays: Int =
            dayList.count { it.viewType == CalendarMonthViewType.CALENDAR_EMPTY_DAY }
        if ((currentMonthDay + addedDays) < calendarStartWeekDay) {
            dayList.add(CalendarDayListItem.CalendarEmptyWeekDay())
            addEmptyDatesRecursive(calendar)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return currentList.getOrNull(position)?.viewType?.ordinal.orZero()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CalendarMonthViewType.CALENDAR_WEEK_DAY.ordinal -> CalendarWeekDayViewHolder(
                CalendarDayItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> CalendarDayViewHolder(
                CalendarDayItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        currentList.getOrNull(position)?.let { day ->
            when (holder) {
                is CalendarWeekDayViewHolder -> holder.bindItem(day, position)
                is CalendarDayViewHolder -> holder.bindItem(day, position)
                else -> {}
            }
        }
    }

    inner class CalendarWeekDayViewHolder(private val binding: CalendarDayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(day: CalendarDayListItem, position: Int) = binding.apply {
            (day as? CalendarDayListItem.CalendarWeekDay)?.let { weekDay ->
                tvCalendarDayItem.apply {
                    text = weekDay.name
                    setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.black))
                    setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                }
            }
        }
    }

    inner class CalendarDayViewHolder(private val binding: CalendarDayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(day: CalendarDayListItem, position: Int) = binding.apply {
            (day as? CalendarDayListItem.CalendarDay)?.let { calendarDay ->
                calendarDay.dayInfo.day?.let {
                    tvCalendarDayItem.text = it.toString()
                } ?: kotlin.run {
                    tvCalendarDayItem.text = 31.toString()
                    tvCalendarDayItem.visibility = View.INVISIBLE
                }
            }
        }
    }

    object MonthAdapterDiffer : DiffUtil.ItemCallback<CalendarDayListItem>() {
        override fun areItemsTheSame(
            oldItem: CalendarDayListItem,
            newItem: CalendarDayListItem
        ): Boolean {
            return when (oldItem) {
                is CalendarDayListItem.CalendarWeekDay -> (oldItem as? CalendarDayListItem.CalendarWeekDay)?.name == (newItem as? CalendarDayListItem.CalendarWeekDay)?.name
                is CalendarDayListItem.CalendarDay -> (oldItem as? CalendarDayListItem.CalendarDay)?.dayInfo?.timeInMillis == (newItem as? CalendarDayListItem.CalendarDay)?.dayInfo?.timeInMillis && (oldItem as? CalendarDayListItem.CalendarDay)?.isSelected == (newItem as? CalendarDayListItem.CalendarDay)?.isSelected
                else -> oldItem == newItem
            }
        }

        override fun areContentsTheSame(
            oldItem: CalendarDayListItem,
            newItem: CalendarDayListItem
        ): Boolean {
            return when (oldItem) {
                is CalendarDayListItem.CalendarWeekDay -> (oldItem as? CalendarDayListItem.CalendarWeekDay) == (newItem as? CalendarDayListItem.CalendarWeekDay)
                is CalendarDayListItem.CalendarDay -> (oldItem as? CalendarDayListItem.CalendarDay) == (newItem as? CalendarDayListItem.CalendarDay)
                else -> oldItem == newItem
            }
        }
    }
}