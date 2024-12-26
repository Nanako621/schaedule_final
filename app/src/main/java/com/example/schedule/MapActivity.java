package com.example.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        // 設置按鈕點擊事件
        Button btnBackToMain = findViewById(R.id.btnIntentActivityB);
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳轉到主畫面
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 結束當前畫面，避免返回按鈕跳回
            }
        });
    }
}
