package com.example.todolist.data;

public class Todo {
    private String content; // 할 일 텍스트
    private boolean done; // 할 일 끝냈는지 여부

    public Todo(String content, boolean done) {
        this.content = content;
        this.done = done;
    }

    public String getContent() { return content; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
}
