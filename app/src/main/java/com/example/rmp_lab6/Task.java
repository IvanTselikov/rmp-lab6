package com.example.rmp_lab6;

import java.util.Calendar;

public class Task {
    static Integer count = 0; // общее количество созданных задач
    private Integer number; // номер задачи
    private String name; // название задачи
    private String description; // описание
    private Calendar dateTime; // срок выполнения задачи
    private boolean hasTime; // false - установлена только дата выполнения задачи, без точного времени
    private Priority priority; // приоритет задачи

    public Task(Integer number, String name, String description, Calendar dateTime, boolean hasTime, Priority priority) throws Exception {
        if (number == 0) { // задача только создана. Номер не установлен
            count++;
            setNumber(count);
        }
        else
            setNumber(number);

        setName(name);
        setDescription(description);
        setDateTime(dateTime);
        setHasTime(hasTime);
        setPriority(priority);
    }

    public void setNumber(Integer number){
        this.number = number;
    }

    public int getNumber() { return this.number; }

    public void setName(String name) throws Exception {
        if (name == null || name.trim().length() == 0)
            throw new Exception("Название задачи не может быть пустым.");
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description : "";
    }

    public String getDescription() {
        return this.description;
    }

    public void setDateTime(Calendar dateTime) throws Exception {
        if (dateTime == null)
            throw new Exception("Срок выполнения задачи не указан.");
        this.dateTime = dateTime;
    }

    public Calendar getDateTime() { return this.dateTime; }

    public void setHasTime(boolean hasTime)
    {
        this.hasTime = hasTime;
        if (!hasTime) {
            dateTime.set(Calendar.HOUR_OF_DAY, 0);
            dateTime.set(Calendar.MINUTE, 0);
        }
    }

    public boolean getHasTime() { return this.hasTime; }

    public void setPriority(Priority priority) throws Exception {
        if (priority == null)
            throw new Exception("Приоритет не указан.");
        this.priority = priority;
    }

    public Priority getPriority() { return this.priority; }

    public static void setCount(int c) { count = c; }
}
