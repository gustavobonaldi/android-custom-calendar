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
import br.com.bonaldi.customcalendar.databinding.CalendarDayItemEmptyBinding
import br.com.bonaldi.customcalendar.databinding.CalendarMonthItemBinding
import br.com.bonaldi.customcalendar.helpers.DateHelper.calendarToCalendarDayInfo
import br.com.bonaldi.customcalendar.helpers.DateHelper.getFirstDateOfMonth
import br.com.bonaldi.customcalendar.helpers.IntHelper.orZero
import br.com.bonaldi.customcalendar.listeners.CalendarAdapterListener
import br.com.bonaldi.customcalendar.models.day.CalendarDayListItem
import br.com.bonaldi.customcalendar.models.day.CalendarMonthViewType
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(private val listener: CalendarAdapterListener) : ListAdapter<CalendarDayListItem, RecyclerView.ViewHolder>(MonthAdapterDiffer) {
    private val weekDaysList = mutableListOf<CalendarDayListItem>(
        CalendarDayListItem.CalendarWeekDay("D"),
        CalendarDayListItem.CalendarWeekDay("S"),
        CalendarDayListItem.CalendarWeekDay("T"),
        CalendarDayListItem.CalendarWeekDay("Q"),
        CalendarDayListItem.CalendarWeekDay("Q"),
        CalendarDayListItem.CalendarWeekDay("S"),
        CalendarDayListItem.CalendarWeekDay("S"),
    )
    private val calendarDaysList = mutableListOf<CalendarDayListItem>()
    private var emptyStateCount: Int = 0
    var selectionType: CalendarSelectionTypeEnum? = null

    fun setMonthItem(monthList: List<Int>) {
        monthList.map {
            setupMonth(it)
        }
        submitList(calendarDaysList)
    }

    private fun setupMonth(month: Int){
        val calendar = getFirstDateOfMonth(month)
        emptyStateCount = 0
        calendarDaysList.add(CalendarDayListItem.CalendarMonthName(getCurrentMonthName(month)))
        mapDaysFromMonthAndAdd(calendar)
    }

    private fun mapDaysFromMonthAndAdd(calendar: Calendar) {
        val currentDate = calendar.get(Calendar.DAY_OF_MONTH)
        val maxMonthDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendarDaysList.addAll(weekDaysList)
        addEmptyDatesRecursive(calendar)
        for (i in currentDate..maxMonthDate) {
            calendarDaysList.add(
                CalendarDayListItem.CalendarDay(
                    calendarToCalendarDayInfo(calendar),
                    isSelected = false
                )
            )
            calendar.add(Calendar.DATE, 1)
        }
        addFinalEmptyDatesRecursive(calendar)
    }

    private fun getCurrentMonthName(month: Int): String{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        return SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(calendar.time)
    }

    private fun addEmptyDatesRecursive(calendar: Calendar) {
        val currentMonthDay = calendar.get(Calendar.DAY_OF_MONTH)
        val calendarStartWeekDay = calendar.get(Calendar.DAY_OF_WEEK)
        if ((currentMonthDay + emptyStateCount) < calendarStartWeekDay) {
            calendarDaysList.add(CalendarDayListItem.CalendarEmptyWeekDay())
            emptyStateCount++
            addEmptyDatesRecursive(calendar)
        }
    }

    private fun addFinalEmptyDatesRecursive(calendar: Calendar) {
        val lastPosition = calendarDaysList.filter { it.viewType != CalendarMonthViewType.CALENDAR_MONTH_NAME }.lastIndex
        if(lastPosition % 7 != 0){
            calendarDaysList.add(CalendarDayListItem.CalendarEmptyWeekDay())
            emptyStateCount++
            addFinalEmptyDatesRecursive(calendar)
        } else {
            calendarDaysList.add(CalendarDayListItem.CalendarEmptyWeekDay())
            emptyStateCount++
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
            CalendarMonthViewType.CALENDAR_MONTH_NAME.ordinal -> CalendarMonthNameViewHolder(
                CalendarMonthItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            CalendarMonthViewType.CALENDAR_EMPTY_DAY.ordinal -> EmptyStateViewHolder(
                CalendarDayItemEmptyBinding.inflate(
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
                is CalendarMonthNameViewHolder -> holder.bindItem(day, position)
                is EmptyStateViewHolder -> holder.bindItem()
                else -> {}
            }
        }
    }

    inner class CalendarMonthNameViewHolder(private val binding: CalendarMonthItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem(day: CalendarDayListItem, position: Int) = binding.apply {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.calendar_day_color))
            (day as? CalendarDayListItem.CalendarMonthName)?.let { weekDay ->
                if(position != 0){
                    (tvMonthName.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                        this.topMargin = 40
                        tvMonthName.layoutParams = this
                    }
                }

                tvMonthName.apply {
                    text = weekDay.name
                }
            }
        }
    }

    inner class CalendarWeekDayViewHolder(private val binding: CalendarDayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(day: CalendarDayListItem, position: Int) = binding.apply {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white_app))
            (day as? CalendarDayListItem.CalendarWeekDay)?.let { weekDay ->
                tvCalendarDayItem.apply {
                    text = weekDay.name
                    setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.calendar_week_day_color))
                    setTextColor(ContextCompat.getColor(itemView.context, R.color.white_app))
                }
            }
        }
    }

    inner class CalendarDayViewHolder(private val binding: CalendarDayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(day: CalendarDayListItem, position: Int) = binding.tvCalendarDayItem.apply {
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white_app))
            (day as? CalendarDayListItem.CalendarDay)?.let { calendarDay ->
                calendarDay.dayInfo.day?.let {
                    text = it.toString()
                    setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.calendar_day_color))
                    setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                }

                itemView.setOnClickListener {
                    day.isSelected = !day.isSelected
                    when {
                        day.isSelected -> {
                            setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.black))
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.white_app))
                        }
                        else -> {
                            setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.calendar_day_color))
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                        }
                    }
                }
            }
        }
    }

    inner class EmptyStateViewHolder(private val binding: CalendarDayItemEmptyBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem() = binding.viewEmptyState.apply {
            setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.calendar_day_color))
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