package com.example.todolist.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.data.Todo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/*
MVVM 패턴: Model-View-ViewModel 패턴
    - Model: 데이터, 비즈니스 로직 담당 (ex.Room DB, Repository, API 등)
    - View: 사용자 UI 담당 (ex.Activity, Fragment, XML 등)
    - ViewModel: View와 Model의 중간 다리 역할, 상태 관리 담당 (ex.ViewModel 클래스, LiveData 등)
    - 흐름: 사용자 입력 -> View -> ViewModel(UI 상태 저장 및 처리) -> Model(실제 데이터 처리) -> ViewModel -> View
    - 장점: 코드 분리, 재사용성 증가, 테스트 용이, 수명 관리 등
*/
public class MainActivity extends AppCompatActivity {

    private EditText editTextTodo;
    private RecyclerView recyclerView;
    private TodoViewModel viewModel;
    private TodoAdapter adapter;
    private Button buttonAdd;


    // 메모리 내 임시 리스트
    private List<Todo> todoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // activity_main 연결

        // UI 초기화
        recyclerView = findViewById(R.id.recyclerViewTodos);
        editTextTodo = findViewById(R.id.editTextTodo);
        buttonAdd = findViewById(R.id.buttonAdd);
        // ViewModel 연결
        viewModel = new ViewModelProvider(this).get(TodoViewModel.class);

        // 추가 버튼 클릭 이벤트
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editTextTodo.getText().toString().trim();
                if (!content.isEmpty()) {
                    // 새 할 일 객체 추가
                    Todo todo = new Todo(content, false);
                    viewModel.insert(todo); // DB에 추가 -> LiveData 자동 갱신
                    // 입력창 비우기
                    editTextTodo.setText(""); // 입력창 초기화
                }
            }
        });

        // Adapter 설정
        adapter = new TodoAdapter( new ArrayList<>(),
                todo -> viewModel.delete(todo), // 삭제
                (todo, isChecked) -> { // 체크여부
                    todo.setDone(isChecked);
                    viewModel.update(todo);
                }
        );
        // RecyclerView 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // LiveData 옵저빙
        viewModel.getAllTodos().observe(this, todos -> {
            adapter.setTodoList(todos); // RecyclerView 데이터 갱신
        });

        // DB 내보내기 버튼 클릭 시 실행
        Button btnExport = findViewById(R.id.buttonExport);
        btnExport.setOnClickListener(v -> exportDatabase(MainActivity.this));
    }

    // DB 데이터 내보내는 메소드
    public void exportDatabase(Context context) {
        File dbFile = context.getDatabasePath("todo_database");  // ← 실제 DB 이름
        File exportDir = context.getExternalFilesDir(null); // 앱 외부 저장소

        if (exportDir != null && !exportDir.exists()) {
            exportDir.mkdirs();
        }

        File outFile = new File(exportDir, "todo_database_copy.db");

        try (FileChannel src = new FileInputStream(dbFile).getChannel();
             FileChannel dst = new FileOutputStream(outFile).getChannel()) {
            dst.transferFrom(src, 0, src.size());
            Log.d("EXPORT", "DB exported to: " + outFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}