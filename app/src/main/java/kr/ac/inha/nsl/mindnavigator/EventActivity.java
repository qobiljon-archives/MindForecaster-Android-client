package kr.ac.inha.nsl.mindnavigator;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case INTERVENTION_ACTIVITY:
                    event.setIntervention(InterventionsActivity.result);
                    event.setInterventionReminder(InterventionsActivity.resultSchedule);
                    selectedInterv.setText(event.getIntervention());
                    switch (InterventionsActivity.resultSchedule) {
                        case -1440:
                            intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_day_before)));
                            break;
                        case -60:
                            intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_hour_before)));
                            break;
                        case -30:
                            intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._30_minutes_before)));
                            break;
                        case -10:
                            intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._10_minutes_before)));
                            break;
                        case 1440:
                            intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_day_after)));
                            break;
                        case 60:
                            intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_hour_after)));
                            break;
                        case 30:
                            intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._30_minutes_after)));
                            break;
                        case 10:
                            intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._10_minutes_after)));
                            break;
                        default:
                            intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, InterventionsActivity.customReminderText));
                            break;
                    }
                    intervReminderTxt.setVisibility(View.VISIBLE);
                    break;
                case EVALUATION_ACTIVITY:
                    finish();
                    overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
                    break;
                default:
                    break;
            }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    //region Variables
    private final int EVALUATION_ACTIVITY = 0, INTERVENTION_ACTIVITY = 1;
    static Event event;

    private ViewGroup inactiveLayout;
    private ViewGroup stressLevelDetails;
    private ViewGroup interventionDetails;
    private ViewGroup repeatDetails;
    private ViewGroup notificationDetails;
    private TextView startDateText;
    private TextView startTimeText;
    private TextView endDateText;
    private TextView endTimeText;
    private TextView selectedInterv;
    private TextView intervReminderTxt;
    private TextView saveButton;
    private TextView cancelButton;
    private TextView deleteButton;
    private RadioGroup stressTypeGroup, repeatModeGroup, eventNotificationGroup;
    private EditText eventTitle, stressCause;
    private Switch switchAllDay;
    private SeekBar stressLvl;

    private RadioButton customNotifRadioButton;

    private String customReminderText; // customized reminder text
    private int customReminderMinutes; //customized reminder minutes
    static String customNotifTimeTxt; //customized reminder time-text for notification text ( Ex.: 2 day(s) )

    private Calendar startTime, endTime;
    //endregion

    private void init() {
        eventTitle = findViewById(R.id.edit_event_title);
        switchAllDay = findViewById(R.id.all_day_switch);
        startDateText = findViewById(R.id.txt_event_start_date);
        startTimeText = findViewById(R.id.txt_event_start_time);
        endDateText = findViewById(R.id.txt_event_end_date);
        endTimeText = findViewById(R.id.txt_event_end_time);
        stressLvl = findViewById(R.id.real_stress_level_seek);
        inactiveLayout = findViewById(R.id.layout_to_be_inactive);
        stressTypeGroup = findViewById(R.id.stress_type_group);
        stressCause = findViewById(R.id.txt_stress_cause);
        ViewGroup tabNotif = findViewById(R.id.tab_notification);
        ViewGroup tabRepeat = findViewById(R.id.tab_repeat);
        ViewGroup tabStressLvl = findViewById(R.id.tab_anticipated_strs_lvl);
        ViewGroup tabInterv = findViewById(R.id.tab_interventions);
        ViewGroup tabEvaluation = findViewById(R.id.tab_evaluation);
        stressLevelDetails = findViewById(R.id.stress_level_details);
        interventionDetails = findViewById(R.id.intervention_details);
        repeatDetails = findViewById(R.id.repeat_details);
        notificationDetails = findViewById(R.id.notification_details);
        ViewGroup feedBackDetails = findViewById(R.id.feedback_details);
        selectedInterv = findViewById(R.id.selected_intervention);
        repeatModeGroup = findViewById(R.id.repeat_mode_group);
        eventNotificationGroup = findViewById(R.id.event_notification_group);
        intervReminderTxt = findViewById(R.id.txt_interv_reminder_time);
        saveButton = findViewById(R.id.btn_create);
        cancelButton = findViewById(R.id.btn_cancel);
        deleteButton = findViewById(R.id.btn_delete);
        TextView customNotifTxt = findViewById(R.id.txt_custom_notif);
        customNotifRadioButton = findViewById(R.id.radio_btn_custom);

        ViewGroup postEventLayout = findViewById(R.id.postEventLayout);

        if (getIntent().hasExtra("eventId")) {
            // Editing an existing event
            event = Event.getEventById(getIntent().getLongExtra("eventId", 0));
            startTime = (Calendar) event.getStartTime().clone();
            endTime = (Calendar) event.getEndTime().clone();

            switchAllDay.setEnabled(false);
            eventTitle.setEnabled(false);
            startDateText.setEnabled(false);
            startTimeText.setEnabled(false);
            endDateText.setEnabled(false);
            endTimeText.setEnabled(false);
            customNotifTxt.setEnabled(false);
            stressLvl.setEnabled(false);
            stressCause.setEnabled(false);
            stressTypeGroup.setEnabled(false);
            for (int n = 0; n < stressTypeGroup.getChildCount(); n++)
                stressTypeGroup.getChildAt(n).setEnabled(false);
            repeatModeGroup.setEnabled(false);
            for (int n = 0; n < repeatModeGroup.getChildCount(); n++)
                repeatModeGroup.getChildAt(n).setEnabled(false);
            eventNotificationGroup.setEnabled(false);
            for (int n = 0; n < eventNotificationGroup.getChildCount(); n++)
                eventNotificationGroup.getChildAt(n).setEnabled(false);
            selectedInterv.setEnabled(false);

            // recover existing values
            fillOutExistingValues();

            if (event.getEndTime().before(Calendar.getInstance())) {
                saveButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                postEventLayout.setVisibility(View.VISIBLE);

                tabNotif.setVisibility(View.GONE);
                tabRepeat.setVisibility(View.GONE);
                tabStressLvl.setVisibility(View.GONE);
                tabInterv.setVisibility(View.GONE);

                if (event.isEvaluated()) {
                    initFeedbackView(event);
                    feedBackDetails.setVisibility(View.VISIBLE);
                    //findViewById(R.id.feedback_text).setVisibility(View.VISIBLE);
                }


                eventTitle.setFocusable(false);
                eventTitle.clearFocus();
            } else {
                saveButton.setText(getString(R.string.edit));
                cancelButton.setVisibility(View.GONE);
            }
        } else {
            event = new Event(0);
            startTime = Calendar.getInstance();
            endTime = Calendar.getInstance();
            deleteButton.setVisibility(View.GONE);

            if (getIntent().hasExtra("selectedDayMillis")) {
                startTime.setTimeInMillis(getIntent().getLongExtra("selectedDayMillis", 0));
                startTime.set(Calendar.HOUR_OF_DAY, 9);
                startTime.set(Calendar.MINUTE, 0);
                endTime.setTimeInMillis(startTime.getTimeInMillis());
                endTime.add(Calendar.HOUR_OF_DAY, 1);
            }
        }

        startDateText.setText(String.format(Locale.US,
                "%d, %s %d, %s",
                startTime.get(Calendar.YEAR),
                startTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                startTime.get(Calendar.DAY_OF_MONTH),
                startTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
        ));
        startDateText.setTag(startTime.getTimeInMillis());
        startTimeText.setText(String.format(Locale.US,
                "%02d:%02d",
                startTime.get(Calendar.HOUR_OF_DAY),
                startTime.get(Calendar.MINUTE))
        );
        startTimeText.setTag(startTime.getTimeInMillis());
        endDateText.setText(String.format(Locale.US,
                "%d, %s %d, %s",
                endTime.get(Calendar.YEAR),
                endTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                endTime.get(Calendar.DAY_OF_MONTH),
                endTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
        ));
        endDateText.setTag(endTime.getTimeInMillis());
        endTimeText.setText(String.format(Locale.US,
                "%02d:%02d",
                endTime.get(Calendar.HOUR_OF_DAY),
                endTime.get(Calendar.MINUTE))
        );
        endTimeText.setTag(endTime.getTimeInMillis());

        MyTextWatcher timePickingCorrector = new MyTextWatcher(startDateText, startTimeText, endDateText, endTimeText) {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Tools.copy_date((long) startDateText.getTag(), startTime);
                Tools.copy_time((long) startTimeText.getTag(), startTime);
                Tools.copy_date((long) endDateText.getTag(), endTime);
                Tools.copy_time((long) endTimeText.getTag(), endTime);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (startTime.before(Calendar.getInstance())) {
                    removeListeners();
                    startTime = Calendar.getInstance();
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.MILLISECOND, 0);
                    startTime.add(Calendar.HOUR_OF_DAY, 1);
                    endTime.setTimeInMillis(startTime.getTimeInMillis());
                    endTime.add(Calendar.HOUR_OF_DAY, 1);

                    startDateText.setText(String.format(Locale.US,
                            "%d, %s %d, %s",
                            startTime.get(Calendar.YEAR),
                            startTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                            startTime.get(Calendar.DAY_OF_MONTH),
                            startTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                    ));
                    startDateText.setTag(startTime.getTimeInMillis());
                    startTimeText.setText(String.format(Locale.US,
                            "%02d:%02d",
                            startTime.get(Calendar.HOUR_OF_DAY),
                            startTime.get(Calendar.MINUTE))
                    );
                    startTimeText.setTag(startTime.getTimeInMillis());
                    endDateText.setText(String.format(Locale.US,
                            "%d, %s %d, %s",
                            endTime.get(Calendar.YEAR),
                            endTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                            endTime.get(Calendar.DAY_OF_MONTH),
                            endTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                    ));
                    endDateText.setTag(endTime.getTimeInMillis());
                    endTimeText.setText(String.format(Locale.US,
                            "%02d:%02d",
                            endTime.get(Calendar.HOUR_OF_DAY),
                            endTime.get(Calendar.MINUTE))
                    );
                    endTimeText.setTag(endTime.getTimeInMillis());
                    addListeners();
                } else if (endTime.before(startTime)) {
                    removeListeners();
                    endTime.setTimeInMillis(startTime.getTimeInMillis());

                    endTime.add(Calendar.HOUR_OF_DAY, 1);
                    endDateText.setText(String.format(Locale.US,
                            "%d, %s %d, %s",
                            endTime.get(Calendar.YEAR),
                            endTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                            endTime.get(Calendar.DAY_OF_MONTH),
                            endTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                    ));
                    endTimeText.setText(String.format(Locale.US,
                            "%02d:%02d",
                            endTime.get(Calendar.HOUR_OF_DAY),
                            endTime.get(Calendar.MINUTE))
                    );
                    addListeners();
                }

                endDateText.setTag(endTime.getTimeInMillis());
                endTimeText.setTag(endTime.getTimeInMillis());
            }
        };
        startDateText.addTextChangedListener(timePickingCorrector);
        startTimeText.addTextChangedListener(timePickingCorrector);
        endDateText.addTextChangedListener(timePickingCorrector);
        endTimeText.addTextChangedListener(timePickingCorrector);

        stressLvl.getProgressDrawable().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null), PorterDuff.Mode.SRC_IN);
        stressLvl.getThumb().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null), PorterDuff.Mode.SRC_IN);

        switchAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startTimeText.setVisibility(View.GONE);
                    endTimeText.setVisibility(View.GONE);

                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.MILLISECOND, 0);
                } else {
                    startTimeText.setVisibility(View.VISIBLE);
                    endTimeText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void fillOutExistingValues() {
        InterventionsActivity.result = event.getIntervention();
        InterventionsActivity.resultSchedule = event.getInterventionReminder();

        eventTitle.setText(event.getTitle());
        stressLvl.setProgress(event.getStressLevel());
        switch (event.getStressType()) {
            case "positive":
                stressTypeGroup.check(R.id.stressor_positive);
                break;
            case "negative":
                stressTypeGroup.check(R.id.stressor_negative);
                break;
            case "unknown":
                stressTypeGroup.check(R.id.stressor_unknown);
                break;
            default:
                break;
        }

        stressCause.setText(event.getStressCause());
        switch (event.getRepeatMode()) {
            case Event.NO_REPEAT:
                repeatModeGroup.check(R.id.no_repeat_radio);
                break;
            case Event.REPEAT_EVERYDAY:
                repeatModeGroup.check(R.id.everyday_repeat_radio);
                break;
            case Event.REPEAT_WEEKLY:
                repeatModeGroup.check(R.id.everyweek_repeat_radio);
                break;
            default:
                break;
        }

        switch (event.getEventReminder()) {
            case 0:
                eventNotificationGroup.check(R.id.option_none);
                break;
            case -1440:
                eventNotificationGroup.check(R.id.option_day_before);
                break;
            case -60:
                eventNotificationGroup.check(R.id.option_hour_before);
                break;
            case -30:
                eventNotificationGroup.check(R.id.option_30mins_before);
                break;
            case -10:
                eventNotificationGroup.check(R.id.option_10mins_before);
                break;
            default:
                eventNotificationGroup.check(R.id.radio_btn_custom);
                customNotifRadioButton.setText(customReminderText);
                customNotifRadioButton.setVisibility(View.VISIBLE);
                break;
        }

        switchAllDay.setChecked(event.getStartTime().get(Calendar.HOUR_OF_DAY) == 0 && event.getStartTime().get(Calendar.MINUTE) == 0 && event.getStartTime().get(Calendar.SECOND) == 0 &&
                event.getStartTime().get(Calendar.MILLISECOND) == 0 && event.getEndTime().get(Calendar.HOUR_OF_DAY) == 0 && event.getEndTime().get(Calendar.MINUTE) == 0 &&
                event.getEndTime().get(Calendar.SECOND) == 0 && event.getEndTime().get(Calendar.MILLISECOND) == 0
        );
        selectedInterv.setText(event.getIntervention());
        switch (event.getInterventionReminder()) {
            case -1440:
                intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_day_before)));
                break;
            case -60:
                intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_hour_before)));
                break;
            case -30:
                intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._30_minutes_before)));
                break;
            case -10:
                intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._10_minutes_before)));
                break;
            case 1440:
                intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_day_after)));
                break;
            case 60:
                intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_hour_after)));
                break;
            case 30:
                intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._30_minutes_after)));
                break;
            case 10:
                intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._10_minutes_after)));
                break;
            default:
                intervReminderTxt.setVisibility(View.GONE);
                break;
        }

        eventTitle.setSelection(eventTitle.length());
    }

    public void expandStressLevelClick(View view) {
        final TextView info = findViewById(R.id.info_txt_stress_level);

        if (stressLevelDetails.getVisibility() == View.VISIBLE) {
            stressLevelDetails.setVisibility(View.GONE);
            info.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_expand), null);
        } else {
            stressLevelDetails.setVisibility(View.VISIBLE);
            info.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_collapse), null);
            findViewById(R.id.tab_interventions).getParent().requestChildFocus(findViewById(R.id.tab_interventions), findViewById(R.id.tab_interventions));
        }

        stressLvl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                stressLvl.getProgressDrawable().setColorFilter(Tools.stressLevelToColor(getApplicationContext(), progress), PorterDuff.Mode.SRC_IN);
                stressLvl.getThumb().setColorFilter(Tools.stressLevelToColor(getApplicationContext(), progress), PorterDuff.Mode.SRC_IN);
                if (progress > 0) {
                    inactiveLayout.setVisibility(View.VISIBLE);
                    findViewById(R.id.tab_interventions).getParent().requestChildFocus(findViewById(R.id.tab_interventions), findViewById(R.id.tab_interventions));
                } else inactiveLayout.setVisibility(View.GONE);

                switch (progress) {
                    case 0:
                        info.setText(getResources().getString(R.string.not_at_all));
                        break;
                    case 1:
                        info.setText(getResources().getString(R.string.low));
                        break;
                    case 2:
                        info.setText(getResources().getString(R.string.normal));
                        break;
                    case 3:
                        info.setText(getResources().getString(R.string.high));
                        break;
                    case 4:
                        info.setText(getResources().getString(R.string.extreme));
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
    }

    public void expandNotificationClick(View view) {
        final TextView info = findViewById(R.id.info_txt_notification);
        if (notificationDetails.getVisibility() == View.VISIBLE) {
            notificationDetails.setVisibility(View.GONE);
            info.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_expand), null);
        } else {
            notificationDetails.setVisibility(View.VISIBLE);
            info.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_collapse), null);
            findViewById(R.id.tab_anticipated_strs_lvl).getParent().requestChildFocus(findViewById(R.id.tab_anticipated_strs_lvl), findViewById(R.id.tab_anticipated_strs_lvl));
        }

        eventNotificationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                info.setText(((RadioButton) group.findViewById(checkedId)).getText().toString());
            }
        });
    }

    public void expandRepeatClick(View view) {
        TextView info = findViewById(R.id.info_txt_repeat);
        if (repeatDetails.getVisibility() == View.VISIBLE) {
            repeatDetails.setVisibility(View.GONE);
            info.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_expand), null);
        } else {
            repeatDetails.setVisibility(View.VISIBLE);
            info.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_collapse), null);
            findViewById(R.id.tab_anticipated_strs_lvl).getParent().requestChildFocus(findViewById(R.id.tab_anticipated_strs_lvl), findViewById(R.id.tab_anticipated_strs_lvl));
        }
    }

    public void expandInterventionsClick(View view) {
        TextView info = (TextView) view;

        if (interventionDetails.getVisibility() == View.VISIBLE) {
            interventionDetails.setVisibility(View.GONE);
            info.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_expand), null);
        } else {
            interventionDetails.setVisibility(View.VISIBLE);
            info.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_collapse), null);
            interventionDetails.getParent().requestChildFocus(interventionDetails, interventionDetails);

            if (event.getIntervention() != null && event.getIntervention().length() > 0) {
                switch (event.getInterventionReminder()) {
                    case -1440:
                        intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_day_before)));
                        break;
                    case -60:
                        intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_hour_before)));
                        break;
                    case -30:
                        intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._30_minutes_before)));
                        break;
                    case -10:
                        intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._10_minutes_before)));
                        break;
                    case 1440:
                        intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_day_after)));
                        break;
                    case 60:
                        intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_hour_after)));
                        break;
                    case 30:
                        intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._30_minutes_after)));
                        break;
                    case 10:
                        intervReminderTxt.setText(getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._10_minutes_after)));
                        break;
                }
                intervReminderTxt.setVisibility(View.VISIBLE);
            } else
                intervReminderTxt.setVisibility(View.GONE);
        }

    }

    public void editInterventionClick(View view) {
        Intent intent = new Intent(this, InterventionsActivity.class);
        if (getIntent().hasExtra("eventId"))
            intent.putExtra("eventId", event.getEventId());
        else intent.putExtra("eventTitle", eventTitle.getText().toString());
        startActivityForResult(intent, INTERVENTION_ACTIVITY);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public void evaluationClick(View view) {
        Intent intent = new Intent(this, EvaluationActivity.class);
        startActivityForResult(intent, EVALUATION_ACTIVITY);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public void feedbackClick(View view) {
        if (Tools.isNetworkAvailable(this))
            Tools.execute(new MyRunnable(
                    this,
                    getString(R.string.url_eval_fetch, getString(R.string.server_ip)),
                    SignInActivity.loginPrefs.getString(SignInActivity.username, null)
            ) {
                @Override
                public void run() {
                    String url = (String) args[0];
                    String username = (String) args[1];

                    JSONObject body = new JSONObject();
                    try {
                        body.put("username", username);
                        body.put("eventId", event.getEventId());

                        JSONObject res = new JSONObject(Tools.post(url, body));
                        switch (res.getInt("result")) {
                            case Tools.RES_OK:
                                JSONObject eventEval = res.getJSONObject("evaluation");
                                runOnUiThread(new MyRunnable(
                                        activity,
                                        eventEval.get("eventDone"),
                                        eventEval.get("realStressLevel")
                                ) {
                                    @Override
                                    public void run() {
                                        boolean eventDone = (boolean) args[0];
                                        int realStressLevel = (int) args[1];

                                        /*Intent intent = new Intent(EventActivity.this, FeedbackActivity.class);
                                        intent.putExtra("eventDone", eventDone);
                                        intent.putExtra("realStressLevel", realStressLevel);
                                        startActivityForResult(intent, FEEDBACK_ACTIVITY);
                                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);*/
                                    }
                                });
                                break;
                            case Tools.RES_FAIL:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EventActivity.this, "Failed to fetch evaluation for this event.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case Tools.RES_SRV_ERR:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EventActivity.this, "Failure occurred while processing the request. (SERVER SIDE)", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(EventActivity.this, "Failed to proceed due to an error in connection with server.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    enableTouch();
                }
            });
        else {
            Toast.makeText(this, "Please connect to a network first!", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelClick(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
    }

    public void deleteClick(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (Tools.isNetworkAvailable(EventActivity.this))
                            Tools.execute(new MyRunnable(
                                    EventActivity.this,
                                    getString(R.string.url_event_delete, getString(R.string.server_ip)),
                                    SignInActivity.loginPrefs.getString(SignInActivity.username, null),
                                    SignInActivity.loginPrefs.getString(SignInActivity.password, null),
                                    event.getEventId()
                            ) {
                                @Override
                                public void run() {
                                    String url = (String) args[0];
                                    String username = (String) args[1];
                                    String password = (String) args[2];
                                    long eventId = (long) args[3];

                                    JSONObject body = new JSONObject();
                                    try {
                                        body.put("username", username);
                                        body.put("password", password);
                                        body.put("event_id", eventId);

                                        JSONObject res = new JSONObject(Tools.post(url, body));
                                        switch (res.getInt("result")) {
                                            case Tools.RES_OK:
                                                runOnUiThread(new MyRunnable(activity, eventId) {
                                                    @Override
                                                    public void run() {
                                                        long eventId = (long) args[0];
                                                        Toast.makeText(EventActivity.this, "Event has been deleted!", Toast.LENGTH_SHORT).show();
                                                        Tools.cancelNotif(EventActivity.this, (int) eventId);
                                                        setResult(Activity.RESULT_OK);
                                                        finish();
                                                        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
                                                    }
                                                });
                                                break;
                                            case Tools.RES_FAIL:
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(EventActivity.this, "Failed to create delete the event.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                break;
                                            case Tools.RES_SRV_ERR:
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(EventActivity.this, "Failure occurred while processing the request. (SERVER SIDE)", Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(EventActivity.this, "Failed to proceed due to an error in connection with server.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    enableTouch();
                                }
                            });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    default:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this event?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }

    public void saveClick(View view) {
        if (saveButton.getText().equals(getString(R.string.edit))) {
            // if edit is clicked
            saveButton.setText(getString(R.string.save));
            cancelButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);

            switchAllDay.setEnabled(true);
            eventTitle.setEnabled(true);
            startDateText.setEnabled(true);
            startTimeText.setEnabled(true);
            endDateText.setEnabled(true);
            endTimeText.setEnabled(true);
            stressLvl.setEnabled(true);
            stressCause.setEnabled(true);
            stressTypeGroup.setEnabled(true);
            for (int n = 0; n < stressTypeGroup.getChildCount(); n++)
                stressTypeGroup.getChildAt(n).setEnabled(true);
            repeatModeGroup.setEnabled(true);
            for (int n = 0; n < repeatModeGroup.getChildCount(); n++)
                repeatModeGroup.getChildAt(n).setEnabled(true);

            eventNotificationGroup.setEnabled(true);
            for (int n = 0; n < eventNotificationGroup.getChildCount(); n++)
                eventNotificationGroup.getChildAt(n).setEnabled(true);
            selectedInterv.setEnabled(true);
            return;
        }

        if (eventTitle.getText().length() > 0)
            event.setTitle(eventTitle.getText().toString());
        else {
            Toast.makeText(this, "Please, type the event title", Toast.LENGTH_SHORT).show();
            return;
        }
        event.setStressLevel(stressLvl.getProgress());

        if (switchAllDay.isChecked()) {
            startTime.set(Calendar.HOUR_OF_DAY, 0);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);
            startTime.set(Calendar.MILLISECOND, 0);

            endTime.add(Calendar.DAY_OF_MONTH, 1);
            endTime.set(Calendar.HOUR_OF_DAY, 0);
            endTime.set(Calendar.MINUTE, 0);
            endTime.set(Calendar.SECOND, 0);
            endTime.set(Calendar.MILLISECOND, 0);
        }
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        switch (stressTypeGroup.getCheckedRadioButtonId()) {
            case R.id.stressor_positive:
                event.setStressType("positive");
                break;
            case R.id.stressor_negative:
                event.setStressType("negative");
                break;
            case R.id.stressor_unknown:
                event.setStressType("unknown");
                break;
            default:
                break;
        }
        event.setStressCause(stressCause.getText().toString());
        switch (repeatModeGroup.getCheckedRadioButtonId()) {
            case R.id.no_repeat_radio:
                event.setRepeatMode(Event.NO_REPEAT);
                break;
            case R.id.everyday_repeat_radio:
                event.setRepeatMode(Event.REPEAT_EVERYDAY);
                break;
            case R.id.everyweek_repeat_radio:
                event.setRepeatMode(Event.REPEAT_WEEKLY);
                break;
            default:
                break;
        }

        switch (eventNotificationGroup.getCheckedRadioButtonId()) {
            case R.id.option_none:
                event.setEventReminder((short) 0);
                break;
            case R.id.option_day_before:
                event.setEventReminder((short) -1440);
                break;
            case R.id.option_hour_before:
                event.setEventReminder((short) -60);
                break;
            case R.id.option_30mins_before:
                event.setEventReminder((short) -30);
                break;
            case R.id.option_10mins_before:
                event.setEventReminder((short) -10);
                break;
            default:
                event.setEventReminder((short) customReminderMinutes);
                break;
        }

        event.setEvaluated(false);

        if (Tools.isNetworkAvailable(this)) {
            MyOnDateSetListener listener;
            DatePickerDialog dialog;
            Calendar cal;

            switch (event.getRepeatMode()) {
                case Event.REPEAT_EVERYDAY:
                    listener = new MyOnDateSetListener(view) {
                        @Override
                        public void onDateSet(DatePicker picker, int year, int month, int dayOfMonth) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(year, month, dayOfMonth, 0, 0, 0);
                            cal.set(Calendar.MILLISECOND, 0);
                            cal.add(Calendar.DAY_OF_MONTH, 1);
                            long till = cal.getTimeInMillis();
                            long sTime = event.getStartTime().getTimeInMillis();
                            long eTime = event.getEndTime().getTimeInMillis();

                            if (eTime - sTime > 86400000) {
                                Toast.makeText(EventActivity.this, "Event length cannot be longer than 24 hours in everyday mode!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            while (sTime + 86400000 < till) {
                                createEvent(sTime, eTime, Calendar.getInstance().getTimeInMillis(), false);

                                sTime += 86400000;
                                eTime += 86400000;
                            }
                            createEvent(sTime, eTime, Calendar.getInstance().getTimeInMillis(), true);
                        }
                    };
                    cal = Calendar.getInstance();
                    cal.setTimeInMillis(event.getStartTime().getTimeInMillis());
                    dialog = new DatePickerDialog(this, listener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                    dialog.setTitle("Repeat until");
                    dialog.show();
                    break;
                case Event.REPEAT_WEEKLY:
                    listener = new MyOnDateSetListener(view) {
                        @Override
                        public void onDateSet(DatePicker picker, int year, int month, int dayOfMonth) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(year, month, dayOfMonth, 0, 0, 0);
                            cal.set(Calendar.MILLISECOND, 0);
                            cal.add(Calendar.DAY_OF_MONTH, 1);
                            long till = cal.getTimeInMillis();
                            long sTime = event.getStartTime().getTimeInMillis();
                            long eTime = event.getEndTime().getTimeInMillis();

                            if (eTime - sTime > 604800000) {
                                Toast.makeText(EventActivity.this, "Event length cannot be longer than 7 days in everyweek mode!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            while (sTime + 604800000 < till) {
                                createEvent(sTime, eTime, Calendar.getInstance().getTimeInMillis(), false);

                                sTime += 604800000;
                                eTime += 604800000;
                            }
                            createEvent(sTime, eTime, Calendar.getInstance().getTimeInMillis(), true);
                        }
                    };
                    cal = Calendar.getInstance();
                    cal.setTimeInMillis(event.getStartTime().getTimeInMillis());
                    dialog = new DatePickerDialog(this, listener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                    dialog.setTitle("Repeat until");
                    dialog.show();
                    break;
                case Event.NO_REPEAT:
                    createEvent(EventActivity.event.getStartTime().getTimeInMillis(), EventActivity.event.getEndTime().getTimeInMillis(), EventActivity.event.getEventId(), true);
                    break;
                default:
                    break;
            }
        } else
            Toast.makeText(this, "No network! Please connect to network first!", Toast.LENGTH_SHORT).show();
    }

    private void createEvent(final long startTime, final long endTime, long eventId, boolean finishActivity) {
        Tools.execute(new MyRunnable(
                this,
                EventActivity.event.isNewEvent() ? getString(R.string.url_event_create, getString(R.string.server_ip)) : getString(R.string.url_event_edit, getString(R.string.server_ip)),
                SignInActivity.loginPrefs.getString(SignInActivity.username, null),
                SignInActivity.loginPrefs.getString(SignInActivity.password, null),
                eventId,
                finishActivity
        ) {
            @Override
            public void run() {
                String url = (String) args[0];
                String username = (String) args[1];
                String password = (String) args[2];
                long eventId = (long) args[3];
                boolean finishActivity = (boolean) args[4];

                JSONObject body = new JSONObject();
                try {
                    body.put("username", username);
                    body.put("password", password);
                    body.put("event_id", eventId);
                    body.put("title", EventActivity.event.getTitle());
                    body.put("stressLevel", EventActivity.event.getStressLevel());
                    body.put("startTime", startTime);
                    body.put("endTime", endTime);
                    if (EventActivity.event.getIntervention() == null) {
                        body.put("intervention", "");
                        body.put("interventionReminder", 0);
                    } else {
                        body.put("intervention", EventActivity.event.getIntervention());
                        body.put("interventionReminder", EventActivity.event.getInterventionReminder());
                    }
                    body.put("stressType", EventActivity.event.getStressType());
                    body.put("stressCause", EventActivity.event.getStressCause());
                    body.put("repeatMode", EventActivity.event.getRepeatMode());
                    body.put("eventReminder", EventActivity.event.getEventReminder());
                    body.put("isEvaluated", EventActivity.event.isEvaluated());

                    JSONObject res = new JSONObject(Tools.post(url, body));
                    switch (res.getInt("result")) {
                        case Tools.RES_OK:
                            if (finishActivity)
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EventActivity.this, EventActivity.event.isNewEvent() ? "Event successfully created!" : "Event has been edited.", Toast.LENGTH_SHORT).show();

                                        setResult(Activity.RESULT_OK);
                                        finish();
                                        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
                                    }
                                });
                            break;
                        case Tools.RES_FAIL:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(EventActivity.this, EventActivity.event.isNewEvent() ? "Failed to create the event." : "Failed to edit the event.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        case Tools.RES_SRV_ERR:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(EventActivity.this, "Failure occurred while processing the request. (SERVER SIDE)", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(EventActivity.this, "Failed to proceed due to an error in connection with server.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if (finishActivity)
                    enableTouch();
            }
        });
    }

    public void pickDateClick(View view) {
        MyOnDateSetListener listener = new MyOnDateSetListener(view) {
            @Override
            public void onDateSet(DatePicker picker, int year, int month, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis((long) view.getTag());
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.YEAR, year);

                view.setTag(cal.getTimeInMillis());
                ((TextView) view).setText(String.format(Locale.US,
                        "%d, %s %d, %s",
                        cal.get(Calendar.YEAR),
                        cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                ));

                switch (view.getId()) {
                    case R.id.txt_event_start_date:
                        Tools.copy_date(cal.getTimeInMillis(), startTime);
                        break;
                    case R.id.txt_event_end_date:
                        Tools.copy_date(cal.getTimeInMillis(), endTime);
                        break;
                    default:
                        break;
                }
            }
        };
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((long) view.getTag());
        DatePickerDialog dialog = new DatePickerDialog(this, listener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void pickTimeClick(View view) {
        MyOnTimeSetListener listener = new MyOnTimeSetListener(view) {
            @Override
            public void onTimeSet(TimePicker picker, int hourOfDay, int minute) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis((long) view.getTag());
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);

                view.setTag(cal.getTimeInMillis());
                ((TextView) view).setText(String.format(Locale.US,
                        "%02d:%02d",
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE))
                );

                switch (view.getId()) {
                    case R.id.txt_event_start_time:
                        Tools.copy_time(cal.getTimeInMillis(), startTime);
                        break;
                    case R.id.txt_event_end_time:
                        Tools.copy_time(cal.getTimeInMillis(), endTime);
                        break;
                    default:
                        break;
                }
            }
        };
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((long) view.getTag());
        TimePickerDialog dialog = new TimePickerDialog(this, listener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public void clickCustomNotification(View view) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialogCustomNotif");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new CustomNotificationDialog();
        Bundle args = new Bundle();
        args.putBoolean("isEventNotification", true);
        dialogFragment.setArguments(args);
        dialogFragment.show(ft, "dialogCustomNotif");

    }

    public void setCustomNotifParams(int minutes, String txt, String timeTxt) {
        customReminderMinutes = minutes;
        customReminderText = txt;
        customNotifTimeTxt = timeTxt;
        eventNotificationGroup.check(R.id.radio_btn_custom);

        customNotifRadioButton.setVisibility(View.VISIBLE);
        customNotifRadioButton.setText(txt);
        //TODO: complete the custom notification part
    }

    public void initFeedbackView(Event event) {

        ViewGroup intervView = findViewById(R.id.feedback_details_interv);
        SeekBar expectedStressLevelSeek = findViewById(R.id.expected_stresslvl_seekbar);
        SeekBar realStressLevelSeek = findViewById(R.id.real_stresslvl_seekbar);
        TextView intervName = findViewById(R.id.intervention_name);
        SeekBar intervEffectiveness = findViewById(R.id.intervention_effectiveness);
        TextView realStressReason = findViewById(R.id.strs_reason_text);
        TextView journal = findViewById(R.id.journal_text);

        // set expected stress level from event variable
        expectedStressLevelSeek.setEnabled(false);
        expectedStressLevelSeek.setProgress(EventActivity.event.getStressLevel());
        int expectedStressColor = Tools.stressLevelToColor(getApplicationContext(), EventActivity.event.getStressLevel());
        expectedStressLevelSeek.getProgressDrawable().setColorFilter(expectedStressColor, PorterDuff.Mode.SRC_IN);
        expectedStressLevelSeek.getThumb().setColorFilter(expectedStressColor, PorterDuff.Mode.SRC_IN);

        // set real stress level from evaluation
        realStressLevelSeek.setEnabled(false);
        /*realStressLevelSeek.setProgress(realStressLevel);
        realStressLevelSeek.getProgressDrawable().setColorFilter(Tools.stressLevelToColor(getApplicationContext(), realStressLevel), PorterDuff.Mode.SRC_IN);
        realStressLevelSeek.getThumb().setColorFilter(Tools.stressLevelToColor(getApplicationContext(), realStressLevel), PorterDuff.Mode.SRC_IN);*/


        if (!event.getIntervention().equals("")) {
            intervView.setVisibility(View.VISIBLE);
            intervName.setText(getResources().getString(R.string.current_interv_title, event.getIntervention()));
            //intervEffectiveness.setProgress(event.getIntervEffectiveness());
        } else {
            intervView.setVisibility(View.GONE);
        }


    }

    private abstract class MyOnDateSetListener implements DatePickerDialog.OnDateSetListener {
        MyOnDateSetListener(View view) {
            this.view = view;
        }

        View view;
    }

    private abstract class MyOnTimeSetListener implements TimePickerDialog.OnTimeSetListener {
        MyOnTimeSetListener(View view) {
            this.view = view;
        }

        View view;
    }

    private abstract class MyTextWatcher implements TextWatcher {
        MyTextWatcher(TextView startDateText, TextView startTimeText, TextView endDateText, TextView endTimeText) {
            this.startDateText = startDateText;
            this.startTimeText = startTimeText;
            this.endDateText = endDateText;
            this.endTimeText = endTimeText;

            startTime = Calendar.getInstance();
            endTime = Calendar.getInstance();
        }

        TextView startDateText, startTimeText, endDateText, endTimeText;
        Calendar startTime, endTime;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        void removeListeners() {
            startDateText.removeTextChangedListener(this);
            startTimeText.removeTextChangedListener(this);
            endDateText.removeTextChangedListener(this);
            endTimeText.removeTextChangedListener(this);
        }

        void addListeners() {
            startDateText.addTextChangedListener(this);
            startTimeText.addTextChangedListener(this);
            endDateText.addTextChangedListener(this);
            endTimeText.addTextChangedListener(this);
        }
    }
}
