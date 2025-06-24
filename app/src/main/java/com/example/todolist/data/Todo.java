package com.example.todolist.data;

public class Todo {
    private String content;
    private boolean done;

    public Todo(String content, boolean done) {
        this.content = content;
        this.done = done;
    }

    public String getContent() { return content; }
    public boolean isDone() { return done; }
}
