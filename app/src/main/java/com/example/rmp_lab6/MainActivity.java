package com.example.rmp_lab6;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Task> tasks = new ArrayList<>();
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getApplicationContext().deleteDatabase("app.db");

        databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getReadableDatabase();

        // пытаемся получить данные с PreferencesActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int number = extras.getInt("number");
            String name = extras.getString("name");
            String description = extras.getString("description");
            Calendar d = (Calendar) extras.getSerializable("dateTime");
            boolean hasTime = extras.getBoolean("hasTime");
            Priority priority = (Priority) extras.getSerializable("priority");
            try {
                writeToDB(new Task(number, name, description, d, hasTime, priority));
            } catch (Exception e) { }
        }

        query = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE, null);

        moveToAnswerToRequest(query);

        recyclerView = findViewById(R.id.list);

        TaskAdapter.OnTaskClickListener taskClickListener = onTaskClickListener(this);

        // создаем адаптер
        adapter = new TaskAdapter(this, tasks, taskClickListener);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
    }

    // определяем слушателя нажатия на кнопку "Редактировать" для конкретной задачи
    private TaskAdapter.OnTaskClickListener onTaskClickListener(Context currentActivity) {
        TaskAdapter.OnTaskClickListener taskClickListener = new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(Task task, int position) {
                Intent intent = new Intent();
                intent.putExtra("number", task.getNumber());
                intent.putExtra("name", task.getName());
                intent.putExtra("description", task.getDescription());
                intent.putExtra("dateTime", task.getDateTime());
                intent.putExtra("hasTime", task.getHasTime());
                intent.putExtra("priority", task.getPriority());
                intent.setClass(currentActivity, PreferencesActivity.class);
                startActivity(intent);
                finish();
            }
        };
        return taskClickListener;
    }

    // прочитываем ответ на запрос из БД и заполняем список задач
    private void moveToAnswerToRequest(Cursor query) {
        while (query.moveToNext()) {
            int number = query.getInt(0);
            String name = query.getString(1);
            String description = query.getString(2);
            long millis = Long.parseLong(query.getString(3));
            Calendar dateTime = Calendar.getInstance();
            dateTime.setTimeInMillis(millis);
            boolean hasTime = query.getInt(4) == 1;
            Priority priority = Priority.values()[query.getInt(5)];
            try {
                tasks.add(new Task(number, name, description, dateTime, hasTime, priority));
            } catch (Exception e) { }
            if (query.isAfterLast())
                Task.setCount(number);
        }
        query.close();
    }

    // запись в базу данных
    public void writeToDB(Task task) {
        String name = task.getName();
        String description = task.getDescription();
        String dateTime = String.valueOf(task.getDateTime().getTimeInMillis());
        int hasTime = task.getHasTime() ? 1 : 0;
        int priority = task.getPriority().ordinal();
        int number = task.getNumber();

        ContentValues cv = new ContentValues();
        cv.put("number", number);
        cv.put("name", name);
        cv.put("description", description);
        cv.put("dateTime", dateTime);
        cv.put("hasTime", hasTime);
        cv.put("priority", priority);

        // проверяем, нет ли уже этой записи в таблице
        Cursor cursor = db.rawQuery("SELECT * FROM " + databaseHelper.TABLE +
                " WHERE " + databaseHelper.COLUMN_NUMBER + " = " + number, null);
        if (cursor.getCount() < 1) {
            // добавление новой записи
            db.execSQL("INSERT OR IGNORE INTO " + databaseHelper.TABLE +
                    " VALUES (" + number + ",'" + name + "', " +
                    "'" + description + "', '" + dateTime + "', " + hasTime + ", " + priority + ")");
        } else {
            // изменение старой записи
            String s = "UPDATE " + databaseHelper.TABLE + " SET " +
                    databaseHelper.COLUMN_NAME + " = '" + name + "', " +
                    databaseHelper.COLUMN_DESCRIPTION + " = '" + description + "', " +
                    databaseHelper.COLUMN_DATETIME + " = '" + dateTime + "', " +
                    databaseHelper.COLUMN_HASTIME + " = " + hasTime + ", " +
                    databaseHelper.COLUMN_PRIORITY + " = " + priority + " " +
                    "WHERE " + databaseHelper.COLUMN_NUMBER + " = " + number;
            db.execSQL(s);
        }
        cursor.close();
        //db.replaceOrThrow("app.db", null, cv);
    }

    //нажата кнопка добавления задачи
    public void onAddButtonClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, PreferencesActivity.class);
        startActivity(intent);
        finish();
    }

    //развернуть меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //выбор опции меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_all:
                adapter.clearTasks();
                databaseHelper.deleteDataBase(db);
                getApplicationContext().deleteDatabase(DatabaseHelper.DATABASE_NAME);
                return true;
            case R.id.sort:
                adapter.sortTasks();
                return true;
            case R.id.highlight:
                adapter.highlightTasks();
                return true;
            case R.id.exit:
                databaseHelper.deleteDataBase(db);
                getApplicationContext().deleteDatabase(DatabaseHelper.DATABASE_NAME);
                databaseHelper = new DatabaseHelper(getApplicationContext());
                db = databaseHelper.getReadableDatabase();
                for (Task task : tasks)
                    writeToDB(task);
                finish();
                return true;
            default:
                return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
        db.close();
    }
}