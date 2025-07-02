package com.example.todolist.ui;
import android.util.Log;
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

    // 생성자: 데이터 초기화
    public TodoAdapter(List<Todo> todoList) {
        this.todoList = todoList;
    }

    /* ===========================================Listener========================================== */
    // 외부(MainActivity)로 이벤트를 전달하기 위한 콜백 인터페이스
    public interface OnTodoDeleteListener { void onDelete(Todo todo); } // 삭제
    public interface OnTodoCheckedChangeListener { void onCheckedChange(Todo todo, boolean isChecked); } // 체크(완료) 여부

    // 리스터 객체
    private OnTodoDeleteListener deleteListener; // 삭제
    private OnTodoCheckedChangeListener checkListener; // 체크

    // 리스너 처리 메소드
    /*
     생성자에서 한 번에 처리할 수도 있지만 나눠 쓰는 방식을 선호
     이유: 가독성과 유지보수 용이성, 단일 책임 원칙 (SRP), 확장성과 재사용성
    */
    public void setOnDeleteClickListener(OnTodoDeleteListener listener) {
        this.deleteListener = listener;
    }
    public void setOnCheckedChangeListener(OnTodoCheckedChangeListener listener) {
        this.checkListener = listener;
    }

    /* ===========================================ViewHolder========================================== */
    // ViewHolder 정의 (아이템 하나당 뷰 참조를 저장)
    // RecyclerView의 한 줄(item_todo.xml)을 참조해서 보관하는 객체
    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;
        Button btnDelete;

        public TodoViewHolder(@NonNull View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBoxDone); // item_todo.xml의 CheckBox 객체
            textView = view.findViewById(R.id.textViewContent); // item_todo.xml의 TextView 객체
            btnDelete = view.findViewById(R.id.buttonDelete); // item_todo.xml의 Button(Delete) 객체
        }

        //bind() 함수는 해당 줄의 Todo 데이터를 화면에 세팅
        public void bind(Todo todo, OnTodoDeleteListener deleteListener) {
            textView.setText(todo.getContent()); // item_todo.xml의 CheckBox 객체에 Todo에 저장된 content 데이터 입력
            checkBox.setChecked(todo.isDone()); // item_todo.xml의 TextView 객체에 Todo에 저장된 done 데이터 입력
            btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) deleteListener.onDelete(todo);
            });
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
    // 해당 위치의 Todo 객체를 꺼내서 bind()로 View에 반영
    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todoList.get(position);
        holder.bind(todo, deleteListener);
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

