package com.insitu.survscribe;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimePicker {
    private Activity activity;
    private EditText dateEditText;
    private EditText timeEditText;
    private Calendar selectedDateTime;
    private SimpleDateFormat dateFormat;

    public DateTimePicker(Activity activity, EditText dateEditText, EditText timeEditText) {
        this.activity = activity;
        this.dateEditText = dateEditText;
        this.timeEditText = timeEditText;
        this.selectedDateTime = Calendar.getInstance();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        setupDateEditText();
        setupTimeEditText();
    }

    private void setupDateEditText() {
        if (dateEditText != null) {
            dateEditText.setOnClickListener(view -> showDatePickerDialog());
        }
    }

    private void setupTimeEditText() {
        if (timeEditText != null)
            timeEditText.setOnClickListener(view -> showTimePickerDialog());
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePicker = new DatePickerDialog(activity,
                (datePicker1, year, month, day) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, day);
                    updateDateDisplay();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePicker = new TimePickerDialog(activity,
                (timePicker1, hour, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hour);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    updateTimeDisplay();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                false);
        timePicker.show();
    }

    private void updateDateDisplay() {
        dateEditText.setText(dateFormat.format(selectedDateTime.getTime()));
    }

    private void updateTimeDisplay() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        timeEditText.setText(timeFormat.format(selectedDateTime.getTime()));
    }

    public Calendar getSelectedDateTime() {
        return selectedDateTime;
    }
}