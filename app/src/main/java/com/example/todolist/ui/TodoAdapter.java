package com.example.todolist.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.data.Todo;

import java.util.List;

/*
Adapter: RecyclerView와 데이터 사이에서 화면에 표시될 아이템을 연결해주는 역할
    - 각 줄마다 어떤 뷰에 어떤 데이터를 넣을지 결정하는 클래스
    - ex) RecyclerView: 도서관 책장, Adapter: 사서 (책 정리 및 관리)
    - 흐름 : XML -> MainActivity -> ViewModel -> Repository -> Room DB -> ViewModel -> MainActivity -> "Adapter" -> RecyclerView
    - 구성요소
        Listener: 사용자의 입력/이벤트 처리 (예: 클릭, 체크 등)
        ViewHolder: 데이터를 View에 표시 (바인딩)
*/
public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<Todo> todoList; // 할 일 목록 데이터 리스트

    /* ===========================================Listener========================================== */
    // 외부(MainActivity)로 이벤트를 전달하기 위한 콜백 인터페이스
    public interface OnTodoDeleteListener { void onDelete(Todo todo); } // 삭제
    public interface OnTodoCheckedChangeListener { void onCheckedChange(Todo todo, boolean isChecked); } // 체크(완료) 여부
    public interface OnTodoEditListener { void onEdit(Todo todo); } // 수정

    // 리스너 객체
    private final OnTodoDeleteListener deleteListener; // 삭제
    private final OnTodoCheckedChangeListener checkListener; // 체크
    private final OnTodoEditListener editListener; // 수정

    // 생성자: 데이터 초기화
    // 자주 쓰는 리스너는 생성자에 고정적으로 받는 것이 가독성과 안전성 면에서 좋고, 옵션적인 리스너는 setter로 설정해주는 방식이 유지보수에 좋음
    // 생성자와 분리하여 처리하는 방식: 가독성과 유지보수 용이, 단일 책임 원칙 (SRP), 확장성과 재사용성
    public TodoAdapter(List<Todo> todoList,
                       OnTodoDeleteListener deleteListener,
                       OnTodoCheckedChangeListener checkListener,
                       OnTodoEditListener editListener) {
        this.todoList = todoList;
        this.deleteListener = deleteListener;
        this.checkListener = checkListener;
        this.editListener = editListener;
    }


    /* ===========================================ViewHolder========================================== */
    // ViewHolder 정의 (아이템 하나당 뷰 참조를 저장)
    // RecyclerView의 한 줄(item_todo.xml)을 참조해서 보관하는 객체
    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox; // 체크 박스
        TextView textViewContent; // 할 일 내용
        TextView startDateView; // 시작일 나올 텍스트 view
        TextView endDateView; // 마감일 나올 텍스트 view
        Button btnEdit; // 수정 버튼
        Button btnDelete; // 삭제 버튼

        public TodoViewHolder(@NonNull View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBoxDone); // item_todo.xml의 CheckBox 객체
            startDateView = view.findViewById(R.id.startDateView); // item_todo.xml의 startDateView 객체
            endDateView = view.findViewById(R.id.endDateView); // item_todo.xml의 endDateView 객체
            textViewContent = view.findViewById(R.id.textViewContent); // item_todo.xml의 textViewContent 객체
            btnDelete = view.findViewById(R.id.buttonDelete); // item_todo.xml의 Button(Delete) 객체
            btnEdit = view.findViewById(R.id.buttonEdit); // item_todo.xml의 Button(Edit) 객체
        }

        //bind() 함수는 해당 줄의 Todo 데이터를 화면에 세팅
        public void bind(Todo todo) {
            checkBox.setOnCheckedChangeListener(null); // 리스너 초기화 (중복 방지)
            checkBox.setChecked(todo.isDone()); // item_todo.xml의 CheckBox 객체에 Todo에 저장된 done 데이터 입력
            textViewContent.setText(todo.getContent()); // item_todo.xml의 textViewContent 객체에 Todo에 저장된 content 데이터 입력
            startDateView.setText(todo.getStartDate()); // item_todo.xml의 startDateView 객체에 Todo에 저장된 startDate 데이터 입력
            endDateView.setText(todo.getEndDate()); // item_todo.xml의 endDateView 객체에 Todo에 저장된 endDate 데이터 입력
        }
    }

    // 아이템 레이아웃을 처음 생성할 때 호출
    // XML 레이아웃을 View로 만들어서 ViewHolder에 담음
    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
        LayoutInflater: XML 레이아웃 파일을 Java에서 사용할 수 있는 View 객체로 바꿔주는 클래스
            - inflate(): XML 레이아웃을 실제 View로 생성하는 메소드
            - Activity 내부에서는 setContentView 메소드를 통해 자동으로 inflate, 하지만 Fragment나 RecyclerView에서는 수동으로 직접 inflate
        */
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    // RecyclerView가 특정 위치의 데이터를 화면에 표시할 때 호출
    // bind(): UI에 집중, onBindViewHolder(): 이벤트 처리 집중 (이유: 중복 방지 및 명확한 역할 분리)
    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todoList.get(position);
        holder.bind(todo); // UI에 데이터 바인딩

        // 이벤트 처리 (각 줄마다 처리 필요)
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(todo); // 삭제 리스너 콜백 실행
        });
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (checkListener != null) checkListener.onCheckedChange(todo, isChecked); // 체크 리스너 콜백 실행
        });
        holder.btnEdit.setOnClickListener(v -> { // 수정 리스너 콜백 실행
            if (editListener != null) editListener.onEdit(todo);
        });
    }

    // RecyclerView가 아이템 몇 개 있는지 알아야 하므로 이 메소드가 필수
    @Override
    public int getItemCount() {
        return todoList != null ? todoList.size() : 0;
    }

    // 리스트 업데이트용 메소드
    // ViewModel이나 Repository에서 새 데이터를 받아올 때 호출
    public void setTodoList(List<Todo> list) {
        this.todoList = list;
        notifyDataSetChanged(); // 데이터 바뀐 걸 알려줌
    }
}

