package br.com.bonaldi.customcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import br.com.bonaldi.customcalendar.adapters.CalendarAdapter
import br.com.bonaldi.customcalendar.databinding.CustomCalendarLayoutBinding
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

    private var selectedDate: CalendarDayInfo? = null
    private var selectedDates: CalendarDayInfo? = null
    private var minDate: CalendarDayInfo? = null
    private var maxDate: CalendarDayInfo? = null
    private var currentDate: CalendarDayInfo? = null

    private val binding = CustomCalendarLayoutBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    private val calendarAdapter: CalendarAdapter by lazy {
        CalendarAdapter()
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

            setCalendarViewType(calendarViewType)
            setCalendarSelectionType(selectionType)
            setupView()
            typedArray.recycle()
        }
    }

    fun setMinDate(calendarDay: CalendarDayInfo){
        this.minDate = calendarDay
        calendarAdapter.setMonthItem(listOf(1, 2, 3, 4, 5, 6))
    }

    fun setMaxDate(calendarDay: CalendarDayInfo){

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

    }
}