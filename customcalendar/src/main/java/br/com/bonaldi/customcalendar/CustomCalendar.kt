package br.com.bonaldi.customcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import br.com.bonaldi.customcalendar.adapters.MonthAdapter
import br.com.bonaldi.customcalendar.databinding.CalendarMonthItemBinding
import br.com.bonaldi.customcalendar.databinding.CustomCalendarLayoutBinding
import br.com.bonaldi.customcalendar.models.day.CalendarDayInfo
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum
import br.com.bonaldi.customcalendar.models.enums.CalendarViewTypeEnum
import java.text.SimpleDateFormat
import java.util.*

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

    private val selectedDate: CalendarDayInfo? = null
    private val selectedDates: CalendarDayInfo? = null
    private val minDate: CalendarDayInfo? = null
    private val maxDate: CalendarDayInfo? = null
    private val currentDate: CalendarDayInfo? = null

    private val binding = CalendarMonthItemBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    private val monthAdapter: MonthAdapter by lazy {
        MonthAdapter()
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

    fun setMinDate(month: Int){
        monthAdapter.setMonthItem(2)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        binding.tvMonthName.text = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(calendar.time)
    }

    fun setMaxMonth(month: Int){

    }

    private fun setupView(){
        binding.rvCalendarMonth.apply {
            adapter = monthAdapter
            layoutManager = GridLayoutManager(context, 7)
        }
    }

    private fun setCalendarViewType(type: CalendarViewTypeEnum){

    }

    private fun setCalendarSelectionType(type: CalendarSelectionTypeEnum){

    }
}