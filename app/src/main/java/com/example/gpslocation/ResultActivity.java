package com.example.gpslocation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        TextView resultTextView;
        resultTextView=findViewById(R.id.textViewResult);
        if (getIntent().getExtras() != null) {
            String value = getIntent().getStringExtra("values");
            resultTextView.setText(value);

        }
    }
}