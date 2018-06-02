package kr.ac.inha.nsl.mindnavigator;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

public class EvaluationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        init();
    }

    //region Variables
    private int stressValue;
    static Object result = null;

    Button[] tabButtons;
    RadioGroup eventCompletionBtn;
    RadioGroup intervCompletion, intervRecommendation;
    private SeekBar stressLvl;
    ViewGroup eventLayout, interventionLayout;
    //endregion

    private void init() {
        tabButtons = new Button[]{
                findViewById(R.id.tab_event),
                findViewById(R.id.tab_intervention)
        };

        eventCompletionBtn = findViewById(R.id.event_cempletion);
        intervCompletion = findViewById(R.id.intervention_completion);
        stressLvl = findViewById(R.id.stressLvl);
        intervRecommendation = findViewById(R.id.intervention_recommendation);
        eventLayout = findViewById(R.id.event_layout);
        interventionLayout = findViewById(R.id.intervention_layout);

        stressLvl.getProgressDrawable().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null), PorterDuff.Mode.SRC_IN);
        stressLvl.getThumb().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null), PorterDuff.Mode.SRC_IN);
        stressLvl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                stressValue = progress;
                if (progress >= 0 && progress < 50) {
                    int slvl0Col = ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null);
                    stressLvl.getProgressDrawable().setColorFilter(slvl0Col, PorterDuff.Mode.SRC_IN);
                    stressLvl.getThumb().setColorFilter(slvl0Col, PorterDuff.Mode.SRC_IN);
                } else if (progress > 50 && progress < 80) {
                    int slvl1Col = ResourcesCompat.getColor(getResources(), R.color.slvl1_color, null);
                    stressLvl.getProgressDrawable().setColorFilter(slvl1Col, PorterDuff.Mode.SRC_IN);
                    stressLvl.getThumb().setColorFilter(slvl1Col, PorterDuff.Mode.SRC_IN);
                } else {
                    int slvl2Col = ResourcesCompat.getColor(getResources(), R.color.slvl2_color, null);
                    stressLvl.getProgressDrawable().setColorFilter(slvl2Col, PorterDuff.Mode.SRC_IN);
                    stressLvl.getThumb().setColorFilter(slvl2Col, PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(EvaluationActivity.this, String.valueOf(stressValue), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void tabClicked(View view) {
        eventLayout.setVisibility(View.GONE);
        interventionLayout.setVisibility(View.GONE);
        for (Button button : tabButtons)
            button.setBackgroundResource(R.color.bright_grey);

        switch (view.getId()) {
            case R.id.tab_event:
                tabButtons[0].setBackgroundResource(android.R.color.transparent);
                eventLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.tab_intervention:
                tabButtons[1].setBackgroundResource(android.R.color.transparent);
                interventionLayout.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    public void cancelClick(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
    }

    public void saveClick(View view) {
        switch (eventCompletionBtn.getCheckedRadioButtonId()) {
            case R.id.btn_did_event:
                //TODO: btn "I did it" clicked; Do smth
                break;
            case R.id.btn_didnt_do_event:
                //TODO: btn "I did not do it" clicked; Do smth
                break;
            default:
                break;
        }

        switch (intervCompletion.getCheckedRadioButtonId()) {
            case R.id.btn_did_intervention:
                //TODO: btn "I did it" clicked; Do smth
                break;
            case R.id.btn_didnt_do_intervention:
                //TODO: btn "I did not do it" clicked; Do smth
                break;
            default:
                break;
        }

        switch (intervRecommendation.getCheckedRadioButtonId()) {
            case R.id.btn_yes_intervention:
                //TODO: btn "Yes" clicked; Do smth
                break;
            case R.id.btn_no_intervention:
                //TODO: btn "No" clicked; Do smth
                break;
            default:
                break;
        }
        setResult(Activity.RESULT_OK);
        finish();
        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
    }
}
