package com.example.todolist.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/*
Room: Android 공식 ORM(Object-Relational Mapping), SQLite 기반 DB
    - 구성요소: Entity(데이터 클래스), DAO(CRUD 쿼리 정의), Database(DB 인스턴스)
    - 특징
        - DB는 각 디바이스에 로컬로 생성 -> 즉, Room이 디바이스 내 앱 전용 저장소에 자동으로 SQLite 기반 DB 파일(.db)을 생성하고 저장
        - Room은 외부 앱에서 접근 불가능한 보안된 공간에 저장됨 -> 안전성 높음
        - 단, 앱을 삭제하면 내부 저장소에 있던 DB도 같이 삭제됨
        - 오프라인 저장 중심 앱에서 대부분 사용
    - 설치 방법
        - build.gradle 확인
            1) dependencies 블록 안에 추가
                dependencies {
                    --------------JAVA--------------
                    implementation "androidx.room:room-runtime:2.6.1"
                    annotationProcessor "androidx.room:room-compiler:2.6.1" // Java는 annotationProcessor 사용
                    implementation "androidx.lifecycle:lifecycle-viewmodel:2.7.0" // ViewModel
                    implementation "androidx.lifecycle:lifecycle-livedata:2.7.0" // LiveData
                    --------------Kotlin--------------
                    // Room (Java용)
                    implementation("androidx.room:room-runtime:2.6.1")
                    annotationProcessor("androidx.room:room-compiler:2.6.1")
                    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0") // ViewModel
                    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0") // LiveData
                }
            2) android 버전 확인 (java 8 미만일 경우에만 추가)
                android {
                    ...
                    compileOptions {
                        sourceCompatibility JavaVersion.VERSION_1_8
                        targetCompatibility JavaVersion.VERSION_1_8
                    }
                }
*/

// DB 스키마를 바꾸면 버전 바꿔야 함 (Room 특징)
@Database(entities = {Todo.class}, version = 2) // Entity 설정
public abstract class TodoDatabase extends RoomDatabase {
    private static TodoDatabase instance;
    public abstract TodoDAO todoDAO();
    public static synchronized TodoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            TodoDatabase.class, "todo_database")
                            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE) // WAL 비활성화 (빠른 확인용으로 개발 시에만 적용, 실제 배포할 때에는 WAL 활성화하기)
                            .fallbackToDestructiveMigration() // 버전 바뀌었을 때 DB 날리고 다시 생성 (초기 개발 단계에서 사용)
                            .build();
        }
        return instance;
    }
}
