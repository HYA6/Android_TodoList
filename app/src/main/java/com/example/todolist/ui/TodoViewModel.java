package com.example.todolist.ui;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todolist.data.Todo;
import com.example.todolist.repository.TodoRepository;

import java.util.List;
/*
ViewModel: View(Activity)와 Model(DB, Repository)의 중간 다리 역할
    - UI가 관찰(Observe)할 수 있는 LiveData 제공
*/
public class TodoViewModel extends AndroidViewModel {

    private TodoRepository repository; // DB 작업은 직접 하지 않고 Repository에 위임
    private LiveData<List<Todo>> allTodos; // Repository로부터 받은 LiveData, UI가 옵저빙

    public TodoViewModel(@NonNull Application application) {
        super(application);
        repository = new TodoRepository(application);
        allTodos = repository.getAllTodos();  // LiveData를 가져옴
    }
    public LiveData<List<Todo>> getAllTodos() { return allTodos; } // UI에서 옵저빙할 수 있음

    // Activity에서 직접 DB에 접근하지 않고 ViewModel을 통해 처리
    public void insert(Todo todo) { repository.insert(todo); } // 내부적으로 백그라운드에서 실행됨
    public void update(Todo todo) { repository.update(todo); }
    public void delete(Todo todo) { repository.delete(todo); }
}
