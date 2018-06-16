package kr.ac.inha.nsl.mindnavigator;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class InterventionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interventions);
        init();
    }

    //region Variables
    boolean saveIntervention = false;
    static String result = null;
    static short resultSchedule = 0;

    static Event event;
    EditText interv_text;
    View interv_choice;
    ViewGroup interv_list, schedulingView;
    RadioGroup intervScheduling;
    Button[] tabButtons;

    private RadioButton customNotifRadioButton;

    static String customReminderText; // customized reminder text
    private int customReminderMinutes; //customized reminder minutes
    static String customNotifTimeTxt; //customized reminder time-text for notification text ( Ex.: 2 day(s) )

    //endregion

    private void init() {
        interv_choice = findViewById(R.id.intervention_choice);
        interv_text = findViewById(R.id.intervention_text);
        interv_list = findViewById(R.id.interventions_list);
        schedulingView = findViewById(R.id.interv_scheduling_view);
        intervScheduling = findViewById(R.id.interv_scheduling_group);
        customNotifRadioButton = findViewById(R.id.option_custom);
        tabButtons = new Button[]{
                findViewById(R.id.button_self_intervention),
                findViewById(R.id.button_systems_intervention),
                findViewById(R.id.button_peer_interventions)
        };

        TextView eventTitle = findViewById(R.id.event_title_text_view);
        eventTitle.setText(getString(R.string.current_event_title, getIntent().getStringExtra("eventTitle")));

        //region For hiding a soft keyboard
        final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(interv_text.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }

        schedulingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imm != null) {
                    imm.hideSoftInputFromWindow(interv_text.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
            }
        });
        eventTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert imm != null;
                imm.hideSoftInputFromWindow(interv_text.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        });
        //endregion

        if (getIntent().hasExtra("eventId")) {
            //Editing and existing event
            event = Event.getEventById(getIntent().getLongExtra("eventId", 0));
            interv_text.setText(event.getIntervention());
            eventTitle.setText(getString(R.string.current_event_title, event.getTitle()));
            switch (event.getInterventionReminder()) {
                case 0:
                    intervScheduling.check(R.id.option_none);
                    break;
                case -1440:
                    intervScheduling.check(R.id.option_day_before);
                case -60:
                    intervScheduling.check(R.id.option_hour_before);
                    break;
                case -30:
                    intervScheduling.check(R.id.option_30mins_before);
                case -10:
                    intervScheduling.check(R.id.option_10mins_before);
                    break;
                case 1440:
                    intervScheduling.check(R.id.option_day_after);
                case 60:
                    intervScheduling.check(R.id.option_hour_after);
                    break;
                case 30:
                    intervScheduling.check(R.id.option_30mins_after);
                case 10:
                    intervScheduling.check(R.id.option_10mins_after);
                    break;
                default:
                    intervScheduling.check(R.id.option_custom);
                    customNotifRadioButton.setText(customReminderText);
                    customNotifRadioButton.setVisibility(View.VISIBLE);
                    break;
            }
        }

        interv_text.setVisibility(View.GONE);
        interv_choice.setVisibility(View.GONE);
        tabButtons[0].callOnClick();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void tabClicked(View view) {
        // Clear out visibility and previously set button color
        interv_text.setVisibility(View.GONE);
        interv_choice.setVisibility(View.GONE);
        schedulingView.setVisibility(View.GONE);
        for (Button button : tabButtons)
            button.setBackgroundResource(R.drawable.bg_interv_method_unchecked_view);

        // Act upon the click event
        switch (view.getId()) {
            case R.id.button_self_intervention:
                tabButtons[0].setBackgroundResource(R.drawable.bg_interv_method_checked_view);
                interv_text.setText("");
                intervScheduling.check(R.id.option_none);
                interv_text.setVisibility(View.VISIBLE);
                schedulingView.setVisibility(View.VISIBLE);
                saveIntervention = true;
                break;
            case R.id.button_systems_intervention:
                tabButtons[1].setBackgroundResource(R.drawable.bg_interv_method_checked_view);
                interv_choice.setVisibility(View.VISIBLE);
                interv_list.removeAllViews();
                Tools.toggle_keyboard(this, interv_text, false);
                if (Tools.isNetworkAvailable(this))
                    Tools.execute(new MyRunnable(
                            this,
                            SignInActivity.loginPrefs.getString(SignInActivity.username, null),
                            SignInActivity.loginPrefs.getString(SignInActivity.password, null),
                            getString(R.string.url_interv_system_fetch, getString(R.string.server_ip))
                    ) {
                        @Override
                        public void run() {
                            String username = (String) args[0];
                            String password = (String) args[1];
                            String url = (String) args[2];

                            JSONObject body = new JSONObject();
                            try {
                                body.put("username", username);
                                body.put("password", password);

                                JSONObject res = new JSONObject(Tools.post(url, body));
                                switch (res.getInt("result")) {
                                    case Tools.RES_OK:
                                        runOnUiThread(new MyRunnable(
                                                activity,
                                                res.getJSONArray("names")
                                        ) {
                                            @Override
                                            public void run() {
                                                JSONArray arr = (JSONArray) args[0];
                                                while (interv_list.getChildCount() > 1)
                                                    interv_list.removeViewAt(1);
                                                LayoutInflater inflater = getLayoutInflater();
                                                try {
                                                    String[] interv = new String[arr.length()];
                                                    for (int n = 0; n < arr.length(); n++) {
                                                        inflater.inflate(R.layout.intervention_element, interv_list);
                                                        TextView interv_text = interv_list.getChildAt(n).findViewById(R.id.intervention_text);
                                                        interv_text.setText(interv[n] = arr.getString(n));
                                                    }
                                                    Tools.cacheSystemInterventions(InterventionsActivity.this, interv);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        break;
                                    case Tools.RES_FAIL:
                                        break;
                                    case Tools.RES_SRV_ERR:
                                        break;
                                    default:
                                        break;
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                            enableTouch();
                        }
                    });
                else {
                    String[] interventions = Tools.readOfflineSystemInterventions(this);
                    if (interventions == null)
                        return;

                    LayoutInflater inflater = getLayoutInflater();
                    for (int n = 0; n < interventions.length; n++) {
                        inflater.inflate(R.layout.intervention_element, interv_list);
                        TextView interv_text = interv_list.getChildAt(n).findViewById(R.id.intervention_text);
                        interv_text.setText(interventions[n]);
                    }
                }
                saveIntervention = false;
                break;
            case R.id.button_peer_interventions:
                tabButtons[2].setBackgroundResource(R.drawable.bg_interv_method_checked_view);
                interv_choice.setVisibility(View.VISIBLE);
                interv_list.removeAllViews();
                Tools.toggle_keyboard(this, interv_text, false);
                if (Tools.isNetworkAvailable(this))
                    Tools.execute(new MyRunnable(
                            this,
                            SignInActivity.loginPrefs.getString(SignInActivity.username, null),
                            SignInActivity.loginPrefs.getString(SignInActivity.password, null),
                            getString(R.string.url_interv_peer_fetch, getString(R.string.server_ip))
                    ) {
                        @Override
                        public void run() {
                            String username = (String) args[0];
                            String password = (String) args[1];
                            String url = (String) args[2];

                            JSONObject body = new JSONObject();
                            try {
                                body.put("username", username);
                                body.put("password", password);

                                JSONObject res = new JSONObject(Tools.post(url, body));
                                switch (res.getInt("result")) {
                                    case Tools.RES_OK:
                                        runOnUiThread(new MyRunnable(
                                                activity,
                                                res.getJSONArray("names")
                                        ) {
                                            @Override
                                            public void run() {
                                                JSONArray arr = (JSONArray) args[0];
                                                while (interv_list.getChildCount() > 1)
                                                    interv_list.removeViewAt(1);

                                                LayoutInflater inflater = getLayoutInflater();
                                                try {
                                                    String[] interv = new String[arr.length()];
                                                    for (int n = 0; n < arr.length(); n++) {
                                                        inflater.inflate(R.layout.intervention_element, interv_list);
                                                        TextView interv_text = interv_list.getChildAt(n).findViewById(R.id.intervention_text);
                                                        interv_text.setText(interv[n] = arr.getString(n));
                                                    }
                                                    Tools.cachePeerInterventions(InterventionsActivity.this, interv);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        break;
                                    case Tools.RES_FAIL:
                                        break;
                                    case Tools.RES_SRV_ERR:
                                        break;
                                    default:
                                        break;
                                }

                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                            enableTouch();
                        }
                    });
                else {
                    String[] interventions = Tools.readOfflinePeerInterventions(this);
                    if (interventions == null)
                        return;

                    LayoutInflater inflater = getLayoutInflater();
                    for (int n = 0; n < interventions.length; n++) {
                        inflater.inflate(R.layout.intervention_element, interv_list);
                        TextView interv_text = interv_list.getChildAt(n).findViewById(R.id.intervention_text);
                        interv_text.setText(interventions[n]);
                    }
                }
                saveIntervention = false;
                break;
            default:
                break;
        }
    }

    public void onIntervClick(View view) {
        result = ((TextView) view.findViewById(R.id.intervention_text)).getText().toString();
        interv_text.setText(result);

        interv_choice.setVisibility(View.GONE);
        interv_text.setVisibility(View.VISIBLE);
        schedulingView.setVisibility(View.VISIBLE);
        saveIntervention = true;
    }

    public void cancelClick(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
    }

    public void saveClick(View view) {
        if (saveIntervention) {
            if (interv_text.length() == 0) {
                Toast.makeText(this, "To create an intervention first you need to type it's name first!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Tools.isNetworkAvailable(this))
                Tools.execute(new MyRunnable(
                        this,
                        getString(R.string.url_interv_create, getString(R.string.server_ip)),
                        SignInActivity.loginPrefs.getString(SignInActivity.username, null),
                        SignInActivity.loginPrefs.getString(SignInActivity.password, null),
                        interv_text.getText().toString()
                ) {
                    @Override
                    public void run() {
                        String url = (String) args[0];
                        String username = (String) args[1];
                        String password = (String) args[2];
                        String interv_name = (String) args[3];

                        JSONObject body = new JSONObject();
                        try {
                            body.put("username", username);
                            body.put("password", password);
                            body.put("interventionName", interv_name);

                            JSONObject res = new JSONObject(Tools.post(url, body));
                            switch (res.getInt("result")) {
                                case Tools.RES_OK:
                                    runOnUiThread(new MyRunnable(
                                            activity,
                                            interv_name
                                    ) {
                                        @Override
                                        public void run() {
                                            String interv_name = (String) args[0];
                                            Toast.makeText(InterventionsActivity.this, "Intervention successfully created!", Toast.LENGTH_SHORT).show();
                                            result = interv_name;
                                            short radioButtonTag = Short.parseShort((String) intervScheduling.findViewById(intervScheduling.getCheckedRadioButtonId()).getTag());
                                            if (radioButtonTag == 1) { // check if the custom radio button with tag "1" is checked
                                                resultSchedule = (short) customReminderMinutes; // set custom reminder minutes
                                            } else resultSchedule = radioButtonTag; // else set from default radio buttons
                                            setResult(Activity.RESULT_OK);
                                            finish();
                                            overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
                                        }
                                    });
                                    break;
                                case Tools.RES_FAIL:
                                    runOnUiThread(new MyRunnable(
                                            activity,
                                            interv_name
                                    ) {
                                        @Override
                                        public void run() {
                                            String interv_name = (String) args[0];
                                            Toast.makeText(InterventionsActivity.this, "Intervention already exists. Thus, it was picked for you.", Toast.LENGTH_SHORT).show();
                                            result = interv_name;
                                            short radioButtonTag = Short.parseShort((String) intervScheduling.findViewById(intervScheduling.getCheckedRadioButtonId()).getTag());
                                            if (radioButtonTag == 1) { // check if the custom radio button with tag "1" is checked
                                                resultSchedule = (short) customReminderMinutes; // set custom reminder minutes
                                            } else resultSchedule = radioButtonTag; // else set from default radio buttons

                                            setResult(Activity.RESULT_OK);
                                            finish();
                                            overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
                                        }
                                    });
                                    break;
                                case Tools.RES_SRV_ERR:
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(InterventionsActivity.this, "Failure in intervention creation. (SERVER SIDE)", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(InterventionsActivity.this, "Failed to create the intervention.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        enableTouch();
                    }
                });
            else {
                short radioButtonTag = Short.parseShort((String) intervScheduling.findViewById(intervScheduling.getCheckedRadioButtonId()).getTag());
                if (radioButtonTag == 1) { // check if the custom radio button with tag "1" is checked
                    resultSchedule = (short) customReminderMinutes; // set custom reminder minutes
                } else resultSchedule = radioButtonTag; // else set from default radio buttons
                setResult(Activity.RESULT_OK);
                finish();
                overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
            }
        } else {
            if (result == null) {
                Toast.makeText(this, "Please pick an intervention first!", Toast.LENGTH_SHORT).show();
                return;
            }
            short radioButtonTag = Short.parseShort((String) intervScheduling.findViewById(intervScheduling.getCheckedRadioButtonId()).getTag());
            if (radioButtonTag == 1) { // check if the custom radio button with tag "1" is checked
                resultSchedule = (short) customReminderMinutes; // set custom reminder minutes
            } else resultSchedule = radioButtonTag; // else set from default radio buttons
            setResult(Activity.RESULT_OK);
            finish();
            overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
        }
    }

    public void clickCustomIntervNotification(View view) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialogCustomNotif");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new CustomNotificationDialog();
        Bundle args = new Bundle();
        args.putBoolean("isEventNotification", false);
        dialogFragment.setArguments(args);
        dialogFragment.show(ft, "dialogCustomNotif");
    }

    public void setCustomNotifParams(int minutes, String custRemTxt, String timeTxt) {
        customReminderText = custRemTxt;
        customReminderMinutes = minutes;
        customNotifTimeTxt = timeTxt;

        intervScheduling.check(R.id.option_custom);
        customNotifRadioButton.setVisibility(View.VISIBLE);
        customNotifRadioButton.setText(custRemTxt);
    }
}
