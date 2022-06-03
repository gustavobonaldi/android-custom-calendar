package br.com.bonaldi.example.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.bonaldi.customcalendar.helpers.DateHelper.getTodayDate
import br.com.bonaldi.customcalendar.helpers.IntHelper.orZero
import br.com.bonaldi.customcalendar.listeners.OnCalendarChangedListener
import br.com.bonaldi.customcalendar.models.day.CalendarDay
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum
import br.com.bonaldi.example.calendar.ViewHelper.createSpinnerAdapter
import br.com.bonaldi.example.calendar.databinding.FragmentCalendarExampleBinding
import kotlinx.coroutines.launch

class CalendarExampleFragment : Fragment() {
    private lateinit var binding: FragmentCalendarExampleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarExampleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendarConfigOptions()
    }

    private fun setupCalendarComponent(selectionTypeEnum: CalendarSelectionTypeEnum = CalendarSelectionTypeEnum.SINGLE) {
        lifecycleScope.launch {
            binding.customCalendarItem.apply {
                setOnCalendarChangedListener(object : OnCalendarChangedListener {
                    override fun onSelectDates(list: List<CalendarDay>) {
                        //TODO("Not yet implemented")
                    }

                    override fun onSelectDate(date: CalendarDay?) {
                        //TODO("Not yet implemented")
                    }

                    override fun onMaxSelectionReach(selectedQuantity: Int) {
                        lifecycleScope.launch {
                            Toast.makeText(
                                context,
                                context.resources.getString(
                                    R.string.max_selection_reached_message,
                                    selectedQuantity
                                ),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
                setMinDate(getTodayDate())
                getTodayDate().apply {
                    year = year.orZero() + 1
                    setMaxDate(this)
                }
                setCalendarSelectionType(selectionTypeEnum)
                refreshCalendar()
            }
        }
    }

    private fun setupCalendarConfigOptions() = binding.spinnerSelectionType.apply {
        lifecycleScope.launch {
            val selectionTypeAdapter = createSpinnerAdapter(
                requireContext(),
                CalendarSelectionTypeEnum.values().map { it.name.toLowerCase().capitalize() }
                    .toList()
            )
            adapter = selectionTypeAdapter
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
    }
}