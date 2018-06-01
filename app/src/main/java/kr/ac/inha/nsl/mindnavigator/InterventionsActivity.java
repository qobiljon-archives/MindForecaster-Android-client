package kr.ac.inha.nsl.mindnavigator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InterventionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interventions);
        init();
    }

    //region Variables
    EditText interv_text;
    View interv_choice;
    ViewGroup interv_list;

    Button[] tabButtons;
    //endregion

    private void init() {
        interv_choice = findViewById(R.id.intervention_choice);
        interv_text = findViewById(R.id.intervention_text);
        interv_list = findViewById(R.id.interventions_list);
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
        for (Button button : tabButtons)
            button.setBackgroundResource(R.drawable.bg_interv_method_unchecked_view);

        // Act upon the click event
        switch (view.getId()) {
            case R.id.button_self_intervention:
                tabButtons[0].setBackgroundResource(R.drawable.bg_interv_method_checked_view);
                interv_text.setVisibility(View.VISIBLE);
                Tools.toggle_keyboard(this, interv_text, true);
                break;
            case R.id.button_systems_intervention:
                tabButtons[1].setBackgroundResource(R.drawable.bg_interv_method_checked_view);
                interv_choice.setVisibility(View.VISIBLE);
                interv_list.removeViews(1, interv_list.getChildCount() - 1);
                Tools.toggle_keyboard(this, interv_text, false);
                Tools.execute(new MyRunnable(
                        "qobiljon",
                        "12345678",
                        getString(R.string.url_fetch_interv_system)
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
                                            res.getJSONArray("names")
                                    ) {
                                        @Override
                                        public void run() {
                                            JSONArray arr = (JSONArray) args[0];
                                            while (interv_list.getChildCount() > 1)
                                                interv_list.removeViewAt(1);

                                            LayoutInflater inflater = getLayoutInflater();
                                            try {
                                                for (int n = 0; n < arr.length(); n++) {
                                                    inflater.inflate(R.layout.intervention_element, interv_list);
                                                    TextView interv_text = interv_list.getChildAt(n + 1).findViewById(R.id.intervention_text);
                                                    interv_text.setText(arr.getString(n));
                                                }
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.button_peer_interventions:
                tabButtons[2].setBackgroundResource(R.drawable.bg_interv_method_checked_view);
                interv_choice.setVisibility(View.VISIBLE);
                interv_list.removeViews(1, interv_list.getChildCount() - 1);
                Tools.toggle_keyboard(this, interv_text, false);
                Tools.execute(new MyRunnable(
                        "qobiljon",
                        "12345678",
                        getString(R.string.url_fetch_interv_peer)
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
                                            res.getJSONArray("names")
                                    ) {
                                        @Override
                                        public void run() {
                                            JSONArray arr = (JSONArray) args[0];
                                            while (interv_list.getChildCount() > 1)
                                                interv_list.removeViewAt(1);

                                            LayoutInflater inflater = getLayoutInflater();
                                            try {
                                                for (int n = 0; n < arr.length(); n++) {
                                                    inflater.inflate(R.layout.intervention_element, interv_list);
                                                    TextView interv_text = interv_list.getChildAt(n + 1).findViewById(R.id.intervention_text);
                                                    interv_text.setText(arr.getString(n));
                                                }
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    public void cancelClick(View view) {
        finish();
    }

    public void saveClick(View view) {
        finish();
    }
}
