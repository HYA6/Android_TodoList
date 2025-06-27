package com.example.todolist.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "todo_table")
public class Todo {

    @PrimaryKey(autoGenerate = true) // 기본키이며 자동 증가
    private int id;

    private String content;

    private boolean isDone;

    // 생성자
    public Todo(String content, boolean isDone) {
        this.content = content;
        this.isDone = isDone;
    }

    // getter, setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isDone() { return isDone; }
    public void setDone(boolean done) { isDone = done; }
}
