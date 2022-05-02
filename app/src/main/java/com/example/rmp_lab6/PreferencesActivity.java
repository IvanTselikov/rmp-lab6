package com.example.rmp_lab6;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class PreferencesActivity extends AppCompatActivity {

    int number = 0; // номер редактируемой задачи
    Calendar dateTime = Calendar.getInstance();
    boolean isDateSet = false;
    EditText taskNameEditText, descriptionEditText;
    Button setDateButton, setTimeButton;
    Spinner prioritySpinner;

//    DatabaseHelper sqlHelper;
//    SQLiteDatabase db;
//    Cursor userCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs_layout);

        taskNameEditText = findViewById(R.id.taskNameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        setDateButton = findViewById(R.id.setDateButton);
        setTimeButton = findViewById(R.id.setTimeButton);
        prioritySpinner = findViewById(R.id.prioritySpinner);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            number = extras.getInt("number");
            String name = extras.getString("name");
            String description = extras.getString("description");
            Calendar dateTime = (Calendar) extras.getSerializable("dateTime");
            boolean hasTime = extras.getBoolean("hasTime");
            Priority priority = (Priority) extras.getSerializable("priority");

            isDateSet = true;

            taskNameEditText.setText(name);
            descriptionEditText.setText(description);

            String timeLabel = String.format(Locale.getDefault(),
                    "%02d.%02d.%4d",
                    dateTime.get(Calendar.DAY_OF_MONTH),
                    dateTime.get(Calendar.MONTH),
                    dateTime.get(Calendar.YEAR));
            setDateButton.setText(timeLabel);
            if (hasTime) {
                timeLabel = String.format(Locale.getDefault(),
                        "%02d:%02d",
                        dateTime.get(Calendar.HOUR_OF_DAY),
                        dateTime.get(Calendar.MINUTE));
                setTimeButton.setText(timeLabel);
            }
            switch (priority) {
                case NORMAL: prioritySpinner.setSelection(0); break;
                case HIGH: prioritySpinner.setSelection(1); break;
                case LOW: prioritySpinner.setSelection(2); break;
            }
        }

//        sqlHelper = new DatabaseHelper(this);
//        db = sqlHelper.getWritableDatabase();

        // пытаемся получить данные с MainActivity
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            int number = extras.getInt("number");
//            if (number > 0) {
//                userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
//                        DatabaseHelper.COLUMN_NUMBER + "=?", new String[]{String.valueOf(number)});
//                userCursor.moveToFirst();
//
//                taskNameEditText.setText(userCursor.getString(1));
//                descriptionEditText.setText(userCursor.getString(2));
//                boolean hasTime = extras.getBoolean("hasTime");
//                long millis = Long.parseLong(userCursor.getString(3));
//                dateTime = Calendar.getInstance();
//                dateTime.setTimeInMillis(millis);
//                Priority priority = (Priority) extras.getSerializable("priority");
//
//                String timeLabel = String.format(Locale.getDefault(),
//                        "%02d:%02d:%4d",
//                        dateTime.get(Calendar.DAY_OF_MONTH),
//                        dateTime.get(Calendar.MONTH),
//                        dateTime.get(Calendar.YEAR));
//                setDateButton.setText(timeLabel);
//                if (hasTime) {
//                    timeLabel = String.format(Locale.getDefault(),
//                            "%02d:%02d",
//                            dateTime.get(Calendar.HOUR_OF_DAY),
//                            dateTime.get(Calendar.MINUTE));
//                    setTimeButton.setText(timeLabel);
//                }
//                switch (priority) {
//                    case NORMAL: prioritySpinner.setSelection(0); break;
//                    case HIGH: prioritySpinner.setSelection(1); break;
//                    case LOW: prioritySpinner.setSelection(2); break;
//                }
//            }
//            userCursor.close();
            /*String name = extras.getString("name");
            String description = extras.getString("description");
            Calendar d = (Calendar) extras.getSerializable("dateTime");
            // устанавливаем в UI полученные данные, если они были
            String dateLabel = String.format(Locale.getDefault(),
                    "%02d.%02d.%d",
                    dateTime.get(Calendar.DAY_OF_MONTH),
                    dateTime.get(Calendar.MONTH),
                    dateTime.get(Calendar.YEAR));
            setDateButton.setText(dateLabel);

            */
//        }
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View view) {
        Calendar prevDateTime = (dateTime != null) ? dateTime : Calendar.getInstance();
        new DatePickerDialog(PreferencesActivity.this, d,
                prevDateTime.get(Calendar.YEAR),
                prevDateTime.get(Calendar.MONTH),
                prevDateTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View view) {
        boolean hasTime = !(setTimeButton.getText().toString().equals("Установить время..."));
        if (hasTime) {
            new TimePickerDialog(PreferencesActivity.this, t,
                    dateTime.get(Calendar.HOUR_OF_DAY),
                    dateTime.get(Calendar.MINUTE), true)
                    .show();
        }
        else {
            new TimePickerDialog(PreferencesActivity.this, t,
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    Calendar.getInstance().get(Calendar.MINUTE), true)
                    .show();
        }
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);
            String timeLabel = String.format(Locale.getDefault(),
                    " %02d:%02d", hourOfDay, minute);
            setTimeButton.setText(timeLabel);
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            isDateSet = true;
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String dateLabel = String.format(Locale.getDefault(),
                    "%02d.%02d.%d", dayOfMonth, monthOfYear, year);
            setDateButton.setText(dateLabel);
        }
    };

    public void saveNewTask(View view) {
        String name = taskNameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        boolean hasTime = !(setTimeButton.getText().toString().equals("Установить время..."));
        Priority priority = null;
        String selected = prioritySpinner.getSelectedItem().toString();
        switch (selected) {
            case "Нормальный": priority = Priority.NORMAL; break;
            case "Высокий": priority = Priority.HIGH; break;
            case "Низкий": priority = Priority.LOW; break;
        };
        Task task;
        try {
            if (!isDateSet)
                throw new Exception("Не установлен срок выполнения задачи.");

            task = new Task(number, name, description, dateTime, hasTime, priority);

            Intent intent = new Intent();
            intent.putExtra("number", task.getNumber());
            intent.putExtra("name", task.getName());
            intent.putExtra("description", task.getDescription());
            intent.putExtra("dateTime", task.getDateTime());
            intent.putExtra("hasTime", task.getHasTime());
            intent.putExtra("priority", task.getPriority());
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}