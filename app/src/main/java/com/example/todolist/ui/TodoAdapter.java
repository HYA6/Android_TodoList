package com.example.todolist.ui;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.data.Todo;

import java.util.List;
public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<Todo> todoList; // 할 일 목록 데이터 리스트

    // 생성자: 데이터 받기
    public TodoAdapter(List<Todo> todoList) {
        this.todoList = todoList;
    }

    // ViewHolder 정의 (아이템 하나당 뷰 참조를 저장)
    // RecyclerView의 한 줄(item_todo.xml)을 참조해서 보관하는 객체
    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;

        public TodoViewHolder(@NonNull View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBoxDone); // item_todo.xml의 CheckBox 객체
            textView = view.findViewById(R.id.textViewContent); // item_todo.xml의 TextView 객체
        }

        //bind() 함수는 해당 줄의 Todo 데이터를 화면에 세팅
        public void bind(Todo todo) {
            textView.setText(todo.getContent()); // item_todo.xml의 CheckBox 객체에 Todo에 저장된 done 데이터 입력
            checkBox.setChecked(todo.isDone()); // item_todo.xml의 TextView 객체에 Todo에 저장된 content 데이터 입력
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
            - Activity 내부에서는 setContentView 메소드를 통해 자동으로 인플레이트, 하지만 Fragment나 RecyclerView에서는 수동으로 직접 인플레이트
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
        holder.bind(todo);
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

