package br.com.bonaldi.example.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.bonaldi.customcalendar.CustomCalendar
import br.com.bonaldi.customcalendar.helpers.DateHelper.getTodayDate
import br.com.bonaldi.customcalendar.helpers.IntHelper.orZero
import br.com.bonaldi.customcalendar.listeners.OnCalendarChangedListener
import br.com.bonaldi.customcalendar.models.day.CalendarDay
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum
import br.com.bonaldi.example.calendar.ViewHelper.createSpinnerAdapter
import br.com.bonaldi.example.calendar.databinding.FragmentCalendarExampleBinding
import kotlinx.coroutines.launch
import java.util.*

class CalendarExampleFragment : Fragment() {
    private lateinit var binding: FragmentCalendarExampleBinding
    private val selectedDatesAdapter: ArrayAdapter<String> by lazy {
        createSpinnerAdapter(
            requireContext(),
            mutableListOf()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarExampleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenCreated {
            setupCalendarConfigOptions()
        }
    }

    private fun setupCalendarComponent(selectionTypeEnum: CalendarSelectionTypeEnum = CalendarSelectionTypeEnum.SINGLE) {
        binding.customCalendarItem.apply {
            addListener()
            setMinDate(getTodayDate())
            getTodayDate().apply {
                year = year.orZero() + 1
                setMaxDate(this)
            }
            setSelectionType(selectionTypeEnum)
            refreshCalendar()
        }
    }

    private fun CustomCalendar.setSelectionType(type: CalendarSelectionTypeEnum) {
        selectionType = type
        binding.tvSelectedDateLabel.text = when (selectionType) {
            CalendarSelectionTypeEnum.SINGLE -> context.resources.getString(R.string.selected_date_label)
            CalendarSelectionTypeEnum.MULTIPLE, CalendarSelectionTypeEnum.RANGE -> context.resources.getString(R.string.selected_dates_label)
        }
    }

    private fun CustomCalendar.addListener() {
        setOnCalendarChangedListener(object : OnCalendarChangedListener {
            override fun onSelectDates(list: List<CalendarDay>) {
                if (list.isNotEmpty()) {
                    when (selectionType) {
                        CalendarSelectionTypeEnum.MULTIPLE -> {
                            setSelectedDatesVisibility(list.count() > 1)
                            when {
                                list.count() == 1 -> {
                                    binding.tvSelectedDateValue.text = context.resources.getString(
                                        R.string.selected_date_value,
                                        list.firstOrNull()?.toString()
                                    )
                                }
                                else -> {
                                    list.map {

                                    }
                                    selectedDatesAdapter.clear()
                                    selectedDatesAdapter.addAll(list.map { it.toString() })
                                }
                            }
                        }
                        CalendarSelectionTypeEnum.RANGE -> {
                            setSelectedDatesVisibility(false)
                            binding.tvSelectedDateValue.text = when {
                                list.count() == 1 -> context.resources.getString(
                                    R.string.selected_date_value,
                                    list.firstOrNull()?.toString()
                                )
                                else -> context.resources.getString(
                                    R.string.selected_date_range_value,
                                    list.firstOrNull()?.toString(),
                                    list.lastOrNull()?.toString()
                                )
                            }
                        }
                    }
                } else {
                    binding.tvSelectedDateValue.text =
                        context.resources.getString(R.string.none_text)
                }
            }

            override fun onSelectDate(date: CalendarDay?) {
                setSelectedDatesVisibility(false)
                when(selectionType){
                    CalendarSelectionTypeEnum.SINGLE -> {
                        date?.toString()?.let {
                            binding.tvSelectedDateValue.text = context.resources.getString(R.string.selected_date_value, it)
                        }
                    }
                }
            }

            override fun onMaxSelectionReach(selectedQuantity: Int) {
                showToast(
                    context.resources.getString(
                        R.string.max_selection_reached_message,
                        selectedQuantity
                    )
                )
            }
        })
    }

    private fun setupCalendarConfigOptions() = binding.apply {
        lifecycleScope.launch {
            spinnerSelectionType.apply {
                adapter = createSpinnerAdapter(
                    requireContext(),
                    CalendarSelectionTypeEnum.values().map { type ->
                        type.name.lowercase(Locale.ROOT).replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                        }
                    }
                )

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        setupCalendarComponent(CalendarSelectionTypeEnum.values()[position])
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
            spinnerSelectedDates.apply {
                adapter = selectedDatesAdapter
            }
        }
    }

    private fun setSelectedDatesVisibility(showSpinner: Boolean){
        binding.tvSelectedDateValue.visibility = if(showSpinner) View.GONE else View.VISIBLE
        binding.spinnerSelectedDates.visibility = if(showSpinner) View.VISIBLE else View.GONE
    }

    private fun showToast(text: String) {
        lifecycleScope.launch {
            Toast.makeText(
                context,
                text,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}