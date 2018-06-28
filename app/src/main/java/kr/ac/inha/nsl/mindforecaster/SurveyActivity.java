package kr.ac.inha.nsl.mindforecaster;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SurveyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        init();
    }

    //region Variables
    private ViewGroup surveyMainHolder1, surveyChildHolder1;
    private ViewGroup surveyMainHolder2, surveyChildHolder2;
    private ViewGroup surveyMainHolder3, surveyChildHolder3;
    private ViewGroup surveyMainHolder4, surveyChildHolder4;
    //endregion

    private void init() {
        surveyMainHolder1 = findViewById(R.id.survey1_main_holder);
        surveyMainHolder2 = findViewById(R.id.survey2_main_holder);
        surveyMainHolder3 = findViewById(R.id.survey3_main_holder);
        surveyMainHolder4 = findViewById(R.id.survey4_main_holder);

        surveyChildHolder1 = findViewById(R.id.survey1_child_holder);
        surveyChildHolder2 = findViewById(R.id.survey2_child_holder);
        surveyChildHolder3 = findViewById(R.id.survey3_child_holder);
        surveyChildHolder4 = findViewById(R.id.survey4_child_holder);

        loadTheSurvey();
    }

    private void loadTheSurvey() {
        surveyChildHolder1.removeAllViews();
        surveyChildHolder2.removeAllViews();
        surveyChildHolder3.removeAllViews();
        surveyChildHolder4.removeAllViews();

        if (Tools.isNetworkAvailable(this))
            Tools.execute(new MyRunnable(
                    this,
                    SignInActivity.loginPrefs.getString(SignInActivity.username, null),
                    SignInActivity.loginPrefs.getString(SignInActivity.password, null),
                    getString(R.string.url_survey_fetch, getString(R.string.server_ip))
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
                                        res.getJSONArray("survey1"),
                                        res.getJSONArray("survey2"),
                                        res.getJSONArray("survey3"),
                                        res.getJSONArray("survey4")
                                ) {
                                    @Override
                                    public void run() {
                                        JSONArray arrSurvey1 = (JSONArray) args[0];
                                        JSONArray arrSurvey2 = (JSONArray) args[1];
                                        JSONArray arrSurvey3 = (JSONArray) args[2];
                                        JSONArray arrSurvey4 = (JSONArray) args[3];

                                        LayoutInflater inflater = getLayoutInflater();
                                        try {
                                            String[] survey1 = new String[arrSurvey1.length()];
                                            String[] survey2 = new String[arrSurvey2.length()];
                                            String[] survey3 = new String[arrSurvey3.length()];
                                            String[] survey4 = new String[arrSurvey4.length()];
                                            for (int n = 0; n < arrSurvey1.length(); n++) {
                                                inflater.inflate(R.layout.survey1_element, surveyChildHolder1);
                                                TextView interv_text = surveyChildHolder1.getChildAt(n).findViewById(R.id.txt_survey_element);
                                                interv_text.setText(survey1[n] = arrSurvey1.getString(n));
                                            }
                                            Tools.cacheSurveys(SurveyActivity.this, survey1, "survey1");
                                            for (int n = 0; n < arrSurvey2.length(); n++) {
                                                inflater.inflate(R.layout.survey2_element, surveyChildHolder2);
                                                TextView interv_text = surveyChildHolder2.getChildAt(n).findViewById(R.id.txt_survey_element);
                                                interv_text.setText(survey2[n] = arrSurvey2.getString(n));
                                            }
                                            Tools.cacheSurveys(SurveyActivity.this, survey2, "survey2");
                                            for (int n = 0; n < arrSurvey3.length(); n++) {
                                                inflater.inflate(R.layout.survey3_element, surveyChildHolder3);
                                                TextView interv_text = surveyChildHolder3.getChildAt(n).findViewById(R.id.txt_survey_element);
                                                interv_text.setText(survey3[n] = arrSurvey3.getString(n));
                                            }
                                            Tools.cacheSurveys(SurveyActivity.this, survey3, "survey3");
                                            for (int n = 0; n < arrSurvey4.length(); n++) {
                                                inflater.inflate(R.layout.survey4_element, surveyChildHolder4);
                                                TextView interv_text = surveyChildHolder4.getChildAt(n).findViewById(R.id.txt_survey_element);
                                                interv_text.setText(survey4[n] = arrSurvey4.getString(n));
                                            }
                                            Tools.cacheSurveys(SurveyActivity.this, survey4, "survey4");

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
            String[] survey1 = Tools.readOfflineSurvey(this, "survey1");
            String[] survey2 = Tools.readOfflineSurvey(this, "survey2");
            String[] survey3 = Tools.readOfflineSurvey(this, "survey3");
            String[] survey4 = Tools.readOfflineSurvey(this, "survey4");

            if (survey1 == null || survey2 == null || survey3 == null || survey4 == null)
                return;

            LayoutInflater inflater = getLayoutInflater();
            for (int n = 0; n < survey1.length; n++) {
                inflater.inflate(R.layout.survey1_element, surveyChildHolder1);
                TextView interv_text = surveyChildHolder1.getChildAt(n).findViewById(R.id.txt_survey_element);
                interv_text.setText(survey1[n]);
            }
            Tools.cacheSurveys(SurveyActivity.this, survey1, "survey1");
            for (int n = 0; n < survey2.length; n++) {
                inflater.inflate(R.layout.survey2_element, surveyChildHolder2);
                TextView interv_text = surveyChildHolder2.getChildAt(n).findViewById(R.id.txt_survey_element);
                interv_text.setText(survey2[n]);
            }
            Tools.cacheSurveys(SurveyActivity.this, survey2, "survey2");
            for (int n = 0; n < survey3.length; n++) {
                inflater.inflate(R.layout.survey3_element, surveyChildHolder3);
                TextView interv_text = surveyChildHolder3.getChildAt(n).findViewById(R.id.txt_survey_element);
                interv_text.setText(survey3[n]);
            }
            Tools.cacheSurveys(SurveyActivity.this, survey3, "survey3");
            for (int n = 0; n < survey4.length; n++) {
                inflater.inflate(R.layout.survey4_element, surveyChildHolder4);
                TextView interv_text = surveyChildHolder4.getChildAt(n).findViewById(R.id.txt_survey_element);
                interv_text.setText(survey4[n]);
            }
        }
    }

    public void expandSurveyHolder1(View view) {
        surveyChildHolder1.removeAllViews();
        if (surveyMainHolder1.getVisibility() == View.VISIBLE) {
            surveyMainHolder1.setVisibility(View.GONE);
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_expand), null);
        } else {
            surveyMainHolder1.setVisibility(View.VISIBLE);
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_collapse), null);
        }
    }

    public void expandSurveyHolder2(View view) {
        surveyChildHolder2.removeAllViews();
        if (surveyMainHolder2.getVisibility() == View.VISIBLE) {
            surveyMainHolder2.setVisibility(View.GONE);
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_expand), null);
        } else {
            surveyMainHolder2.setVisibility(View.VISIBLE);
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_collapse), null);
        }
    }

    public void expandSurveyHolder3(View view) {
        surveyChildHolder3.removeAllViews();
        if (surveyMainHolder3.getVisibility() == View.VISIBLE) {
            surveyMainHolder3.setVisibility(View.GONE);
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_expand), null);
        } else {
            surveyMainHolder3.setVisibility(View.VISIBLE);
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_collapse), null);
        }
    }

    public void expandSurveyHolder4(View view) {
        surveyChildHolder4.removeAllViews();
        if (surveyMainHolder4.getVisibility() == View.VISIBLE) {
            surveyMainHolder4.setVisibility(View.GONE);
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_expand), null);
        } else {
            surveyMainHolder4.setVisibility(View.VISIBLE);
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_collapse), null);
        }
    }

    public void saveClick(View view) {
    }

    public void cancelClick(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
    }
}
