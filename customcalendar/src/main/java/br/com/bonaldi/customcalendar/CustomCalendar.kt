package br.com.bonaldi.customcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import br.com.bonaldi.customcalendar.adapters.CalendarAdapter
import br.com.bonaldi.customcalendar.databinding.CustomCalendarLayoutBinding
import br.com.bonaldi.customcalendar.listeners.CalendarAdapterListener
import br.com.bonaldi.customcalendar.listeners.OnCalendarChangedListener
import br.com.bonaldi.customcalendar.models.day.CalendarDay
import br.com.bonaldi.customcalendar.models.day.CalendarDayListItem
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum

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
    

    private val binding = CustomCalendarLayoutBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    private var calendarAdapter: CalendarAdapter? = null
    private var params = CalendarParams()
    private var onCalendarChangedListener: OnCalendarChangedListener? = null

    private fun setAttributes(attrs: AttributeSet?){
        attrs?.let { attributeSet ->
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomCalendar, 0, 0)
            val selectionType = CalendarSelectionTypeEnum.values()[typedArray.getInt(
                R.styleable.CustomCalendar_selectionType,
                0
            )]

            typedArray.getInt(R.styleable.CustomCalendar_maxMultiSelectionDates, 0).takeIf { it != 0 }?.let {
                setMaxMultiSelectionDates(it)
            }

            params.colorParams.apply {
                typedArray.getColorStateList(R.styleable.CustomCalendar_weekDayTextColor)?.let { weekDayTextColor ->
                    this.weekDayTextColor = weekDayTextColor
                }
                typedArray.getColor(R.styleable.CustomCalendar_weekDayBackgroundColor, 0).takeIf { it != 0 }?.let { weekDayBackgroundColor ->
                    this.weekDayBackgroundColor = weekDayBackgroundColor
                }
                typedArray.getColorStateList(R.styleable.CustomCalendar_dayTextColor)?.let { dayTextColor ->
                    this.dayTextColor = dayTextColor
                }
                typedArray.getColor(R.styleable.CustomCalendar_dayBackgroundColor, 0).takeIf { it != 0 }?.let { dayBackgroundColor ->
                    this.dayBackgroundColor = dayBackgroundColor
                }
                typedArray.getColorStateList(R.styleable.CustomCalendar_selectedDayTextColor)?.let { selectedDayTextColor ->
                    this.selectedDayTextColor = selectedDayTextColor
                }
                typedArray.getColor(R.styleable.CustomCalendar_selectedDayBackgroundColor, 0).takeIf { it != 0 }?.let { selectedDayBackgroundColor ->
                    this.selectedDayBackgroundColor = selectedDayBackgroundColor
                }
                typedArray.getColorStateList(R.styleable.CustomCalendar_monthTextColor)?.let { monthTextColor ->
                    this.monthTextColor = monthTextColor
                }
                typedArray.getColor(R.styleable.CustomCalendar_monthBackgroundColor, 0).takeIf { it != 0 }?.let { monthBackgroundColor ->
                    this.monthBackgroundColor = monthBackgroundColor
                }
            }

            setCalendarSelectionType(selectionType)
            setupView()
            typedArray.recycle()
        }
    }

    fun setMinDate(calendarDay: CalendarDay){
        params.dateParams.minDate = calendarDay
    }

    fun setMaxDate(calendarDay: CalendarDay){
        params.dateParams.maxDate = calendarDay
    }

    fun refreshCalendar(){
        setupView()
        calendarAdapter?.refreshCalendar()
    }

    fun setOnCalendarChangedListener(onCalendarChangedListener: OnCalendarChangedListener){
        this.onCalendarChangedListener = onCalendarChangedListener
    }

    fun setCalendarSelectionType(type: CalendarSelectionTypeEnum){
        params.typeParams.selectionType = type
    }

    private fun setupView(){
        binding.rvCalendarMonth.apply {
            calendarAdapter = CalendarAdapter(listener = calendarListener)
            adapter = calendarAdapter
            val gridLayoutManager = GridLayoutManager(context, 7)
            gridLayoutManager.spanSizeLookup = object: SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when(calendarAdapter?.currentList?.getOrNull(position)?.viewType){
                        CalendarDayListItem.CalendarViewType.CALENDAR_MONTH_NAME -> 7
                        else -> 1
                    }
                }
            }
            layoutManager = gridLayoutManager
        }
    }

    private fun setMaxMultiSelectionDates(max: Int){
        params.typeParams.maxMultiSelectionDates = max
    }

    private val calendarListener = object: CalendarAdapterListener {
        override fun getCalendarSelectionType(): CalendarSelectionTypeEnum {
            return this@CustomCalendar.params.typeParams.selectionType
        }

        override fun getMaxMultiSelectionDates(): Int? {
            return this@CustomCalendar.params.typeParams.maxMultiSelectionDates
        }

        override fun onSelectDates(list: List<CalendarDay>) {
            this@CustomCalendar.params.preConfigParam.selectedDates = list.toMutableList()
            onCalendarChangedListener?.onSelectDates(list)
        }

        override fun onSelectDate(date: CalendarDay?) {
            this@CustomCalendar.params.preConfigParam.selectedDate = date
            this@CustomCalendar.onCalendarChangedListener?.onSelectDate(date)
        }

        override fun onMaxSelectionReach(selectedQuantity: Int) {
            this@CustomCalendar.onCalendarChangedListener?.onMaxSelectionReach(selectedQuantity)
        }

        override fun getCalendarParams(): CalendarParams {
            return this@CustomCalendar.params
        }

        override fun getMaxDate(): CalendarDay? {
            return this@CustomCalendar.params.dateParams.maxDate
        }

        override fun getMinDate(): CalendarDay {
            return this@CustomCalendar.params.dateParams.minDate
        }
    }
}