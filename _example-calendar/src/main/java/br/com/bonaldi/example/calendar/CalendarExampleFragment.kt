package br.com.bonaldi.example.calendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.bonaldi.customcalendar.helpers.DateHelper.getTodayDate
import br.com.bonaldi.customcalendar.helpers.IntHelper.orZero
import br.com.bonaldi.customcalendar.listeners.OnCalendarChangedListener
import br.com.bonaldi.customcalendar.models.day.CalendarDay
import br.com.bonaldi.customcalendar.models.enums.CalendarSelectionTypeEnum
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

    private fun setupCalendarComponent(selectionTypeEnum: CalendarSelectionTypeEnum = CalendarSelectionTypeEnum.SINGLE){
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

    private fun setupCalendarConfigOptions() = binding.apply {
        lifecycleScope.launch {
            val selectionTypeAdapter = setupSpinnerAdapter(
                requireContext(),
                CalendarSelectionTypeEnum.values().map { it.name.toLowerCase().capitalize() }
                    .toList()
            )
            spinnerSelectionType.adapter = selectionTypeAdapter
            spinnerSelectionType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        CalendarSelectionTypeEnum.values()[position].let { selectedType ->
                            setupCalendarComponent(selectedType)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }

    private fun <T> setupSpinnerAdapter(context: Context, itemList: List<T>): ArrayAdapter<T> {
        val arrayAdapter: ArrayAdapter<T> = ArrayAdapter<T>(
            context,
            R.layout.calendar_example_spinner_item,
            itemList
        )
        arrayAdapter.setDropDownViewResource(R.layout.calendar_example_spinner_dropdown_item)
        return arrayAdapter
    }
}