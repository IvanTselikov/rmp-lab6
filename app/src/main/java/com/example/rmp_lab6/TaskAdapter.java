package com.example.rmp_lab6;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    interface OnTaskClickListener{
        void onTaskClick(Task task, int position);
    }

    private final LayoutInflater inflater;
    private final List<Task> tasks;
    private final OnTaskClickListener onClickListener;

    TaskAdapter(Context context, List<Task> tasks, OnTaskClickListener onClickListener) {
        this.tasks = tasks;
        this.inflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;
    }

    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskAdapter.ViewHolder holder, int position) {
        Task task = tasks.get(holder.getAdapterPosition());
        Calendar dateTime = task.getDateTime();

        String timeLabel = String.format(Locale.getDefault(),
                "%02d.%02d.%d ",
                dateTime.get(Calendar.DAY_OF_MONTH),
                dateTime.get(Calendar.MONTH) + 1,
                dateTime.get(Calendar.YEAR));

        if (task.getHasTime())
            timeLabel += String.format(Locale.getDefault(),
                    " %02d:%02d",
                    dateTime.get(Calendar.HOUR_OF_DAY),
                    dateTime.get(Calendar.MINUTE));

        holder.nameTextView.setText(task.getName());
        holder.dateTimeTextView.setText(timeLabel);

        // обработка нажатия на кнопку "Редактировать"
        holder.editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // вызываем метод слушателя, передавая ему данные
                onClickListener.onTaskClick(task, holder.getAdapterPosition());
            }
        });

        // обработка нажатия на кнопку "Удалить"
        holder.removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int actualPosition = holder.getAdapterPosition();
                tasks.remove(actualPosition);
                notifyItemRemoved(actualPosition);
                notifyItemRangeChanged(actualPosition, tasks.size());
            }
        });

        if (highlighted) {
            switch (task.getPriority()) {
                case NORMAL: holder.nameTextView.setBackgroundColor(Color.rgb(255, 255, 128)); break;
                case HIGH: holder.nameTextView.setBackgroundColor(Color.rgb(255, 128, 128)); break;
                case LOW: holder.nameTextView.setBackgroundColor(Color.rgb(128, 128, 255)); break;
            }
        }
        else {
            holder.nameTextView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    boolean highlighted = false; // флаг: включена ли подсветка задач
    public void highlightTasks() {
        highlighted = !highlighted;
        for (int i = 0; i < tasks.size(); i++) {
            notifyItemRemoved(i);
            notifyItemRangeChanged(i, tasks.size());
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void clearTasks() {
        while (tasks.size() > 0) {
            tasks.remove(0);
            notifyItemRemoved(0);
            notifyItemRangeChanged(0, tasks.size());
        }
    }

    public void sortTasks() {
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return t1.getDateTime().compareTo(t2.getDateTime());
            }
        });
        for (int i = 0; i < tasks.size(); i++) {
            notifyItemRemoved(i);
            notifyItemRangeChanged(i, tasks.size());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView, dateTimeTextView;
        final ImageButton editImageButton, removeImageButton;

        ViewHolder(View view){
            super(view);
            nameTextView = view.findViewById(R.id.nameTextView);
            dateTimeTextView = view.findViewById(R.id.dateTimeTextView);
            editImageButton = view.findViewById(R.id.editImageButton);
            removeImageButton = view.findViewById(R.id.removeImageButton);
        }
    }
}
