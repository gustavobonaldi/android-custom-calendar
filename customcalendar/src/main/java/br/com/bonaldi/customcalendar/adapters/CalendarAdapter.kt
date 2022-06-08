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
import br.com.bonaldi.customcalendar.helpers.DateHelper.isToday
import br.com.bonaldi.customcalendar.helpers.DateHelper.toCalendar
import br.com.bonaldi.customcalendar.helpers.DateHelper.toCalendarDayInfo
import br.com.bonaldi.customcalendar.helpers.IntHelper.orZero
import br.com.bonaldi.customcalendar.listeners.CalendarAdapterListener
import br.com.bonaldi.customcalendar.models.day.CalendarDay
import br.com.bonaldi.customcalendar.models.day.CalendarDayListItem
import br.com.bonaldi.customcalendar.models.day.CalendarDayListItem.CalendarViewType
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(private val listener: CalendarAdapterListener) : ListAdapter<CalendarDayListItem, RecyclerView.ViewHolder>(MonthAdapterDiffer) {
    private val weekDaysList = DateFormatSymbols.getInstance(Locale.getDefault()).weekdays
    private var calendarDaysList = mutableListOf<CalendarDayListItem>()
    private var startRangeSelection: Pair<Int, CalendarDayListItem>? = null
    private var endRangeSelection: Pair<Int, CalendarDayListItem>? = null
    private var selectedDaysHashMap: SortedMap<Int, CalendarDayListItem> = sortedMapOf()
    private var shouldBlockNewItems = false
    private var emptyStateCount: Int = 0

    private val weekDaysShortNameList: List<CalendarDayListItem> by lazy {
        listOf<CalendarDayListItem>(
            CalendarDayListItem.CalendarWeekDayItem(weekDaysList[1][0].toString()),
            CalendarDayListItem.CalendarWeekDayItem(weekDaysList[2][0].toString()),
            CalendarDayListItem.CalendarWeekDayItem(weekDaysList[3][0].toString()),
            CalendarDayListItem.CalendarWeekDayItem(weekDaysList[4][0].toString()),
            CalendarDayListItem.CalendarWeekDayItem(weekDaysList[5][0].toString()),
            CalendarDayListItem.CalendarWeekDayItem(weekDaysList[6][0].toString()),
            CalendarDayListItem.CalendarWeekDayItem(weekDaysList[7][0].toString())
        )
    }

    private val selectedDates: List<CalendarDayListItem>
        get() = selectedDaysHashMap.map { it.value }

    private val selectedDate: CalendarDayListItem
        get() = selectedDaysHashMap.firstNotNullOf { it.value }

    fun refreshCalendar() {
        if(!shouldBlockNewItems) {
            shouldBlockNewItems = true
            val minDate = listener.getMinDate().toCalendar()
            val maxDate =
                listener.getMaxDate()?.toCalendar() ?: minDate.toCalendarDayInfo().toCalendar()
                    .apply {
                        add(Calendar.YEAR, 1)
                    }
            val currentDateToAdd = minDate
            while (currentDateToAdd.get(Calendar.MONTH) != maxDate.get(Calendar.MONTH) || currentDateToAdd.get(
                    Calendar.YEAR
                ) != maxDate.get(Calendar.YEAR)
            ) {
                setupMonth(
                    currentDateToAdd.get(Calendar.MONTH),
                    currentDateToAdd.get(Calendar.YEAR)
                )
                currentDateToAdd.add(Calendar.MONTH, 1)
            }
            setupMonth(currentDateToAdd.get(Calendar.MONTH), currentDateToAdd.get(Calendar.YEAR))
            submitList(calendarDaysList) {
                calendarDaysList = currentList
                shouldBlockNewItems = false
            }
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
        calendarDaysList.addAll(weekDaysShortNameList)
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
        return SimpleDateFormat("yyyy MMMM", Locale.getDefault()).format(calendar.time)
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
                setStyle(day.isSelected, calendarDay.dayInfo)
                calendarDay.dayInfo.day?.let {
                    text = it.toString()
                }
                updateSelectedMap(calendarDay, position)
                emitMultipleDateSelection()
                itemView.setOnClickListener {
                    handleDateSelection(day, !day.isSelected, position) { shouldUpdateItem ->
                        if(shouldUpdateItem) {
                            day.isSelected = !day.isSelected
                            notifyItemChanged(position)
                        }
                    }
                }
            }
        }

        private fun updateSelectedMap(calendarDay: CalendarDayListItem.CalendarDayItem, position: Int){
            when {
                calendarDay.isSelected -> {
                    selectedDaysHashMap[position] = calendarDay
                }
                else -> {
                    selectedDaysHashMap.remove(position)
                }
            }
        }

        private fun setStyle(isSelected: Boolean, calendarDay: CalendarDay) = binding.tvCalendarDayItem.apply {
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
                when {
                    calendarDay.isToday() -> {
                        binding.tvCalendarDayItem.setCompoundDrawablesWithIntrinsicBounds(null, null, null, ContextCompat.getDrawable(context, R.drawable.ic_current_date_point))
                    }
                    else -> {
                        binding.tvCalendarDayItem.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    }
                }
            }
        }

        private fun emitMultipleDateSelection(){
            when (listener.getCalendarSelectionType()) {
                CalendarSelectionTypeEnum.MULTIPLE, CalendarSelectionTypeEnum.RANGE -> {
                    val selectedDateList = selectedDates.mapNotNull { (it as? CalendarDayListItem.CalendarDayItem)?.dayInfo  }
                    listener.onSelectDates(selectedDateList)
                }
            }
        }

        private fun handleDateSelection(
            day: CalendarDayListItem.CalendarDayItem,
            isSelected: Boolean,
            position: Int,
            onUpdateCurrentItem: (shouldUpdateItem: Boolean) -> Unit
        ) {
            when (listener.getCalendarSelectionType()) {
                CalendarSelectionTypeEnum.SINGLE -> {
                    when {
                        isSelected -> {
                            onUpdateCurrentItem.invoke(true)
                            unSelectAllItems(position)
                            listener.onSelectDate(day.dayInfo)
                        }
                        else -> {
                            listener.onSelectDate(null)
                            onUpdateCurrentItem.invoke(true)
                        }
                    }
                }
                CalendarSelectionTypeEnum.MULTIPLE -> {
                    val selectedDateList = selectedDates.mapNotNull { (it as? CalendarDayListItem.CalendarDayItem)?.dayInfo }
                    listener.getMaxMultiSelectionDates()?.let { maxSelection ->
                        val currentSelectedQuantity = selectedDateList.count()
                        when {
                            currentSelectedQuantity < maxSelection || !isSelected -> {
                                onUpdateCurrentItem(true)
                            }
                            else -> listener.onMaxSelectionReach(currentSelectedQuantity)
                        }
                    } ?: run { onUpdateCurrentItem.invoke(true) }
                }
                CalendarSelectionTypeEnum.RANGE -> {
                    when {
                        isSelected -> {
                            when {
                                startRangeSelection == null -> {
                                    unSelectAllItems()
                                    startRangeSelection = position to day
                                    endRangeSelection = null
                                }
                                startRangeSelection?.first.orZero() > position -> {
                                    unSelectAllItems()
                                    startRangeSelection = position to day
                                    endRangeSelection = null
                                }
                                startRangeSelection != null && endRangeSelection != null -> {
                                    if(position <= startRangeSelection?.first.orZero()) {
                                        startRangeSelection = null
                                        endRangeSelection = null
                                        unSelectAllItems()
                                    }
                                    else if(position > startRangeSelection?.first.orZero()){
                                        endRangeSelection = position to day
                                    }
                                }
                                else -> {
                                    endRangeSelection = position to day
                                }
                            }
                        }
                        else -> {
                            startRangeSelection = null
                            endRangeSelection = null
                            unSelectAllItems()
                        }
                    }
                    startRangeSelection?.let { start ->
                        endRangeSelection?.let { end ->
                            selectRangePosition(start.first, end.first)
                        }
                    }
                    onUpdateCurrentItem.invoke(startRangeSelection == null || endRangeSelection == null)
                }
            }
        }

        private fun selectRangePosition(start: Int, end: Int){
            val currentItemList = currentList.toMutableList()
            currentItemList.mapIndexed { index, calendarDayListItem ->
                when {
                    calendarDayListItem.isSelected && index < start && index > end -> {
                        (calendarDayListItem as? CalendarDayListItem.CalendarDayItem)?.let { calendarDay ->
                            currentItemList[index] = calendarDay.copy(isSelected = false)
                        }
                    }
                    !calendarDayListItem.isSelected && index >= start && index <= end -> {
                        (calendarDayListItem as? CalendarDayListItem.CalendarDayItem)?.let { calendarDay ->
                            currentItemList[index] = calendarDay.copy(isSelected = true)
                        }
                    }
                    else ->{}
                }
            }
            submitList(currentItemList.toList())
        }

        private fun unSelectAllItems(indexException: Int? = null){
            val currentItemList = currentList.toMutableList()
            selectedDaysHashMap.keys.map { index ->
                if(index != indexException) {
                    (currentItemList.getOrNull(index) as? CalendarDayListItem.CalendarDayItem)?.let { calendarDay ->
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
                is CalendarDayListItem.CalendarDayItem -> (oldItem as? CalendarDayListItem.CalendarDayItem)?.dayInfo == (newItem as? CalendarDayListItem.CalendarDayItem)?.dayInfo
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