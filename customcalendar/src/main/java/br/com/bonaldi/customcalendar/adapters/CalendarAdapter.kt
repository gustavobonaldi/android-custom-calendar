package br.com.bonaldi.customcalendar.adapters

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.bonaldi.customcalendar.R
import br.com.bonaldi.customcalendar.databinding.CalendarDayItemBinding
import br.com.bonaldi.customcalendar.databinding.CalendarDayItemEmptyBinding
import br.com.bonaldi.customcalendar.databinding.CalendarMonthItemBinding
import br.com.bonaldi.customcalendar.databinding.CalendarWeekDayItemBinding
import br.com.bonaldi.customcalendar.helpers.DateHelper.getFirstDateOfMonth
import br.com.bonaldi.customcalendar.helpers.DateHelper.toCalendar
import br.com.bonaldi.customcalendar.helpers.DateHelper.toCalendarDayInfo
import br.com.bonaldi.customcalendar.helpers.IntHelper.orZero
import br.com.bonaldi.customcalendar.listeners.CalendarAdapterListener
import br.com.bonaldi.customcalendar.models.day.CalendarDayListItem
import br.com.bonaldi.customcalendar.models.day.CalendarDayListItem.CalendarViewType
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(private val listener: CalendarAdapterListener) : ListAdapter<CalendarDayListItem, RecyclerView.ViewHolder>(MonthAdapterDiffer) {
    private val weekDaysList = mutableListOf<CalendarDayListItem>(
        CalendarDayListItem.CalendarWeekDayItem("D"),
        CalendarDayListItem.CalendarWeekDayItem("S"),
        CalendarDayListItem.CalendarWeekDayItem("T"),
        CalendarDayListItem.CalendarWeekDayItem("Q"),
        CalendarDayListItem.CalendarWeekDayItem("Q"),
        CalendarDayListItem.CalendarWeekDayItem("S"),
        CalendarDayListItem.CalendarWeekDayItem("S"),
    )
    private var calendarDaysList = mutableListOf<CalendarDayListItem>()
    private var emptyStateCount: Int = 0

    private val selectedDates: List<CalendarDayListItem>
        get() = currentList.filter { it.isSelected }

    private val selectedDate: CalendarDayListItem?
        get() = currentList.firstOrNull { it.isSelected }

    fun refreshCalendar() {
        val minDate = listener.getMinDate().toCalendar()
        val maxDate = listener.getMaxDate()?.toCalendar() ?: minDate.toCalendarDayInfo().toCalendar().apply {
            add(Calendar.YEAR, 1)
        }
        val currentDateToAdd = minDate
        while (currentDateToAdd.get(Calendar.MONTH) != maxDate.get(Calendar.MONTH) || currentDateToAdd.get(Calendar.YEAR) != maxDate.get(Calendar.YEAR)){
            setupMonth(currentDateToAdd.get(Calendar.MONTH), currentDateToAdd.get(Calendar.YEAR))
            currentDateToAdd.add(Calendar.MONTH, 1)
        }
        setupMonth(currentDateToAdd.get(Calendar.MONTH), currentDateToAdd.get(Calendar.YEAR))
        submitList(calendarDaysList){
            calendarDaysList = currentList
        }
    }

    private fun setupMonth(month: Int, year: Int){
        val calendar = getFirstDateOfMonth(month, year)
        emptyStateCount = 0
        calendarDaysList.add(CalendarDayListItem.CalendarMonthHeaderItem(getCurrentMonthName(month, year)))
        mapDaysFromMonthAndAdd(calendar)
    }

    private fun mapDaysFromMonthAndAdd(calendar: Calendar) {
        val currentDate = calendar.get(Calendar.DAY_OF_MONTH)
        val maxMonthDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendarDaysList.addAll(weekDaysList)
        addEmptyDatesRecursive(calendar)
        for (i in currentDate..maxMonthDate) {
            calendarDaysList.add(
                CalendarDayListItem.CalendarDayItem(
                    calendar.toCalendarDayInfo(),
                    isSelected = false
                )
            )
            calendar.add(Calendar.DATE, 1)
        }
        addFinalEmptyDatesRecursive(calendar)
    }

    private fun getCurrentMonthName(month: Int, year: Int): String{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        return SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(calendar.time)
    }

    private fun addEmptyDatesRecursive(calendar: Calendar) {
        val currentMonthDay = calendar.get(Calendar.DAY_OF_MONTH)
        val calendarStartWeekDay = calendar.get(Calendar.DAY_OF_WEEK)
        if ((currentMonthDay + emptyStateCount) < calendarStartWeekDay) {
            calendarDaysList.add(CalendarDayListItem.CalendarEmptyWeekDayItem())
            emptyStateCount++
            addEmptyDatesRecursive(calendar)
        }
    }

    private fun addFinalEmptyDatesRecursive(calendar: Calendar) {
        val lastPosition = calendarDaysList.filter { it.viewType != CalendarViewType.CALENDAR_MONTH_NAME }.lastIndex
        if(lastPosition % 7 != 0){
            calendarDaysList.add(CalendarDayListItem.CalendarEmptyWeekDayItem())
            emptyStateCount++
            addFinalEmptyDatesRecursive(calendar)
        } else {
            calendarDaysList.add(CalendarDayListItem.CalendarEmptyWeekDayItem())
            emptyStateCount++
        }
    }

    override fun getItemViewType(position: Int): Int {
        return currentList.getOrNull(position)?.viewType?.ordinal.orZero()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CalendarViewType.CALENDAR_WEEK_DAY.ordinal -> CalendarWeekDayViewHolder(
                CalendarWeekDayItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            CalendarViewType.CALENDAR_MONTH_NAME.ordinal -> CalendarMonthNameViewHolder(
                CalendarMonthItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            CalendarViewType.CALENDAR_EMPTY_DAY.ordinal -> EmptyStateViewHolder(
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
            setStyle()
            (day as? CalendarDayListItem.CalendarMonthHeaderItem)?.let { weekDay ->
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

        private fun setStyle() = binding.apply {
            listener.getCalendarParams().colorParams.apply {
                monthTextColor?.let {
                    tvMonthName.setTextColor(it)
                }
                monthBackgroundColor?.let {
                    tvMonthName.background?.colorFilter = PorterDuffColorFilter(
                        it,
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
            }
        }
    }

    inner class CalendarWeekDayViewHolder(private val binding: CalendarWeekDayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(day: CalendarDayListItem, position: Int) = binding.apply {
            setStyle()
            (day as? CalendarDayListItem.CalendarWeekDayItem)?.let { weekDay ->
                tvCalendarDayItem.apply {
                    text = weekDay.name
                }
            }
        }

        private fun setStyle() = binding.apply {
            listener.getCalendarParams().colorParams.apply {
                weekDayTextColor?.let {
                    tvCalendarDayItem.setTextColor(it)
                }
                weekDayBackgroundColor?.let {
                    tvCalendarDayItem.background?.colorFilter = PorterDuffColorFilter(
                        it,
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
            }
        }
    }

    inner class CalendarDayViewHolder(private val binding: CalendarDayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(day: CalendarDayListItem, position: Int) = binding.tvCalendarDayItem.apply {
            (day as? CalendarDayListItem.CalendarDayItem)?.let { calendarDay ->
                calendarDay.dayInfo.day?.let {
                    text = it.toString()
                }
                setStyle(day.isSelected)
                itemView.setOnClickListener {
                    handleDateSelection(day, !day.isSelected) {
                        day.isSelected = !day.isSelected
                        notifyItemChanged(position)
                    }
                }
            }
        }

        private fun setStyle(isSelected: Boolean) = binding.tvCalendarDayItem.apply {
            listener.getCalendarParams().colorParams.apply {
                when {
                    isSelected -> {
                        selectedDayTextColor?.let {
                            setTextColor(it)
                        } ?: kotlin.run {
                            setTextColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.white_app
                                )
                            )
                        }
                        selectedDayBackgroundColor?.let {
                            background?.colorFilter = PorterDuffColorFilter(
                                it,
                                PorterDuff.Mode.SRC_ATOP
                            )
                        } ?: kotlin.run {
                            setBackgroundColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.black
                                )
                            )
                        }
                    }
                    else -> {
                        dayTextColor?.let {
                            setTextColor(it)
                        } ?: kotlin.run {
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                        }
                        dayBackgroundColor?.let {
                            background?.colorFilter = PorterDuffColorFilter(
                                it,
                                PorterDuff.Mode.SRC_ATOP
                            )
                        } ?: kotlin.run {
                            setBackgroundColor(
                                ContextCompat.getColor(
                                    itemView.context,
                                    R.color.calendar_day_color
                                )
                            )
                        }
                    }
                }
            }
        }

        private fun setDateStyleBySelection(day: CalendarDayListItem.CalendarDayItem) = binding.tvCalendarDayItem.apply{
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

        private fun handleDateSelection(
            day: CalendarDayListItem.CalendarDayItem,
            isSelected: Boolean,
            onUpdateCurrentItem: () -> Unit
        ) {
            when (listener.getCalendarSelectionType()) {
                CalendarSelectionTypeEnum.SINGLE -> {
                    if (isSelected) {
                        unSelectAllItems()
                        listener.onSelectDate(day.dayInfo)
                    }
                    onUpdateCurrentItem.invoke()
                }
                CalendarSelectionTypeEnum.MULTIPLE -> {
                    listener.getMaxMultiSelectionDates()?.let { maxSelection ->
                        val selectedDateList = selectedDates.mapNotNull { (it as? CalendarDayListItem.CalendarDayItem)?.dayInfo }
                        val currentSelectedQuantity = selectedDateList.count()
                        when {
                            currentSelectedQuantity < maxSelection || !isSelected -> {
                                onUpdateCurrentItem()
                                listener.onSelectDates(selectedDateList)
                            }
                            else -> listener.onMaxSelectionReach(currentSelectedQuantity)
                        }
                    } ?: run(onUpdateCurrentItem)
                }
                CalendarSelectionTypeEnum.RANGE -> {
                    //TODO: add implementation
                }
            }
        }

        private fun unSelectAllItems(){
            val currentItemList = currentList.toMutableList()
            currentItemList.mapIndexed { index, calendarDayListItem ->
                if(calendarDayListItem.isSelected){
                    (calendarDayListItem as? CalendarDayListItem.CalendarDayItem)?.let { calendarDay ->
                        currentItemList[index] = calendarDay.copy(isSelected = false)
                    }
                }
            }
            submitList(currentItemList.toList())
        }
    }

    inner class EmptyStateViewHolder(private val binding: CalendarDayItemEmptyBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem() = binding.viewEmptyState.apply {
            listener.getCalendarParams().colorParams.apply {
                dayBackgroundColor?.let {
                    background?.colorFilter = PorterDuffColorFilter(
                        it,
                        PorterDuff.Mode.SRC_ATOP
                    )
                } ?: kotlin.run {
                    setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.calendar_day_color))
                }
            }
        }
    }

    object MonthAdapterDiffer : DiffUtil.ItemCallback<CalendarDayListItem>() {
        override fun areItemsTheSame(
            oldItem: CalendarDayListItem,
            newItem: CalendarDayListItem
        ): Boolean {
            return  when (oldItem) {
                is CalendarDayListItem.CalendarWeekDayItem -> (oldItem as? CalendarDayListItem.CalendarWeekDayItem)?.name == (newItem as? CalendarDayListItem.CalendarWeekDayItem)?.name
                is CalendarDayListItem.CalendarDayItem -> (oldItem as? CalendarDayListItem.CalendarDayItem)?.dayInfo?.timeInMillis == (newItem as? CalendarDayListItem.CalendarDayItem)?.dayInfo?.timeInMillis
                is CalendarDayListItem.CalendarMonthHeaderItem -> (oldItem as? CalendarDayListItem.CalendarMonthHeaderItem)?.name == (newItem as? CalendarDayListItem.CalendarMonthHeaderItem)?.name
                else -> oldItem == newItem
            }
        }

        override fun areContentsTheSame(
            oldItem: CalendarDayListItem,
            newItem: CalendarDayListItem
        ): Boolean {
            return when (oldItem) {
                is CalendarDayListItem.CalendarWeekDayItem -> (oldItem as? CalendarDayListItem.CalendarWeekDayItem) == (newItem as? CalendarDayListItem.CalendarWeekDayItem)
                is CalendarDayListItem.CalendarDayItem -> (oldItem as? CalendarDayListItem.CalendarDayItem) == (newItem as? CalendarDayListItem.CalendarDayItem)
                is CalendarDayListItem.CalendarMonthHeaderItem -> (oldItem as? CalendarDayListItem.CalendarMonthHeaderItem) == (newItem as? CalendarDayListItem.CalendarMonthHeaderItem)
                else -> oldItem == newItem
            } && (oldItem.isSelected == newItem.isSelected)
        }
    }
}