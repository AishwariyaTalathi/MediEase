package edu.csulb.mediease;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void ocr(View view) {
        startActivity(new Intent(StartActivity.this, OCRActivity.class));
    }

    public void alarms(View view) {
        startActivity(new Intent(StartActivity.this, AlarmsActivity.class));
    }

    public void login(View view) {
        startActivity(new Intent(StartActivity.this, SignUpActivity.class));
    }
}
