package kr.ac.inha.nsl.mindnavigator;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class EvaluationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        init();
    }

    //region Variables
    private Button[] tabButtons;
    private CheckBox eventCompletionCheck, intervCompletionCheck, intervSharingCheck, intervBeforeEventCheck;
    private SeekBar realStressLevel, intervEffectiveness;
    private ViewGroup eventLayout, interventionLayout;
    //endregion

    private void init() {
        tabButtons = new Button[]{
                findViewById(R.id.tab_event),
                findViewById(R.id.tab_intervention)
        };

        eventCompletionCheck = findViewById(R.id.event_cempletion_check);
        intervCompletionCheck = findViewById(R.id.intervention_completion);
        realStressLevel = findViewById(R.id.real_stress_level_seek);
        intervSharingCheck = findViewById(R.id.intervention_sharing_check);
        eventLayout = findViewById(R.id.event_layout);
        interventionLayout = findViewById(R.id.intervention_layout);
        intervEffectiveness = findViewById(R.id.intervention_effectiveness);
        intervBeforeEventCheck = findViewById(R.id.interv_before_event_check);
        TextView eventTitle = findViewById(R.id.event_title_text_view);
        eventTitle.setText(getString(R.string.current_event_title, EventActivity.event.getTitle()));
        TextView intervTitle = findViewById(R.id.intervention_title_text);
        intervTitle.setText(getString(R.string.current_interv_title, EventActivity.event.getIntervention()));

        realStressLevel.getProgressDrawable().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.green, null), PorterDuff.Mode.SRC_IN);
        realStressLevel.getThumb().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.green, null), PorterDuff.Mode.SRC_IN);
        realStressLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                realStressLevel.getProgressDrawable().setColorFilter(Tools.stressLevelToColor(progress), PorterDuff.Mode.SRC_IN);
                realStressLevel.getThumb().setColorFilter(Tools.stressLevelToColor(progress), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void tabClicked(View view) {
        if (EventActivity.event.getIntervention().length() == 0) {
            Toast.makeText(this, "Intervention was not selected/created for this event.", Toast.LENGTH_LONG).show();
            return;
        }

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
        if (Tools.isNetworkAvailable(this))
            Tools.execute(new MyRunnable(
                    getString(R.string.url_eval_subm),
                    SignInActivity.loginPrefs.getString(SignInActivity.username, null),
                    SignInActivity.loginPrefs.getString(SignInActivity.password, null)
            ) {
                @Override
                public void run() {
                    String url = (String) args[0];
                    String username = (String) args[1];
                    String password = (String) args[2];

                    JSONObject body = new JSONObject();
                    try {
                        body.put("username", username);
                        body.put("password", password);
                        body.put("eventId", EventActivity.event.getEventId());
                        body.put("interventionName", EventActivity.event.getIntervention());
                        body.put("startTime", EventActivity.event.getStartTime().getTimeInMillis());
                        body.put("endTime", EventActivity.event.getEndTime().getTimeInMillis());
                        body.put("realStressLevel", realStressLevel.getProgress());
                        body.put("eventDone", eventCompletionCheck.isChecked());
                        body.put("interventionDone", intervCompletionCheck.isChecked());
                        body.put("interventionDoneBefore", intervBeforeEventCheck.isChecked());
                        body.put("sharedIntervention", intervSharingCheck.isChecked());
                        body.put("intervEffectiveness", intervEffectiveness.getProgress());

                        JSONObject res = new JSONObject(Tools.post(url, body));
                        switch (res.getInt("result")) {
                            case Tools.RES_OK:
                                runOnUiThread(new MyRunnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EvaluationActivity.this, "Evaluation successfully submitted, thank you!", Toast.LENGTH_SHORT).show();

                                        setResult(Activity.RESULT_OK);
                                        finish();
                                        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
                                    }
                                });
                                break;
                            case Tools.RES_FAIL:
                                runOnUiThread(new MyRunnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EvaluationActivity.this, "Failed to submit the evaluation.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case Tools.RES_SRV_ERR:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EvaluationActivity.this, "Failure occurred while processing the request. (SERVER SIDE)", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(EvaluationActivity.this, "Failed to proceed due to an error in connection with server.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        else
            Toast.makeText(this, "Please connect to a network first!", Toast.LENGTH_SHORT).show();
    }
}
