package kr.ac.inha.nsl.mindnavigator;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EvaluationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setTitle("Event");
    }
}
