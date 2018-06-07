package kr.ac.inha.nsl.mindnavigator;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

    EditText interv_text;
    View interv_choice;
    ViewGroup interv_list, schedulingView;
    RadioGroup intervScheduling;
    Button[] tabButtons;

    Button prevEditButton = null;
    TextView prevTextView = null;
    //endregion

    private void init() {
        interv_choice = findViewById(R.id.intervention_choice);
        interv_text = findViewById(R.id.intervention_text);
        interv_list = findViewById(R.id.interventions_list);
        schedulingView = findViewById(R.id.interv_scheduling_view);
        intervScheduling = findViewById(R.id.interv_scheduling_group);
        tabButtons = new Button[]{
                findViewById(R.id.button_self_intervention),
                findViewById(R.id.button_systems_intervention),
                findViewById(R.id.button_peer_interventions)
        };

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
        prevEditButton = null;
        prevTextView = null;
        for (Button button : tabButtons)
            button.setBackgroundResource(R.drawable.bg_interv_method_unchecked_view);

        // Act upon the click event
        switch (view.getId()) {
            case R.id.button_self_intervention:
                tabButtons[0].setBackgroundResource(R.drawable.bg_interv_method_checked_view);
                interv_text.setVisibility(View.VISIBLE);
                Tools.toggle_keyboard(this, interv_text, true);
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
        if (prevTextView != null) {
            prevTextView.setTextColor(getColor(R.color.black));
            prevEditButton.setVisibility(View.GONE);
        }

        ((TextView) view.findViewById(R.id.intervention_text)).setTextColor(getColor(R.color.dark_blue));
        schedulingView.setVisibility(View.VISIBLE);
        result = (prevTextView = view.findViewById(R.id.intervention_text)).getText().toString();
        (prevEditButton = view.findViewById(R.id.btn_edit_interv)).setVisibility(View.VISIBLE);
    }

    public void editIntervClick(View view) {
        interv_text.setText(result);
        interv_text.setSelection(interv_text.length());
        tabButtons[0].callOnClick();
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
                                            resultSchedule = Short.parseShort((String) intervScheduling.findViewById(intervScheduling.getCheckedRadioButtonId()).getTag());
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
                                            resultSchedule = Short.parseShort((String) intervScheduling.findViewById(intervScheduling.getCheckedRadioButtonId()).getTag());
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
                resultSchedule = Short.parseShort((String) intervScheduling.findViewById(intervScheduling.getCheckedRadioButtonId()).getTag());
                setResult(Activity.RESULT_OK);
                finish();
                overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
            }
        } else {
            if (result == null) {
                Toast.makeText(this, "Please pick an intervention first!", Toast.LENGTH_SHORT).show();
                return;
            }
            resultSchedule = Short.parseShort((String) intervScheduling.findViewById(intervScheduling.getCheckedRadioButtonId()).getTag());
            setResult(Activity.RESULT_OK);
            finish();
            overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
        }
    }
}
