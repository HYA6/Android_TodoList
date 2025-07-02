package com.example.todolist.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.todolist.data.Todo;
import com.example.todolist.data.TodoDAO;
import com.example.todolist.data.TodoDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// ViewModel과 DB 사이의 중간 다리 역할
public class TodoRepository {

    private TodoDAO todoDao;
    private LiveData<List<Todo>> allTodos;

    // 백그라운드에서 실행할 Executor (DB는 메인스레드에서 실행하면 안 됨)
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public TodoRepository(Application application) {
        // DB 인스턴스 생성
        TodoDatabase db = TodoDatabase.getInstance(application);
        todoDao = db.todoDAO();
        allTodos = todoDao.getAllTodos();
    }

    // UI(ViewModel)에서 옵저빙 할 수 있도록 모든 할 일 리스트를 보관
    // 옵저빙(Observing): Android 앱에서 LiveData를 쓸 때 데이터의 변화를 실시간으로 감지해서 자동으로 UI를 갱신하는 동작
    public LiveData<List<Todo>> getAllTodos() { return allTodos; }
    // 추가
    public void insert(Todo todo) { executorService.execute(() -> todoDao.insert(todo)); }
    // 수정
    public void update(Todo todo) { executorService.execute(() -> todoDao.update(todo)); }
    // 삭제
    public void delete(Todo todo) { executorService.execute(() -> todoDao.delete(todo)); }
}
