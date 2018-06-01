package kr.ac.inha.nsl.mindnavigator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class EvaluationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        init();
    }

    //region Variables
    Button[] tabButtons;
    Button[] btnEventDidNot;
    Button[] btnIntervDidNot, btnIntervYesNo;
    LinearLayout eventLayout, interventionLayout;

    //endregion

    private void init() {

        tabButtons = new Button[]{
                findViewById(R.id.tab_event),
                findViewById(R.id.tab_intervention)
        };

        btnEventDidNot = new Button[]{
                findViewById(R.id.btn_did_event),
                findViewById(R.id.btn_didnt_do_event)
        };

        btnIntervDidNot = new Button[]{
                findViewById(R.id.btn_did_intervention),
                findViewById(R.id.btn_didnt_do_intervention)
        };

        btnIntervYesNo = new Button[]{
                findViewById(R.id.btn_yes_intervention),
                findViewById(R.id.btn_no_intervention)
        };

        eventLayout = findViewById(R.id.event_layout);
        interventionLayout = findViewById(R.id.intervention_layout);

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

    public void btnDidNotEventClick(View view) {
        for (Button button : btnEventDidNot)
            button.setBackgroundResource(R.drawable.bg_box_unchecked_view);

        switch (view.getId()) {
            case R.id.btn_did_event:
                btnEventDidNot[0].setBackgroundResource(R.drawable.bg_box_checked_view);
                break;
            case R.id.btn_didnt_do_event:
                btnEventDidNot[1].setBackgroundResource(R.drawable.bg_box_checked_view);
                break;
            default:
                break;
        }
    }

    public void btnDidNotIntervClick(View view) {
        for (Button button : btnIntervDidNot)
            button.setBackgroundResource(R.drawable.bg_box_unchecked_view);

        switch (view.getId()) {
            case R.id.btn_did_intervention:
                btnIntervDidNot[0].setBackgroundResource(R.drawable.bg_box_checked_view);
                break;
            case R.id.btn_didnt_do_intervention:
                btnIntervDidNot[1].setBackgroundResource(R.drawable.bg_box_checked_view);
                break;
            default:
                break;
        }
    }

    public void btnYesNoIntervionClick(View view) {
        for (Button button : btnIntervYesNo)
            button.setBackgroundResource(R.drawable.bg_box_unchecked_view);

        switch (view.getId()) {
            case R.id.btn_yes_intervention:
                btnIntervYesNo[0].setBackgroundResource(R.drawable.bg_box_checked_view);
                break;
            case R.id.btn_no_intervention:
                btnIntervYesNo[1].setBackgroundResource(R.drawable.bg_box_checked_view);
                break;
            default:
                break;
        }
    }

    public void cancelClick(View view) {
        finish();
    }

    public void saveClick(View view) {
    }


}
