package com.example.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Detail extends AppCompatActivity {

    TextView time;
    TextView clsNum;
    TextView sub;
    TextView teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_press);  // 設置佈局文件

        // 取得 Intent 傳遞過來的數據
        Intent intent = getIntent();
        String subject = intent.getStringExtra("name");
        String timeValue = intent.getStringExtra("day");
        String clsNumValue = intent.getStringExtra("time");
        String teacherName = intent.getStringExtra("teacher");

        // 初始化界面上的 TextView 控件並顯示數據
        sub = findViewById(R.id.sub);
        sub.setText(subject);

        time = findViewById(R.id.time);
        time.setText(timeValue);

        clsNum = findViewById(R.id.clsNum);
        clsNum.setText(clsNumValue);

        teacher = findViewById(R.id.teacher);
        teacher.setText(teacherName);

        // 設置返回按鈕
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 創建返回的 Intent 並啟動 MainActivity
                Intent backIntent = new Intent(Detail.this, MainActivity.class);
                startActivity(backIntent);
                finish();  // 結束當前頁面
            }
        });

        setFinishOnTouchOutside(true);  // 點擊外部區域結束 Activity
    }
}