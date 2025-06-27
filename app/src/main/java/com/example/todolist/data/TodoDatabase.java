package com.example.todolist.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Todo.class}, version = 1) // Entity 설정
public abstract class TodoDatabase extends RoomDatabase {
    private static TodoDatabase instance;
    public abstract TodoDAO todoDAO();
    public static synchronized TodoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            TodoDatabase.class, "todo_database")
                    .fallbackToDestructiveMigration() // 버전 바뀌었을 때 DB 날리고 다시 생성 (초기 개발 단계에서 사용)
                    .build();
        }
        return instance;
    }
}
