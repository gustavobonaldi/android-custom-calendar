package br.com.bonaldi.customcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import br.com.bonaldi.customcalendar.adapters.CalendarAdapter
import br.com.bonaldi.customcalendar.databinding.CustomCalendarLayoutBinding
import br.com.bonaldi.customcalendar.helpers.DateHelper.getTodayDate
import br.com.bonaldi.customcalendar.listeners.CalendarAdapterListener
import br.com.bonaldi.customcalendar.listeners.OnCalendarChangedListener
import br.com.bonaldi.customcalendar.models.day.CalendarDayInfo
import br.com.bonaldi.customcalendar.models.day.CalendarMonthViewType
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum
import br.com.bonaldi.customcalendar.models.enums.CalendarViewTypeEnum

class CustomCalendar : ConstraintLayout {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        setAttributes(attrs)
    }

    private var selectionType: CalendarSelectionTypeEnum = CalendarSelectionTypeEnum.SINGLE
    private var calendarViewType: CalendarViewTypeEnum = CalendarViewTypeEnum.LIST
    private var selectedDate: CalendarDayInfo? = null
    private var selectedDates: MutableList<CalendarDayInfo> = mutableListOf()
    private var minDate: CalendarDayInfo = getTodayDate()
    private var maxDate: CalendarDayInfo? = null
    private var currentDate: CalendarDayInfo? = null
    private var maxMultiSelectionDates: Int? = null
    private var onCalendarChangedListener: OnCalendarChangedListener? = null


    private val binding = CustomCalendarLayoutBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    private val calendarAdapter: CalendarAdapter by lazy {
        CalendarAdapter(calendarListener)
    }

    private fun setAttributes(attrs: AttributeSet?){
        attrs?.let { attributeSet ->
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomCalendar, 0, 0)
            val calendarViewType = CalendarViewTypeEnum.values()[typedArray.getInt(
                R.styleable.CustomCalendar_calendarViewType,
                0
            )]

            val selectionType = CalendarSelectionTypeEnum.values()[typedArray.getInt(
                R.styleable.CustomCalendar_selectionType,
                0
            )]

            typedArray.getInt(R.styleable.CustomCalendar_maxMultiSelectionDates, 0).takeIf { it != 0 }?.let {
                setMaxMultiSelectionDates(it)
            }

            setCalendarViewType(calendarViewType)
            setCalendarSelectionType(selectionType)
            setupView()
            typedArray.recycle()
        }
    }

    fun setMinDate(calendarDay: CalendarDayInfo){
        this.minDate = calendarDay
        calendarAdapter.setMonthItem(listOf(1, 2, 3, 4, 5, 6))
        getCalendarAllowedDates()
    }

    fun setMaxDate(calendarDay: CalendarDayInfo){
        this.maxDate = calendarDay
        getCalendarAllowedDates()
    }

    fun setOnCalendarChangedListener(onCalendarChangedListener: OnCalendarChangedListener){
        this.onCalendarChangedListener = onCalendarChangedListener
    }

    private fun getCalendarAllowedDates(){
        minDate.let { minDate ->
            getMaxDate().let { maxDate ->

            }
        }
    }

    private fun getMaxDate(): CalendarDayInfo {
        return maxDate ?: getTodayDate().apply {
            year?.let {
                year = it + 1
            }
        }
    }

    private fun setupView(){
        binding.rvCalendarMonth.apply {
            adapter = calendarAdapter
            val gridLayoutManager = GridLayoutManager(context, 7)
            gridLayoutManager.spanSizeLookup = object: SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when(calendarAdapter.currentList.getOrNull(position)?.viewType){
                        CalendarMonthViewType.CALENDAR_MONTH_NAME -> 7
                        else -> 1
                    }
                }
            }
            layoutManager = gridLayoutManager
        }
    }

    private fun setCalendarViewType(type: CalendarViewTypeEnum){

    }

    private fun setCalendarSelectionType(type: CalendarSelectionTypeEnum){
        this.selectionType = type
    }

    private fun setMaxMultiSelectionDates(max: Int){
        this.maxMultiSelectionDates = max
    }

    private val calendarListener = object: CalendarAdapterListener {
        override fun getCalendarSelectionType(): CalendarSelectionTypeEnum {
            return this@CustomCalendar.selectionType
        }

        override fun getMaxMultiSelectionDates(): Int? {
            return this@CustomCalendar.maxMultiSelectionDates
        }

        override fun onSelectDates(list: List<CalendarDayInfo>) {
            this@CustomCalendar.selectedDates = list.toMutableList()
            onCalendarChangedListener?.onSelectDates(list)
        }

        override fun onSelectDate(date: CalendarDayInfo) {
            this@CustomCalendar.selectedDate = date
            onCalendarChangedListener?.onSelectDate(date)
        }

        override fun onMaxSelectionReach(selectedQuantity: Int) {
            onCalendarChangedListener?.onMaxSelectionReach(selectedQuantity)
        }
    }
}