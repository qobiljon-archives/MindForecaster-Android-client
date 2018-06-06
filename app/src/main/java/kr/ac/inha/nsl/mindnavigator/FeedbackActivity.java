package kr.ac.inha.nsl.mindnavigator;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        init();
    }

    //region Variables
    private SeekBar expectedStressLevel;
    private SeekBar realStressLevel;
    private CheckBox eventDone;
    private EditText stressIncrReason;
    private ViewGroup stressIncrDetails;
    //endregion

    public void init() {
        expectedStressLevel = findViewById(R.id.expected_stresslvl_seekbar);
        realStressLevel = findViewById(R.id.real_stresslvl_seekbar);
        stressIncrDetails = findViewById(R.id.stress_incr_details_view);
        eventDone = findViewById(R.id.event_done_check);
        TextView eventTitle = findViewById(R.id.current_event_title);
        stressIncrReason = findViewById(R.id.stress_incr_reason_edit);

        eventTitle.setText(getResources().getString(R.string.current_event_title, EventActivity.event.getTitle()));

        expectedStressLevel.setEnabled(false);
        expectedStressLevel.setProgress(EventActivity.event.getStressLevel());
        int expectedStressColor = Tools.stressLevelToColor(EventActivity.event.getStressLevel());
        expectedStressLevel.getProgressDrawable().setColorFilter(expectedStressColor, PorterDuff.Mode.SRC_IN);
        expectedStressLevel.getThumb().setColorFilter(expectedStressColor, PorterDuff.Mode.SRC_IN);

        realStressLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                realStressLevel.getProgressDrawable().setColorFilter(Tools.stressLevelToColor(progress), PorterDuff.Mode.SRC_IN);
                realStressLevel.getThumb().setColorFilter(Tools.stressLevelToColor(progress), PorterDuff.Mode.SRC_IN);

                if (expectedStressLevel.getProgress() < realStressLevel.getProgress())
                    stressIncrDetails.setVisibility(View.VISIBLE);
                else
                    stressIncrDetails.setVisibility(View.GONE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void cancelClick(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
    }

    public void saveClick(View view) {
        if (Tools.isNetworkAvailable(this))
            Tools.execute(new MyRunnable(
                    getString(R.string.url_subm_feedback),
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
                        body.put("realStressLevel", realStressLevel.getProgress());
                        body.put("stressIncrReason", stressIncrReason.getText().toString());
                        body.put("done", eventDone.isChecked());

                        JSONObject res = new JSONObject(Tools.post(url, body));
                        switch (res.getInt("result")) {
                            case Tools.RES_OK:
                                runOnUiThread(new MyRunnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FeedbackActivity.this, "Feedback successfully submitted, thank you!", Toast.LENGTH_SHORT).show();

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
                                        Toast.makeText(FeedbackActivity.this, "Failed to submit the feedback.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case Tools.RES_SRV_ERR:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FeedbackActivity.this, "Failure occurred while processing the request. (SERVER SIDE)", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(FeedbackActivity.this, "Failed to proceed due to an error in connection with server.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        else
            Toast.makeText(this, "Please connect to a network first!", Toast.LENGTH_SHORT).show();
    }
}
