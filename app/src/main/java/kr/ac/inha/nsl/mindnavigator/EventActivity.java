package kr.ac.inha.nsl.mindnavigator;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        init();
    }

    //region Variables
    private TextView eventTitle;
    private Switch switchAllDay;
    private TextView startDate, startTime, endDate, endTime;
    private SeekBar stressLvl;
    private LinearLayout inactiveLayout;
    private TextView positiveStress, negativeStress, dentKnowStress;
    private TextView stressCause;

    //endregion

    private void init() {
        //region Assign UI variables
        eventTitle = findViewById(R.id.txt_event_title);
        switchAllDay = findViewById(R.id.all_day_switch);
        startDate = findViewById(R.id.txt_event_start_date);
        startTime = findViewById(R.id.txt_event_start_time);
        endDate = findViewById(R.id.txt_event_end_date);
        endTime = findViewById(R.id.txt_event_end_time);

        stressLvl = findViewById(R.id.stressLvl);
        inactiveLayout = findViewById(R.id.layout_to_be_inactive);
        positiveStress = findViewById(R.id.stressor_positive);
        negativeStress = findViewById(R.id.stressor_negative);
        dentKnowStress = findViewById(R.id.stressor_dont_know);
        stressCause = findViewById(R.id.txt_stress_cause);
        //endregion


        //region Set the selected day fields (Date & Time)
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTimeInMillis(getIntent().getLongExtra("selectedDayMillis", 0));

        startDate.setText(String.format(Locale.US,
                "%s, %02d %s",
                selectedCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()),
                selectedCal.get(Calendar.DAY_OF_MONTH),
                selectedCal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        ));

        startTime.setText(String.format(Locale.US,
                "%02d:%02d",
                selectedCal.get(Calendar.HOUR),
                selectedCal.get(Calendar.MINUTE)));

        endDate.setText(String.format(Locale.US,
                "%s, %02d %s",
                selectedCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()),
                selectedCal.get(Calendar.DAY_OF_MONTH),
                selectedCal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        ));

        selectedCal.add(Calendar.HOUR, 1);
        endTime.setText(String.format(Locale.US,
                "%02d:%02d",
                selectedCal.get(Calendar.HOUR),
                selectedCal.get(Calendar.MINUTE)));

        //endregion

        stressLvl.getProgressDrawable().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null), PorterDuff.Mode.SRC_IN);
        stressLvl.getThumb().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null), PorterDuff.Mode.SRC_IN);
        stressLvl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress > 0 && progress < 50){
                    int slvl0Col = ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null);
                    stressLvl.getProgressDrawable().setColorFilter(slvl0Col, PorterDuff.Mode.SRC_IN);
                    stressLvl.getThumb().setColorFilter(slvl0Col, PorterDuff.Mode.SRC_IN);
                    inactiveLayout.setVisibility(View.GONE);
                }
                else if(progress > 50 && progress < 80){
                    int slvl1Col = ResourcesCompat.getColor(getResources(), R.color.slvl1_color, null);
                    stressLvl.getProgressDrawable().setColorFilter(slvl1Col, PorterDuff.Mode.SRC_IN);
                    stressLvl.getThumb().setColorFilter(slvl1Col, PorterDuff.Mode.SRC_IN);
                    inactiveLayout.setVisibility(View.VISIBLE);
                } else{
                    int slvl2Col = ResourcesCompat.getColor(getResources(), R.color.slvl2_color, null);
                    stressLvl.getProgressDrawable().setColorFilter(slvl2Col, PorterDuff.Mode.SRC_IN);
                    stressLvl.getThumb().setColorFilter(slvl2Col, PorterDuff.Mode.SRC_IN);
                    inactiveLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        switchAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startTime.setVisibility(View.GONE);
                    endTime.setVisibility(View.GONE);
                } else {
                    startTime.setVisibility(View.VISIBLE);
                    endTime.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void moreOptionsClick(View view) {
        findViewById(R.id.text_more_event_options).setVisibility(View.GONE);
        findViewById(R.id.more_options_layout).setVisibility(View.VISIBLE);
    }

    public void expandStressLevelClick(View view) {
        LinearLayout moreStressLvl = findViewById(R.id.stress_lvl_more);

        if (moreStressLvl.getVisibility() == View.VISIBLE) {
            moreStressLvl.setVisibility(View.GONE);
        } else
            moreStressLvl.setVisibility(View.VISIBLE);
    }

    public void interventionsClick(View view) {
        Intent intent = new Intent(this, InterventionsActivity.class);
        startActivity(intent);
    }

    public void evaluationClick(View view) {
        Intent intent = new Intent(this, EvaluationActivity.class);
        startActivity(intent);
    }

    public void cancelClick(View view) {
        finish();
    }

    public void saveClick(View view) {
        finish();
    }
}
