package kr.ac.inha.nsl.mindnavigator;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        init();
    }

    //region Variables
    private int stressLvlSun;
    private int stressLvlToday;

    SeekBar strsLvlSunday;
    SeekBar strsLvlToday;
    ViewGroup strsReasonView;

    public void init() {

        stressLvlSun = 45;
        stressLvlToday = 85;
        strsLvlSunday = findViewById(R.id.stressLvl_sunday);
        strsLvlToday = findViewById(R.id.stressLvl_today);
        strsReasonView = findViewById(R.id.stress_reason_view);

        strsLvlSunday.setProgress(stressLvlSun);
        strsLvlSunday.setEnabled(false);
        strsLvlToday.setProgress(stressLvlToday);
        strsLvlToday.setEnabled(false);

        if (stressLvlSun >= 0 && stressLvlSun < 50) {
            int slvl0Col = ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null);
            strsLvlSunday.getProgressDrawable().setColorFilter(slvl0Col, PorterDuff.Mode.SRC_IN);
            strsLvlSunday.getThumb().setColorFilter(slvl0Col, PorterDuff.Mode.SRC_IN);
        } else if (stressLvlSun > 50 && stressLvlSun < 80) {
            int slvl1Col = ResourcesCompat.getColor(getResources(), R.color.slvl1_color, null);
            strsLvlSunday.getProgressDrawable().setColorFilter(slvl1Col, PorterDuff.Mode.SRC_IN);
            strsLvlSunday.getThumb().setColorFilter(slvl1Col, PorterDuff.Mode.SRC_IN);
        } else {
            int slvl2Col = ResourcesCompat.getColor(getResources(), R.color.slvl2_color, null);
            strsLvlSunday.getProgressDrawable().setColorFilter(slvl2Col, PorterDuff.Mode.SRC_IN);
            strsLvlSunday.getThumb().setColorFilter(slvl2Col, PorterDuff.Mode.SRC_IN);
        }

        if (stressLvlToday >= 0 && stressLvlToday < 50) {
            int slvl0Col = ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null);
            strsLvlToday.getProgressDrawable().setColorFilter(slvl0Col, PorterDuff.Mode.SRC_IN);
            strsLvlToday.getThumb().setColorFilter(slvl0Col, PorterDuff.Mode.SRC_IN);
        } else if (stressLvlToday > 50 && stressLvlToday < 80) {
            int slvl1Col = ResourcesCompat.getColor(getResources(), R.color.slvl1_color, null);
            strsLvlToday.getProgressDrawable().setColorFilter(slvl1Col, PorterDuff.Mode.SRC_IN);
            strsLvlToday.getThumb().setColorFilter(slvl1Col, PorterDuff.Mode.SRC_IN);
        } else {
            int slvl2Col = ResourcesCompat.getColor(getResources(), R.color.slvl2_color, null);
            strsLvlToday.getProgressDrawable().setColorFilter(slvl2Col, PorterDuff.Mode.SRC_IN);
            strsLvlToday.getThumb().setColorFilter(slvl2Col, PorterDuff.Mode.SRC_IN);
        }

        if (stressLvlToday > stressLvlSun) {
            strsReasonView.setVisibility(View.VISIBLE);
        } else strsReasonView.setVisibility(View.GONE);

    }

    //endregion
    public void cancelClick(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
    }

    public void saveClick(View view) {
        setResult(Activity.RESULT_OK);
        finish();
        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
    }
}
