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
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

import org.json.JSONArray;
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
    static long repeatTillTime;

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
    private ViewGroup weekdaysGroup;
    private CheckBox[] repeatWeeklDayChecks = new CheckBox[7];

    private RadioButton customNotifRadioButton;

    private String customReminderText; // customized reminder text
    private int customReminderMinutes; //customized reminder minutes
    static String customNotifTimeTxt; //customized reminder time-text for notification text ( Ex.: 2 day(s) )
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
        TextView tabInterv = findViewById(R.id.tab_interventions);
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

        weekdaysGroup = findViewById(R.id.weekdays_group);
        repeatWeeklDayChecks[0] = findViewById(R.id.sun);
        repeatWeeklDayChecks[1] = findViewById(R.id.mon);
        repeatWeeklDayChecks[2] = findViewById(R.id.tue);
        repeatWeeklDayChecks[3] = findViewById(R.id.wed);
        repeatWeeklDayChecks[4] = findViewById(R.id.thu);
        repeatWeeklDayChecks[5] = findViewById(R.id.fri);
        repeatWeeklDayChecks[6] = findViewById(R.id.sat);


        if (getIntent().hasExtra("eventId")) {
            // Editing an existing event
            event = Event.getEventById(getIntent().getLongExtra("eventId", 0));
            repeatTillTime = 0;

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

            if (event.getEndTime().before(Calendar.getInstance(Locale.US))) {
                saveButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                postEventLayout.setVisibility(View.VISIBLE);

                tabNotif.setVisibility(View.GONE);
                tabRepeat.setVisibility(View.GONE);
                tabStressLvl.setVisibility(View.GONE);
                tabInterv.setVisibility(View.GONE);

                if (event.isEvaluated()) {
                    initFeedbackView();
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
            repeatTillTime = 0;
            event.setStartTime(Calendar.getInstance(Locale.US));
            event.setEndTime(Calendar.getInstance(Locale.US));
            deleteButton.setVisibility(View.GONE);

            if (getIntent().hasExtra("selectedDayMillis")) {
                Calendar startTime = event.getStartTime();
                Calendar endTime = event.getEndTime();

                startTime.setTimeInMillis(getIntent().getLongExtra("selectedDayMillis", 0));
                startTime.set(Calendar.HOUR_OF_DAY, 9);
                startTime.set(Calendar.MINUTE, 0);
                endTime.setTimeInMillis(startTime.getTimeInMillis());
                endTime.add(Calendar.HOUR_OF_DAY, 1);

                event.setStartTime(startTime);
                event.setEndTime(endTime);
            }
        }

        Calendar startTime = event.getStartTime();
        Calendar endTime = event.getEndTime();

        startDateText.setText(String.format(Locale.US,
                "%d, %s %d, %s",
                startTime.get(Calendar.YEAR),
                startTime.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                startTime.get(Calendar.DAY_OF_MONTH),
                startTime.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
        ));
        startTimeText.setText(String.format(Locale.US,
                "%02d:%02d",
                startTime.get(Calendar.HOUR_OF_DAY),
                startTime.get(Calendar.MINUTE))
        );
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

        stressLvl.getProgressDrawable().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null), PorterDuff.Mode.SRC_IN);
        stressLvl.getThumb().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null), PorterDuff.Mode.SRC_IN);

        switchAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startTimeText.setVisibility(View.GONE);
                    endTimeText.setVisibility(View.GONE);

                    Calendar startTime = event.getStartTime();
                    Calendar endTime = event.getEndTime();

                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.MILLISECOND, 0);

                    event.setStartTime(startTime);
                    event.setEndTime(endTime);
                } else {
                    startTimeText.setVisibility(View.VISIBLE);
                    endTimeText.setVisibility(View.VISIBLE);
                }
            }
        });

        repeatModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, final int checkedId) {
                for (CheckBox cb : repeatWeeklDayChecks)
                    cb.setChecked(false);
                weekdaysGroup.setVisibility(View.GONE);

                if (checkedId == R.id.no_repeat_radio) {
                    event.setRepeatMode(Event.NO_REPEAT);
                    repeatTillTime = 0;
                    return;
                }

                Calendar cal = Calendar.getInstance(Locale.US);
                cal.setTimeInMillis(event.getStartTime().getTimeInMillis());

                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker picker, int year, int month, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance(Locale.US);
                        cal.set(year, month, dayOfMonth, 0, 0, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        if (event.getStartTime().after(cal))
                            repeatTillTime = 0;
                        else
                            repeatTillTime = cal.getTimeInMillis();

                        switch (checkedId) {
                            case R.id.everyday_repeat_radio:
                                event.setRepeatMode(Event.REPEAT_EVERYDAY);
                                break;
                            case R.id.everyweek_repeat_radio:
                                for (CheckBox cb : repeatWeeklDayChecks)
                                    cb.setChecked(false);
                                repeatWeeklDayChecks[event.getStartTime().get(Calendar.DAY_OF_WEEK) - 1].setChecked(true);
                                weekdaysGroup.setVisibility(View.VISIBLE);
                                event.setRepeatMode(Event.REPEAT_WEEKLY);
                                break;
                            default:
                                break;
                        }
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(EventActivity.this, listener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            for (CheckBox cb : repeatWeeklDayChecks)
                                cb.setChecked(false);
                            weekdaysGroup.setVisibility(View.GONE);
                            repeatModeGroup.check(R.id.no_repeat_radio);
                        }
                    }
                });
                dialog.setTitle("Repeat until");
                dialog.show();
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
                                        body.put("eventId", eventId);

                                        JSONObject res = new JSONObject(Tools.post(url, body));
                                        switch (res.getInt("result")) {
                                            case Tools.RES_OK:
                                                runOnUiThread(new MyRunnable(activity, eventId) {
                                                    @Override
                                                    public void run() {
                                                        long eventId = (long) args[0];
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
                        else
                            Toast.makeText(EventActivity.this, "Please connect to a network first!", Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        if (Tools.isNetworkAvailable(EventActivity.this))
                            Tools.execute(new MyRunnable(
                                    EventActivity.this,
                                    getString(R.string.url_event_delete, getString(R.string.server_ip)),
                                    SignInActivity.loginPrefs.getString(SignInActivity.username, null),
                                    SignInActivity.loginPrefs.getString(SignInActivity.password, null),
                                    event.getRepeatId()
                            ) {
                                @Override
                                public void run() {
                                    String url = (String) args[0];
                                    String username = (String) args[1];
                                    String password = (String) args[2];
                                    long repeatId = (long) args[3];

                                    JSONObject body = new JSONObject();
                                    try {
                                        body.put("username", username);
                                        body.put("password", password);
                                        body.put("repeatId", repeatId);

                                        JSONObject res = new JSONObject(Tools.post(url, body));
                                        long[] deletedIds = null;
                                        if (res.has("deletedIds")) {
                                            JSONArray array = res.getJSONArray("deletedIds");
                                            deletedIds = new long[array.length()];
                                            for (int n = 0; n < deletedIds.length; n++)
                                                deletedIds[n] = array.getLong(n);
                                        }
                                        switch (res.getInt("result")) {
                                            case Tools.RES_OK:
                                                runOnUiThread(new MyRunnable(activity, (Object) deletedIds) {
                                                    @Override
                                                    public void run() {
                                                        long[] deletedIds = (long[]) args[0];

                                                        Toast.makeText(EventActivity.this, "Events have been deleted!", Toast.LENGTH_SHORT).show();
                                                        for (long deletedId : deletedIds)
                                                            Tools.cancelNotif(EventActivity.this, (int) deletedId);
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
                                                        Toast.makeText(EventActivity.this, "Failed to delete the recurring events.", Toast.LENGTH_SHORT).show();
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
                        else
                            Toast.makeText(EventActivity.this, "Please connect to a network first!", Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    default:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (event.getRepeatId() == 0)
            builder.setMessage("Are you sure you want to delete this event?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        else {
            builder.setMessage("Delete recurring event")
                    .setPositiveButton("This event only", dialogClickListener)
                    .setNeutralButton("All recurring events", dialogClickListener)
                    .setNegativeButton("Cancel", dialogClickListener).show();
        }
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

        // check the input values
        if (eventTitle.length() > 0)
            event.setTitle(eventTitle.getText().toString());
        else {
            Toast.makeText(this, "Please, type the event title", Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar startTime = event.getStartTime();
        Calendar endTime = event.getEndTime();
        if (!(startTime.after(Calendar.getInstance(Locale.US)) && startTime.before(endTime))) {
            Toast.makeText(this, "Event start time must be between it's end and present time. Please try again!", Toast.LENGTH_LONG).show();
            return;
        }

        event.setStressLevel(stressLvl.getProgress());

        if (switchAllDay.isChecked()) {
            startTime.set(Calendar.HOUR_OF_DAY, 0);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);
            startTime.set(Calendar.MILLISECOND, 0);

            endTime.setTimeInMillis(startTime.getTimeInMillis());
            endTime.add(Calendar.DAY_OF_MONTH, 1);

            event.setStartTime(startTime);
            event.setEndTime(endTime);
        }

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
            switch (event.getRepeatMode()) {
                case Event.NO_REPEAT:
                    createEvent(EventActivity.event.getStartTime().getTimeInMillis(), EventActivity.event.getEndTime().getTimeInMillis(), EventActivity.event.getEventId(), 0, true);
                    break;
                case Event.REPEAT_EVERYDAY:
                    createEvent(EventActivity.event.getStartTime().getTimeInMillis(), EventActivity.event.getEndTime().getTimeInMillis(), EventActivity.event.getEventId(), Calendar.getInstance(Locale.US).getTimeInMillis(), true);
                    break;
                case Event.REPEAT_WEEKLY:
                    createEvent(EventActivity.event.getStartTime().getTimeInMillis(), EventActivity.event.getEndTime().getTimeInMillis(), EventActivity.event.getEventId(), Calendar.getInstance(Locale.US).getTimeInMillis(), true);
                    long repeatId = Calendar.getInstance(Locale.US).getTimeInMillis();

                    // a dynamically updaing row
                    Calendar[] startTimeCals = new Calendar[7];
                    Calendar[] endTimeCals = new Calendar[7];

                    // for building up the first row
                    final Calendar evStartTime = event.getStartTime();
                    final Calendar evEndTime = event.getEndTime();

                    for (int n = 0; n < repeatWeeklDayChecks.length; n++) {
                        if (!repeatWeeklDayChecks[n].isChecked())
                            continue;
                        if (startTimeCals[n] == null) {
                            // building up the first row calendars
                            startTimeCals[n] = (Calendar) evStartTime.clone();
                            endTimeCals[n] = (Calendar) evEndTime.clone();

                            // shifting the start-time to the needed day of the week
                            startTimeCals[n].set(Calendar.DAY_OF_WEEK, n + 1);
                            if (startTimeCals[n].before(evStartTime))
                                startTimeCals[n].add(Calendar.DAY_OF_MONTH, 7);

                            // adjusting the end-time in a synchronized way, using the start-time delta
                            endTimeCals[n].add(Calendar.MILLISECOND, (int) (startTimeCals[n].getTimeInMillis() - evStartTime.getTimeInMillis()));
                        }

                        // rock it buddy
                        createRepeatingEvents(startTimeCals[n].getTimeInMillis(), endTimeCals[n].getTimeInMillis(), repeatTillTime, repeatId, WEEK_MILLIS);
                    }
                    break;
                default:
                    break;
            }
        } else
            Toast.makeText(this, "No network! Please connect to network first!", Toast.LENGTH_SHORT).show();
    }

    private void createRepeatingEvents(long origStartTime, long origEndTime, long repeatUntil, long repeatId, int jump) {
        if (origEndTime - origStartTime > jump) {
            Toast.makeText(EventActivity.this, "Event length is too long for this repeat mode, please recheck!", Toast.LENGTH_SHORT).show();
            return;
        }

        while (origStartTime + jump < repeatUntil) {
            createEvent(origStartTime, origEndTime, Calendar.getInstance(Locale.US).getTimeInMillis(), repeatId, false);

            origStartTime += jump;
            origEndTime += jump;
        }
        createEvent(origStartTime, origEndTime, Calendar.getInstance(Locale.US).getTimeInMillis(), repeatId, true);
    }

    private void createEvent(long startTime, long endTime, long eventId, long repeatId, boolean finishActivity) {
        event.setRepeatId(repeatId);
        Calendar sCal = Calendar.getInstance(Locale.US);
        sCal.setTimeInMillis(startTime);
        event.setStartTime(sCal);
        Calendar eCal = Calendar.getInstance(Locale.US);
        eCal.setTimeInMillis(endTime);
        event.setEndTime(eCal);

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
                    body.put("eventId", eventId);
                    body.put("title", EventActivity.event.getTitle());
                    body.put("stressLevel", EventActivity.event.getStressLevel());
                    body.put("startTime", EventActivity.event.getStartTime().getTimeInMillis());
                    body.put("endTime", EventActivity.event.getEndTime().getTimeInMillis());
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
                    body.put("repeatId", EventActivity.event.getRepeatId());
                    body.put("repeatTill", repeatTillTime);
                    body.put("eventReminder", EventActivity.event.getEventReminder());
                    body.put("isEvaluated", EventActivity.event.isEvaluated());

                    JSONObject res = new JSONObject(Tools.post(url, body));
                    Thread.sleep(100);
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
                } catch (JSONException | IOException | InterruptedException e) {
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

    public void pickStartDateClick(View view) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker picker, int year, int month, int dayOfMonth) {
                Calendar startDate = event.getStartTime();
                startDate.set(year, month, dayOfMonth);
                event.setStartTime(startDate);

                startDateText.setText(String.format(Locale.US,
                        "%d, %s %d, %s",
                        startDate.get(Calendar.YEAR),
                        startDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                        startDate.get(Calendar.DAY_OF_MONTH),
                        startDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                ));
            }
        };
        Calendar cal = event.getStartTime();
        DatePickerDialog dialog = new DatePickerDialog(this, listener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void pickEndDateClick(View view) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker picker, int year, int month, int dayOfMonth) {
                Calendar endDate = event.getEndTime();
                endDate.set(year, month, dayOfMonth);
                event.setEndTime(endDate);

                endDateText.setText(String.format(Locale.US,
                        "%d, %s %d, %s",
                        endDate.get(Calendar.YEAR),
                        endDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                        endDate.get(Calendar.DAY_OF_MONTH),
                        endDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
                ));
            }
        };
        Calendar cal = event.getEndTime();
        DatePickerDialog dialog = new DatePickerDialog(this, listener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void pickStartTimeClick(View view) {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker picker, int hourOfDay, int minute) {
                Calendar startTime = event.getStartTime();
                startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTime.set(Calendar.MINUTE, minute);
                event.setStartTime(startTime);

                startTimeText.setText(String.format(Locale.US,
                        "%02d:%02d",
                        startTime.get(Calendar.HOUR_OF_DAY),
                        startTime.get(Calendar.MINUTE))
                );
            }
        };
        Calendar cal = event.getStartTime();
        TimePickerDialog dialog = new TimePickerDialog(this, listener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        dialog.show();
    }

    public void pickEndTimeClick(View view) {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker picker, int hourOfDay, int minute) {
                Calendar endTime = event.getEndTime();
                endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endTime.set(Calendar.MINUTE, minute);
                event.setEndTime(endTime);

                endTimeText.setText(String.format(Locale.US,
                        "%02d:%02d",
                        endTime.get(Calendar.HOUR_OF_DAY),
                        endTime.get(Calendar.MINUTE))
                );
            }
        };
        Calendar cal = event.getEndTime();
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

    public void initFeedbackView() {

        ViewGroup intervView = findViewById(R.id.feedback_details_interv);
        SeekBar expectedStressLevelSeek = findViewById(R.id.expected_stresslvl_seekbar);
        TextView intervName = findViewById(R.id.intervention_name);
        final SeekBar realStressLevelSeek = findViewById(R.id.real_stresslvl_seekbar);
        final SeekBar intervEffectiveness = findViewById(R.id.intervention_effectiveness);
        final TextView realStressReason = findViewById(R.id.strs_reason_text);
        final TextView journalTxt = findViewById(R.id.journal_text);

        // set expected stress level from event variable
        expectedStressLevelSeek.setEnabled(false);
        expectedStressLevelSeek.setProgress(EventActivity.event.getStressLevel());
        int expectedStressColor = Tools.stressLevelToColor(getApplicationContext(), EventActivity.event.getStressLevel());
        expectedStressLevelSeek.getProgressDrawable().setColorFilter(expectedStressColor, PorterDuff.Mode.SRC_IN);
        expectedStressLevelSeek.getThumb().setColorFilter(expectedStressColor, PorterDuff.Mode.SRC_IN);

        if (!event.getIntervention().equals("")) {
            intervView.setVisibility(View.VISIBLE);
            intervName.setText(getResources().getString(R.string.current_interv_title, event.getIntervention()));
        } else {
            intervView.setVisibility(View.GONE);
        }

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
                                        eventEval.get("realStressLevel"),
                                        eventEval.get("intervEffectiveness"),
                                        eventEval.get("realStressCause"),
                                        eventEval.get("journal")

                                ) {
                                    @Override
                                    public void run() {
                                        int realStressLevel = (int) args[0];
                                        int interventionEffectiveness = (int) args[1];
                                        String realStressCause = (String) args[2];
                                        String journalString = (String) args[3];

                                        // set real stress level from evaluation
                                        realStressLevelSeek.setEnabled(false);
                                        realStressLevelSeek.setProgress(realStressLevel);
                                        realStressLevelSeek.getProgressDrawable().setColorFilter(Tools.stressLevelToColor(getApplicationContext(), realStressLevel), PorterDuff.Mode.SRC_IN);
                                        realStressLevelSeek.getThumb().setColorFilter(Tools.stressLevelToColor(getApplicationContext(), realStressLevel), PorterDuff.Mode.SRC_IN);

                                        intervEffectiveness.setEnabled(false);
                                        intervEffectiveness.setProgress(interventionEffectiveness);

                                        realStressReason.setText(realStressCause);
                                        journalTxt.setText(journalString);

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
}
