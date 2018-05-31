package kr.ac.inha.nsl.mindnavigator;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setTitle("Event");
        init();
    }

    //region Variables
    TextView eventTitle;
    Switch switchAllDay;
    TextView startDate, startTime, endDate, endTime;
    SeekBar stressLvl;
    LinearLayout inactiveLayout;
    TextView positiveStressor, negativeStressor, dontKnowStressor;
    TextView stressCause;
    Button saveBtn, cancelBtn;

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
        positiveStressor = findViewById(R.id.stressor_positive);
        negativeStressor = findViewById(R.id.stressor_negative);
        dontKnowStressor = findViewById(R.id.stressor_dont_know);
        stressCause = findViewById(R.id.txt_stress_cause);

        saveBtn = findViewById(R.id.btn_save);
        cancelBtn = findViewById(R.id.btn_cancel);
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
                switch (progress) {
                    case 0:
                        int slvl0Col = ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null);
                        stressLvl.getProgressDrawable().setColorFilter(slvl0Col, PorterDuff.Mode.SRC_IN);
                        stressLvl.getThumb().setColorFilter(slvl0Col, PorterDuff.Mode.SRC_IN);
                        inactiveLayout.setVisibility(View.GONE);
                        break;
                    case 1:
                        int slvl1Col = ResourcesCompat.getColor(getResources(), R.color.slvl1_color, null);
                        stressLvl.getProgressDrawable().setColorFilter(slvl1Col, PorterDuff.Mode.SRC_IN);
                        stressLvl.getThumb().setColorFilter(slvl1Col, PorterDuff.Mode.SRC_IN);
                        inactiveLayout.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        int slvl2Col = ResourcesCompat.getColor(getResources(), R.color.slvl2_color, null);
                        stressLvl.getProgressDrawable().setColorFilter(slvl2Col, PorterDuff.Mode.SRC_IN);
                        stressLvl.getThumb().setColorFilter(slvl2Col, PorterDuff.Mode.SRC_IN);
                        inactiveLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
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
                if(isChecked){
                    startTime.setVisibility(View.GONE);
                    endTime.setVisibility(View.GONE);
                } else{
                    startTime.setVisibility(View.VISIBLE);
                    endTime.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void moreOptionsClick(View view) {
        LinearLayout btnMore = findViewById(R.id.btn_more_layout);
        LinearLayout moreOptLayout = findViewById(R.id.more_options_layout);
        btnMore.setVisibility(View.GONE);
        moreOptLayout.setVisibility(View.VISIBLE);
    }

    public void stressLvlMoreClick(View view) {
        LinearLayout moreStressLvl = findViewById(R.id.stress_lvl_more);

        if (moreStressLvl.getVisibility() == View.VISIBLE) {
            moreStressLvl.setVisibility(View.GONE);
        } else
            moreStressLvl.setVisibility(View.VISIBLE);
    }

    public void cancelClick(View view) {
        finish();
    }

    public void interVentionsClick(View view) {
        Intent intent = new Intent(this, InterventionsActivity.class);
        startActivity(intent);
    }


}
