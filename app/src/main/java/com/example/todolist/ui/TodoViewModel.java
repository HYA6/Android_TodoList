package com.example.todolist.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.todolist.data.Todo;
import com.example.todolist.repository.TodoRepository;

import java.util.ArrayList;
import java.util.List;
/*
ViewModel: View(Activity)와 Model(DB, Repository)의 중간 다리 역할
    - UI가 관찰(Observe)할 수 있는 LiveData 제공
*/
public class TodoViewModel extends AndroidViewModel {

    private final TodoRepository repository; // DB 작업은 직접 하지 않고 Repository에 위임
    private final LiveData<List<Todo>> allTodos; // Repository로부터 받은 LiveData, UI가 옵저빙

    // 선택된 날짜 (yyyy-MM-dd 형식)
    private final MutableLiveData<String> selectDay = new MutableLiveData<>();

    public TodoViewModel(@NonNull Application application) {
        super(application);
        this.repository = new TodoRepository(application);
        this.allTodos = repository.getAllTodos(); // LiveData를 가져옴
    }

    // 전체 할 일 목록
    public LiveData<List<Todo>> getAllTodos() { return allTodos; } // UI에서 옵저빙할 수 있음

    // Activity에서 직접 DB에 접근하지 않고 ViewModel을 통해 처리
    public void insert(Todo todo) { repository.insert(todo); } // 내부적으로 백그라운드에서 실행됨
    public void update(Todo todo) { repository.update(todo); }
    public void delete(Todo todo) { repository.delete(todo); }
}
