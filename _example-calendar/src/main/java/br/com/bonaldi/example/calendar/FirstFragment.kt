package br.com.bonaldi.example.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.bonaldi.customcalendar.helpers.DateHelper.getTodayDate
import br.com.bonaldi.customcalendar.helpers.IntHelper.orZero
import br.com.bonaldi.customcalendar.listeners.OnCalendarChangedListener
import br.com.bonaldi.customcalendar.models.day.CalendarDayInfo
import br.com.bonaldi.example.calendar.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.customCalendarItem.apply {
            setOnCalendarChangedListener(object : OnCalendarChangedListener {
                override fun onSelectDates(list: List<CalendarDayInfo>) {
                    //TODO("Not yet implemented")
                }

                override fun onSelectDate(date: CalendarDayInfo) {
                    //TODO("Not yet implemented")
                }

                override fun onMaxSelectionReach(selectedQuantity: Int) {
                    Toast.makeText(
                        context,
                        "Quantidade máxima de $selectedQuantity já foi selecionada.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            setMinDate(getTodayDate())
            getTodayDate().apply {
                month = month.orZero() + 2
                setMaxDate(this)
            }
            refreshCalendar()
        }
    }
}