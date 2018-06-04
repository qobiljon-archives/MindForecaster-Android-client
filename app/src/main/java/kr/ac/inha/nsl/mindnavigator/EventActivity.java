package kr.ac.inha.nsl.mindnavigator;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
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
                    InterventionsActivity.result = null;
                    selectedInterv.setText(event.getIntervention());
                    //region Setting a text for intervention reminder
                    String text = null;
                    switch (InterventionsActivity.resultSchedule) {
                        case -1440:
                            text = getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_day_before));
                            break;
                        case -60:
                            text = getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_hour_before));
                            break;
                        case -30:
                            text = getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._30_minutes_before));
                            break;
                        case -10:
                            text = getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._10_minutes_before));
                            break;
                        case 1440:
                            text = getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_day_after));
                            break;
                        case 60:
                            text = getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._1_hour_after));
                            break;
                        case 30:
                            text = getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._30_minutes_after));
                            break;
                        case 10:
                            text = getResources().getString(R.string.intervention_reminder_text, getResources().getString(R.string._10_minutes_after));
                            break;
                    }
                    //endregion
                    intervReminderTxt.setText(text);
                    intervReminderTxt.setVisibility(View.VISIBLE);
                    break;
                case EVALUATION_ACTIVITY:
                    // TODO: Do something
                    break;
                case FEEDBACK_ACTIVITY:
                    // TODO: Do something
                    break;
                default:
                    break;
            }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //region Variables
    private final int EVALUATION_ACTIVITY = 0, INTERVENTION_ACTIVITY = 1, FEEDBACK_ACTIVITY = 2;
    static Event event;

    private ViewGroup inactiveLayout;
    private ViewGroup stressLevelDetails;
    private ViewGroup interventionDetails;
    private ViewGroup repeatNotificationDetails;
    private TextView startDateText, startTimeText, endDateText, endTimeText, selectedInterv, intervReminderTxt, expandDetails, saveButton, cancelButton, deleteButton;
    private RadioGroup stressTypeGroup, repeatModeGroup;
    private EditText eventTitle, stressCause;
    private Switch switchAllDay;
    private SeekBar stressLvl;
    private Button intervEditButton;

    private Calendar startTime, endTime;
    //endregion

    private void init() {
        eventTitle = findViewById(R.id.edit_event_title);
        switchAllDay = findViewById(R.id.all_day_switch);
        startDateText = findViewById(R.id.txt_event_start_date);
        startTimeText = findViewById(R.id.txt_event_start_time);
        endDateText = findViewById(R.id.txt_event_end_date);
        endTimeText = findViewById(R.id.txt_event_end_time);
        stressLvl = findViewById(R.id.stressLvl);
        inactiveLayout = findViewById(R.id.layout_to_be_inactive);
        stressTypeGroup = findViewById(R.id.stress_type_group);
        stressCause = findViewById(R.id.txt_stress_cause);
        stressLevelDetails = findViewById(R.id.stress_level_details);
        interventionDetails = findViewById(R.id.intervention_details);
        repeatNotificationDetails = findViewById(R.id.repeat_notification_details);
        selectedInterv = findViewById(R.id.selected_intervention);
        repeatModeGroup = findViewById(R.id.repeat_mode_group);
        intervReminderTxt = findViewById(R.id.txt_interv_reminder_time);
        expandDetails = findViewById(R.id.text_more_event_options);
        saveButton = findViewById(R.id.btn_save);
        cancelButton = findViewById(R.id.btn_cancel);
        deleteButton = findViewById(R.id.btn_delete);
        ViewGroup postEventLayout = findViewById(R.id.postEventLayout);
        intervEditButton = findViewById(R.id.interv_edit_button);

        Calendar selCal = Calendar.getInstance();
        if (getIntent().hasExtra("selectedDayMillis"))
            selCal.setTimeInMillis(getIntent().getLongExtra("selectedDayMillis", 0));
        if (getIntent().hasExtra("eventId"))
            event = Event.getEventById(getIntent().getLongExtra("eventId", 0));
        else
            event = new Event(0);

        if (event.isNewEvent())
            deleteButton.setVisibility(View.GONE);
        else {
            // Editing an existing event
            selCal = event.getStartTime();
            switchAllDay.setEnabled(false);
            eventTitle.setEnabled(false);
            startDateText.setEnabled(false);
            startTimeText.setEnabled(false);
            endDateText.setEnabled(false);
            endTimeText.setEnabled(false);
            stressLvl.setEnabled(false);
            stressCause.setEnabled(false);
            stressTypeGroup.setEnabled(false);
            for (int n = 0; n < stressTypeGroup.getChildCount(); n++)
                stressTypeGroup.getChildAt(n).setEnabled(false);
            repeatModeGroup.setEnabled(false);
            for (int n = 0; n < repeatModeGroup.getChildCount(); n++)
                repeatModeGroup.getChildAt(n).setEnabled(false);
            intervEditButton.setEnabled(false);

            if (event.getEndTime().before(Calendar.getInstance())) {
                saveButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                postEventLayout.setVisibility(View.VISIBLE);
            } else {
                saveButton.setText(getString(R.string.edit));
                cancelButton.setVisibility(View.GONE);
            }
        }

        startDateText.setText(String.format(Locale.US,
                "%s, %s %d, %d",
                selCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()),
                selCal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                selCal.get(Calendar.DAY_OF_MONTH),
                selCal.get(Calendar.YEAR)
        ));
        startDateText.setTag(selCal.getTimeInMillis());

        startTimeText.setText(String.format(Locale.US,
                "%02d:%02d",
                selCal.get(Calendar.HOUR_OF_DAY),
                selCal.get(Calendar.MINUTE))
        );
        startTimeText.setTag(selCal.getTimeInMillis());
        startTime = (Calendar) selCal.clone();

        if (event.isNewEvent())
            selCal.add(Calendar.HOUR_OF_DAY, 1);
        else
            selCal = event.getEndTime();
        endDateText.setText(String.format(Locale.US,
                "%s, %s %d, %d",
                selCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()),
                selCal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                selCal.get(Calendar.DAY_OF_MONTH),
                selCal.get(Calendar.YEAR)
        ));
        endDateText.setTag(selCal.getTimeInMillis());

        endTimeText.setText(String.format(Locale.US,
                "%02d:%02d",
                selCal.get(Calendar.HOUR_OF_DAY),
                selCal.get(Calendar.MINUTE))
        );
        endTimeText.setTag(selCal.getTimeInMillis());
        endTime = (Calendar) selCal.clone();

        MyTextWatcher timePickingCorrector = new MyTextWatcher(startDateText, startTimeText, endDateText, endTimeText) {
            @Override
            public void afterTextChanged(Editable s) {
                Calendar startTime = Calendar.getInstance(), endTime = Calendar.getInstance();
                Tools.copy_date((long) startDateText.getTag(), startTime);
                Tools.copy_time((long) startTimeText.getTag(), startTime);
                Tools.copy_date((long) endDateText.getTag(), endTime);
                Tools.copy_time((long) endTimeText.getTag(), endTime);

                if (endTime.before(startTime)) {
                    endTime.setTimeInMillis(startTime.getTimeInMillis());
                    endTime.add(Calendar.HOUR_OF_DAY, 1);

                    startDateText.removeTextChangedListener(this);
                    startTimeText.removeTextChangedListener(this);
                    endDateText.removeTextChangedListener(this);
                    endTimeText.removeTextChangedListener(this);
                    endDateText.setText(String.format(Locale.US,
                            "%s, %s %d, %d",
                            endTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()),
                            endTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                            endTime.get(Calendar.DAY_OF_MONTH),
                            endTime.get(Calendar.YEAR)
                    ));
                    endTimeText.setText(String.format(Locale.US,
                            "%02d:%02d",
                            endTime.get(Calendar.HOUR_OF_DAY),
                            endTime.get(Calendar.MINUTE))
                    );
                    startDateText.addTextChangedListener(this);
                    startTimeText.addTextChangedListener(this);
                    endDateText.addTextChangedListener(this);
                    endTimeText.addTextChangedListener(this);
                }

                endDateText.setTag(endTime.getTimeInMillis());
                endTimeText.setTag(endTime.getTimeInMillis());
            }
        };
        startDateText.addTextChangedListener(timePickingCorrector);
        startTimeText.addTextChangedListener(timePickingCorrector);
        endDateText.addTextChangedListener(timePickingCorrector);
        endTimeText.addTextChangedListener(timePickingCorrector);

        stressLvl.getProgressDrawable().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.green, null), PorterDuff.Mode.SRC_IN);
        stressLvl.getThumb().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.green, null), PorterDuff.Mode.SRC_IN);
        stressLvl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                stressLvl.getProgressDrawable().setColorFilter(Tools.stressLevelToColor(progress), PorterDuff.Mode.SRC_IN);
                stressLvl.getThumb().setColorFilter(Tools.stressLevelToColor(progress), PorterDuff.Mode.SRC_IN);
                if (progress >= 0 && progress < 50) {
                    inactiveLayout.setVisibility(View.GONE);
                } else if (progress > 50 && progress < 80) {
                    inactiveLayout.setVisibility(View.VISIBLE);
                } else {
                    inactiveLayout.setVisibility(View.VISIBLE);
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

        if (!event.isNewEvent())
            fillOutExistingValues();
    }

    private void fillOutExistingValues() {
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

    public void moreOptionsClick(View view) {
        expandDetails.setVisibility(View.GONE);
        findViewById(R.id.more_options_layout).setVisibility(View.VISIBLE);
    }

    public void expandStressLevelClick(View view) {
        TextView optionView = (TextView) view;

        if (stressLevelDetails.getVisibility() == View.VISIBLE) {
            stressLevelDetails.setVisibility(View.GONE);
            optionView.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_expand), null);
        } else {
            stressLevelDetails.setVisibility(View.VISIBLE);
            optionView.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_collapse), null);
        }
    }

    public void expandRepeatNotificationClick(View view) {
        TextView optionView = (TextView) view;

        if (repeatNotificationDetails.getVisibility() == View.VISIBLE) {
            repeatNotificationDetails.setVisibility(View.GONE);
            optionView.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_expand), null);
        } else {
            repeatNotificationDetails.setVisibility(View.VISIBLE);
            optionView.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_collapse), null);
        }
    }

    public void expandInterventionsClick(View view) {
        TextView optionView = (TextView) view;

        if (interventionDetails.getVisibility() == View.VISIBLE) {
            interventionDetails.setVisibility(View.GONE);
            optionView.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_expand), null);
        } else {
            interventionDetails.setVisibility(View.VISIBLE);
            optionView.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.img_collapse), null);
            if (selectedInterv.length() > 1)
                intervReminderTxt.setVisibility(View.VISIBLE);
        }

    }

    public void editInterventionClick(View view) {
        Intent intent = new Intent(this, InterventionsActivity.class);
        startActivityForResult(intent, INTERVENTION_ACTIVITY);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public void evaluationClick(View view) {
        Intent intent = new Intent(this, EvaluationActivity.class);
        startActivityForResult(intent, EVALUATION_ACTIVITY);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public void feedbackClick(View view) {
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivityForResult(intent, FEEDBACK_ACTIVITY);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
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
                                    getString(R.string.url_event_delete),
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
                                                runOnUiThread(new MyRunnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(EventActivity.this, "Event has been deleted!", Toast.LENGTH_SHORT).show();

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
            intervEditButton.setEnabled(true);
            return;
        }

        event.setTitle(eventTitle.getText().toString());
        event.setStressLevel(stressLvl.getProgress());

        Tools.copy_date((long) startDateText.getTag(), startTime);
        Tools.copy_time((long) startTimeText.getTag(), startTime);
        Tools.copy_date((long) endDateText.getTag(), endTime);
        Tools.copy_time((long) endTimeText.getTag(), endTime);
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

        Event event = EventActivity.event;
        if (Tools.isNetworkAvailable(this))
            Tools.execute(new MyRunnable(
                    event.isNewEvent() ? getString(R.string.url_event_create) : getString(R.string.url_event_edit),
                    SignInActivity.loginPrefs.getString(SignInActivity.username, null),
                    SignInActivity.loginPrefs.getString(SignInActivity.password, null),
                    event
            ) {
                @Override
                public void run() {
                    String url = (String) args[0];
                    String username = (String) args[1];
                    String password = (String) args[2];
                    Event event = (Event) args[3];

                    JSONObject body = new JSONObject();
                    try {
                        body.put("username", username);
                        body.put("password", password);
                        body.put("event_id", event.getEventId());
                        body.put("title", event.getTitle());
                        body.put("stressLevel", event.getStressLevel());
                        body.put("startTime", event.getStartTime().getTimeInMillis());
                        body.put("endTime", event.getEndTime().getTimeInMillis());
                        if (event.getIntervention() == null) {
                            body.put("intervention", "");
                            body.put("interventionReminder", 0);
                        } else {
                            body.put("intervention", event.getIntervention());
                            body.put("interventionReminder", event.getInterventionReminder());
                        }
                        body.put("stressType", event.getStressType());
                        body.put("stressCause", event.getStressCause());
                        body.put("repeatMode", event.getRepeatMode());

                        JSONObject res = new JSONObject(Tools.post(url, body));
                        switch (res.getInt("result")) {
                            case Tools.RES_OK:
                                runOnUiThread(new MyRunnable(
                                        event.isNewEvent()
                                ) {
                                    @Override
                                    public void run() {
                                        boolean isNewEvent = (boolean) args[0];
                                        Toast.makeText(EventActivity.this, isNewEvent ? "Event successfully created!" : "Event has been edited.", Toast.LENGTH_SHORT).show();

                                        setResult(Activity.RESULT_OK);
                                        finish();
                                        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
                                    }
                                });
                                break;
                            case Tools.RES_FAIL:
                                runOnUiThread(new MyRunnable(
                                        event.isNewEvent()
                                ) {
                                    @Override
                                    public void run() {
                                        boolean isNewEvent = (boolean) args[0];
                                        Toast.makeText(EventActivity.this, isNewEvent ? "Failed to create the event." : "Failed to edit the event.", Toast.LENGTH_SHORT).show();
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
                }
            });
        else {
            // TODO: Save an action for later
            Toast.makeText(this, "No network! Please connect to network first!", Toast.LENGTH_SHORT).show();
        }
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
                        "%s, %s %d, %d",
                        cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()),
                        cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.YEAR)
                ));
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
            }
        };
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((long) view.getTag());
        TimePickerDialog dialog = new TimePickerDialog(this, listener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        dialog.show();
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
        }

        TextView startDateText, startTimeText, endDateText, endTimeText;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    }
}
