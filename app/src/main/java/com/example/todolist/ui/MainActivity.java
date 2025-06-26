package com.example.todolist.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.data.Todo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTodo;
    private Button buttonAdd;
    private RecyclerView recyclerView;
    private TodoAdapter adapter;

    // 메모리 내 임시 리스트 (Room 없이)
    private List<Todo> todoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // activity_main 연결

        // UI 컴포넌트 연결
        editTextTodo = findViewById(R.id.editTextTodo);
        buttonAdd = findViewById(R.id.buttonAdd);
        recyclerView = findViewById(R.id.recyclerViewTodos);

        // RecyclerView 설정
        adapter = new TodoAdapter(todoList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 추가 버튼 클릭 이벤트
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editTextTodo.getText().toString().trim();
                if (!content.isEmpty()) {
                    // 새 할 일 객체 추가
                    Todo todo = new Todo(content, false);
                    todoList.add(todo);
                    // RecyclerView 갱신
                    adapter.notifyItemInserted(todoList.size() - 1);
                    // 입력창 비우기
                    editTextTodo.setText("");
                }
            }
        });
    }
}