package com.kaianchan.fillin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ResultActivity extends AppCompatActivity {

    TextView result_encourage, result_numOfCorr, result_numOfWrong;

    ImageView result_image;

    Button result_tohome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Result"); // Set specific page title

        result_image = findViewById(R.id.result_image);
        result_encourage = findViewById(R.id.result_encourage);
        result_numOfCorr = findViewById(R.id.result_numOfCorr);
        result_numOfWrong = findViewById(R.id.result_numOfWrong);
        result_tohome = findViewById(R.id.result_tohome);

        // Get user's information from LoginActivity
        Intent intent = getIntent();
        String winOrLost = intent.getStringExtra("winOrLost");
        int Corr = intent.getIntExtra("numOfCorr", 0);
        int Wrong = intent.getIntExtra("numOfWrong", 0);

        if (winOrLost.equals("lost")) {
            result_image.setImageDrawable(getResources().getDrawable(R.drawable.lost));
            result_encourage.setText("Keep going! You will do it!");
            result_numOfCorr.setText(Corr + " Questions");
            result_numOfWrong.setText(Wrong + " Questions");
        } else if (winOrLost.equals("win")) {
            result_image.setImageDrawable(getResources().getDrawable(R.drawable.win));
            result_encourage.setText("Conguatulations!");
            result_numOfCorr.setText(Corr + " Questions");
            result_numOfWrong.setText(Wrong + " Questions");
        } else {
            result_image.setImageDrawable(getResources().getDrawable(R.drawable.error));
            result_encourage.setText("Error...");
        }

//        result_tohome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
    }


}
