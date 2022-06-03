package br.com.bonaldi.example.calendar

import android.content.Context
import android.widget.ArrayAdapter

object ViewHelper {

    fun <T> createSpinnerAdapter(context: Context, itemList: List<T>): ArrayAdapter<T> {
        val arrayAdapter: ArrayAdapter<T> = ArrayAdapter<T>(
            context,
            R.layout.calendar_example_spinner_item,
            itemList
        )
        arrayAdapter.setDropDownViewResource(R.layout.calendar_example_spinner_dropdown_item)
        return arrayAdapter
    }
}