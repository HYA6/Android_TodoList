package com.example.todolist.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.data.Todo;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
MVVM 패턴: Model-View-ViewModel 패턴
    - Model: 데이터, 비즈니스 로직 담당 (ex.Room DB, Repository, API 등)
    - View: 사용자 UI 담당 (ex.Activity, Fragment, XML 등)
    - ViewModel: View와 Model의 중간 다리 역할, 상태 관리 담당 (ex.ViewModel 클래스, LiveData 등)
    - 흐름: 사용자 입력 -> View -> ViewModel(UI 상태 저장 및 처리) -> Model(실제 데이터 처리) -> ViewModel -> View
    - 장점: 코드 분리, 재사용성 증가, 테스트 용이, 수명 관리 등
*/
public class MainActivity extends AppCompatActivity {

    private TodoViewModel viewModel; // 뷰모델
    private TodoAdapter adapter; // 어댑터
    private EditText editTextTodo; // 할 일 내용
    private Button buttonStartDate; // 시작일 추가 버튼
    private String startDate; // 시작일
    private TextView startDateText; // 시작일 나올 텍스트
    private Button buttonEndDate; // 마감일 추가 버튼
    private String endDate; // 마감일
    private TextView endDateText; // 시작일 나올 텍스트
    private Button buttonAdd; //추가 버튼
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // activity_main 연결

        // UI 초기화
        editTextTodo = findViewById(R.id.editTextTodo); // 내용 view
        buttonStartDate = findViewById(R.id.buttonStartDate); // 시작일 추가 버튼
        startDateText = findViewById(R.id.startDateText); // 시작일 나올 텍스트 view
        buttonEndDate = findViewById(R.id.buttonEndDate); // 마감일 추가 버튼
        endDateText = findViewById(R.id.endDateText); // 마감일 나올 텍스트 view
        buttonAdd = findViewById(R.id.buttonAdd); // 일정 추가 버튼
        recyclerView = findViewById(R.id.recyclerViewTodos); // recyclerview

        // ViewModel 연결
        viewModel = new ViewModelProvider(this).get(TodoViewModel.class);


        // 시작일 선택 이벤트
        buttonStartDate.setOnClickListener(v -> {
            showDatePicker(MainActivity.this, null, endDate, selectedDate -> {
                startDate = selectedDate;
                startDateText.setText(startDate);
            });
        });
        // 마감일 선택 이벤트
        buttonEndDate.setOnClickListener(v -> {
            if (startDate != null) {
                showDatePicker(MainActivity.this, startDate, null, selectedDate -> {
                    endDate = selectedDate;
                    endDateText.setText(endDate);
                });
            } else {
                Toast.makeText(this, "먼저 시작일을 선택하세요", Toast.LENGTH_SHORT).show();
            }
        });

        // 추가 버튼 클릭 이벤트
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editTextTodo.getText().toString().trim();
                if (startDate == null || endDate == null || content.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("추가 불가")
                            .setMessage("날짜가 선택되지 않았거나 내용이 입력되지 않았습니다.")
                            .setNeutralButton("닫기", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dlg, int sumthin) {
                                }
                            })
                            .show(); // 팝업창 보여줌
                } else {
                    // 새 할 일 객체 추가
                    Todo todo = new Todo(content, false, startDate, endDate);
                    viewModel.insert(todo); // DB에 추가 -> LiveData 자동 갱신
                    // 입력창, 시작일, 마감일 비우기
                    editTextTodo.setText("");
                    editTextTodo.setHint("할 일을 입력하세요");
                    startDateText.setText("날짜를 선택하세요"); // 시작일 텍스트뷰 초기화
                    endDateText.setText("날짜를 선택하세요"); // 마감일 텍스트뷰 초기화
                }
            }
        });

        // Adapter 설정
        adapter = new TodoAdapter( new ArrayList<>(),
                todo -> viewModel.delete(todo), // 삭제
                (todo, isChecked) -> { // 체크여부
                    todo.setDone(isChecked);
                    viewModel.update(todo);
                },
                todo -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("할 일 수정");

                    // 레이아웃 수동 구성
                    LinearLayout layout = new LinearLayout(MainActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setPadding(50, 40, 50, 10);

                    // 제목 입력창
                    final EditText inputContent = new EditText(MainActivity.this);
                    inputContent.setHint("내용");
                    inputContent.setText(todo.getContent());
                    layout.addView(inputContent);

                    // 시작일 버튼
                    final Button startDateButton = new Button(MainActivity.this);
                    startDateButton.setText("시작일: " + (todo.getStartDate() != null ? todo.getStartDate() : "선택 안 됨"));
                    layout.addView(startDateButton);

                    // 마감일 버튼
                    final Button endDateButton = new Button(MainActivity.this);
                    endDateButton.setText("마감일: " + (todo.getEndDate() != null ? todo.getEndDate() : "선택 안 됨"));
                    layout.addView(endDateButton);

                    final String[] startDate = {todo.getStartDate()};
                    final String[] endDate = {todo.getEndDate()};

                    // 시작일 선택 이벤트
                    startDateButton.setOnClickListener(v -> {
                        showDatePicker(MainActivity.this, null, endDate[0], selected -> {
                            startDate[0] = selected;
                            startDateButton.setText("시작일: " + selected);
                        });
                    });

                    // 마감일 선택 이벤트
                    endDateButton.setOnClickListener(v -> {
                        if (startDate[0] != null) {
                            showDatePicker(MainActivity.this, startDate[0], null, selected -> {
                                endDate[0] = selected;
                                endDateButton.setText("마감일: " + selected);
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "시작일을 먼저 선택하세요", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setView(layout);

                    // [수정] 버튼
                    builder.setPositiveButton("수정", (dialog, which) -> {
                        String newContent = inputContent.getText().toString().trim();
                        if (!newContent.isEmpty()) {
                            todo.setContent(newContent);
                            todo.setStartDate(startDate[0]);
                            todo.setEndDate(endDate[0]);
                            viewModel.update(todo);
                        }
                    });

                    builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
                    builder.show();
                } // 수정
        );
        // RecyclerView 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // LiveData 옵저빙
        viewModel.getAllTodos().observe(this, todos -> {
            adapter.setTodoList(todos); // RecyclerView 데이터 갱신
        });

        // DB 내보내기 버튼 클릭 시 실행
//        Button btnExport = findViewById(R.id.buttonExport);
//        btnExport.setOnClickListener(v -> exportDatabase(MainActivity.this));
    }

    // 데이터피커 이벤트
    private void showDatePicker(Context context, @Nullable String minDateString, @Nullable String maxDateString, OnDateSelectedListener listener) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%04d/%02d/%02d", selectedYear, selectedMonth + 1, selectedDay);
                    listener.onDateSelected(selectedDate); // 선택된 날짜 콜백
                },
                year, month, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        try {
            if (minDateString != null) { // 최소 날짜
                Date min = sdf.parse(minDateString);
                if (min != null) datePickerDialog.getDatePicker().setMinDate(min.getTime());
            }
            if (maxDateString != null) { // 최대 날짜
                Date max = sdf.parse(maxDateString);
                if (max != null) datePickerDialog.getDatePicker().setMaxDate(max.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        datePickerDialog.show();
    }

    public interface OnDateSelectedListener {
        void onDateSelected(String date);
    }


    // DB 데이터 내보내는 메소드
//    public void exportDatabase(Context context) {
//        File dbFile = context.getDatabasePath("todo_database");  // ← 실제 DB 이름
//        File exportDir = context.getExternalFilesDir(null); // 앱 외부 저장소
//
//        if (exportDir != null && !exportDir.exists()) {
//            exportDir.mkdirs();
//        }
//
//        File outFile = new File(exportDir, "todo_database_copy.db");
//
//        try (FileChannel src = new FileInputStream(dbFile).getChannel();
//             FileChannel dst = new FileOutputStream(outFile).getChannel()) {
//            dst.transferFrom(src, 0, src.size());
//            Log.d("EXPORT", "DB exported to: " + outFile.getAbsolutePath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}