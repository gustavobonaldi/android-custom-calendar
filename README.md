# Custom Calendar 

Custom options:
- Set Minimum date, maximium date and current date;
- Set color of bullet on current date;
- Set fontFamily of textViews displayed on Calendar;
- Set `TextColor` and `BackgroundColor` from: 
  - Disabled days;
  - Week day;
  - Month day;
  - Selected month day;
  - Month name.

- Dates selection type:
  - Single;
  - Multiple (with possibility to set maximum number);
  - Range.

# Single Selection
<img src="https://user-images.githubusercontent.com/39656251/172745717-348665c2-e103-4ac6-af56-e3c93f048cf1.png" width=50% height=50%>

# Multiple Selection
<img src="https://user-images.githubusercontent.com/39656251/172745807-61541387-16b0-4ced-8396-8ef9ef823d7c.png" width=50% height=50%>

# Range Selection
<img src="https://user-images.githubusercontent.com/39656251/172745911-c68c1616-af36-4fb2-8005-f9535226feb2.png" width=50% height=50%>

# Getting started

Create a style with the attributes you want:
```
  <style name="Theme.CustomCalendar" parent="Theme.MaterialComponents.DayNight">
        <item name="selectionType">Single</item>
        <item name="calendarFontFamily">monospace</item>
        <item name="dayBackgroundColor">@color/white_app</item>
        <item name="dayTextColor">@color/black</item>
        <item name="monthBackgroundColor">@color/black</item>
        <item name="monthTextColor">@color/white_app</item>
        <item name="weekDayBackgroundColor">@color/black</item>
        <item name="weekDayTextColor">@color/white_app</item>
        <item name="selectedDayBackgroundColor">@color/black</item>
        <item name="selectedDayTextColor">@color/white_app</item>
        <item name="currentDateColor">@color/black</item>
        <item name="disabledDayBackgroundColor">@color/white_app</item>
        <item name="disabledDayTextColor">@color/disabled_day_color</item>
    </style>
```

Then add the component to your layout, as follow:
```
  <br.com.bonaldi.customcalendar.CustomCalendar
        android:id="@+id/custom_calendar_component"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_margin="10dp"
        android:theme="@style/CalendarStyle">
```

In your class, set optional attributes like minimum date, maximum date and disabled days, attach OnChangedCalendar listener to receive updates and then, call `refreshCalendar()`:
```
  private fun setupCalendar(){
        binding.customCalendarComponent.apply {
            setOnCalendarChangedListener(object: OnCalendarChangedListener{
                override fun onSelectDates(list: List<CalendarDay>) {
                    ...
                }

                override fun onSelectDate(date: CalendarDay?) {
                    ...
                }

                override fun onMaxSelectionReach(selectedQuantity: Int) {
                    ...
                }
            })
            
            setMinDate(getTodayDate())
            refreshCalendar()
        }
    }
```

