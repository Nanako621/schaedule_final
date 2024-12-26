package com.example.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CampusMapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_map);

        // 返回按鈕
        Button btnBackToMain = findViewById(R.id.btnIntentActivityB);
        btnBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(CampusMapActivity.this, CourseListActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
