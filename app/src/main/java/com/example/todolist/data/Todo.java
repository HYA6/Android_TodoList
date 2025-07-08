package com.example.todolist.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "todo_table")
public class Todo {

    @PrimaryKey(autoGenerate = true) // 기본키, 자동 증가
    private int id;
    private String content; // 내용
    private boolean isDone; // 체크 여부
    private String startDate; // 시작일
    private String endDate; // 마감일

    // 생성자
    public Todo(String content, boolean isDone, String startDate, String endDate) {
        this.content = content;
        this.isDone = isDone;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }

    // getter, setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isDone() { return isDone; }
    public void setDone(boolean done) { isDone = done; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
