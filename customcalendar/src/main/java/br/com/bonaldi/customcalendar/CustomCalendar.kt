package br.com.bonaldi.customcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import br.com.bonaldi.customcalendar.adapters.CalendarAdapter
import br.com.bonaldi.customcalendar.databinding.CustomCalendarLayoutBinding
import br.com.bonaldi.customcalendar.listeners.CalendarAdapterListener
import br.com.bonaldi.customcalendar.listeners.OnCalendarChangedListener
import br.com.bonaldi.customcalendar.models.day.CalendarDay
import br.com.bonaldi.customcalendar.models.day.CalendarDayListItem
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum

class CustomCalendar : LinearLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.calendarDefaultStyle)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): this(context, attrs, defStyleAttr, R.style.Theme_CustomCalendar)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ){
        setAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private val binding = CustomCalendarLayoutBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    private var calendarAdapter: CalendarAdapter? = null
    private var params = CalendarParams()
    private var onCalendarChangedListener: OnCalendarChangedListener? = null
    var selectionType: CalendarSelectionTypeEnum
        get() = params.typeParams.selectionType
        set(value) {
            params.typeParams.selectionType = value
        }

    private fun setAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int){
        attrs?.let { attributeSet ->
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomCalendar, defStyleAttr, 0)
            val selectionType = CalendarSelectionTypeEnum.values()[typedArray.getInt(
                R.styleable.CustomCalendar_selectionType,
                0
            )]

            typedArray.getInt(R.styleable.CustomCalendar_maxMultiSelectionDates, 0).takeIf { it != 0 }?.let {
                setMaxMultiSelectionDates(it)
            }

            this.selectionType = selectionType
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

    fun setDisabledDays(list: List<CalendarDay>){
        params.dateParams.disabledDays = list
    }

    fun refreshCalendar(){
        setupView()
        calendarAdapter?.refreshCalendar()
    }

    fun setOnCalendarChangedListener(onCalendarChangedListener: OnCalendarChangedListener){
        this.onCalendarChangedListener = onCalendarChangedListener
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

        override fun getDisabledDays(): List<CalendarDay> {
            return this@CustomCalendar.params.dateParams.disabledDays
        }
    }
}