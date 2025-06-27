package com.example.todolist.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao // 데이터 액세스 객체 (JPA랑 비슷)
public interface TodoDAO {
    @Insert
    void insert(Todo todo);
    @Update
    void update(Todo todo);
    @Delete
    void delete(Todo todo);
    @Query("SELECT * FROM todo_table ORDER BY id DESC")
    LiveData<List<Todo>> getAllTodos(); // 실시간으로 변경되는 데이터 감지
}
