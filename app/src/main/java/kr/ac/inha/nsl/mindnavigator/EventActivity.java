package kr.ac.inha.nsl.mindnavigator;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
                    InterventionsActivity.result = null;
                    selectedInterv.setText(event.getIntervention());
                    Toast.makeText(this, String.valueOf(InterventionsActivity.resultSchedule), Toast.LENGTH_SHORT).show();
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

    private ViewGroup inactiveLayout, stressLevelDetails, interventionDetails, repeatNotificationDetails;
    private TextView startDateText, startTimeText, endDateText, endTimeText, selectedInterv;
    private RadioGroup stressTypeGroup, repeatModeGroup;
    private EditText eventTitle, stressCause;
    private Switch switchAllDay, shareSwitch;
    private SeekBar stressLvl;

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
        shareSwitch = findViewById(R.id.share_switch);
        selectedInterv = findViewById(R.id.selected_intervention);
        repeatModeGroup = findViewById(R.id.repeat_mode_group);

        Calendar selCal = Calendar.getInstance();
        selCal.setTimeInMillis(getIntent().getLongExtra("selectedDayMillis", 0));

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

        selCal.add(Calendar.HOUR, 1);
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
                    endDateText.addTextChangedListener(this);
                    endTimeText.addTextChangedListener(this);
                }

                startTimeText.setTag(startTime.getTimeInMillis());
            }
        };
        startDateText.addTextChangedListener(timePickingCorrector);
        startTimeText.addTextChangedListener(timePickingCorrector);
        endDateText.addTextChangedListener(timePickingCorrector);
        endTimeText.addTextChangedListener(timePickingCorrector);

        stressLvl.getProgressDrawable().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null), PorterDuff.Mode.SRC_IN);
        stressLvl.getThumb().setColorFilter(ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null), PorterDuff.Mode.SRC_IN);
        stressLvl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress >= 0 && progress < 50) {
                    int slvl0Col = ResourcesCompat.getColor(getResources(), R.color.slvl0_color, null);
                    stressLvl.getProgressDrawable().setColorFilter(slvl0Col, PorterDuff.Mode.SRC_IN);
                    stressLvl.getThumb().setColorFilter(slvl0Col, PorterDuff.Mode.SRC_IN);
                    inactiveLayout.setVisibility(View.GONE);
                } else if (progress > 50 && progress < 80) {
                    int slvl1Col = ResourcesCompat.getColor(getResources(), R.color.slvl1_color, null);
                    stressLvl.getProgressDrawable().setColorFilter(slvl1Col, PorterDuff.Mode.SRC_IN);
                    stressLvl.getThumb().setColorFilter(slvl1Col, PorterDuff.Mode.SRC_IN);
                    inactiveLayout.setVisibility(View.VISIBLE);
                } else {
                    int slvl2Col = ResourcesCompat.getColor(getResources(), R.color.slvl2_color, null);
                    stressLvl.getProgressDrawable().setColorFilter(slvl2Col, PorterDuff.Mode.SRC_IN);
                    stressLvl.getThumb().setColorFilter(slvl2Col, PorterDuff.Mode.SRC_IN);
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

                    startTime.set(Calendar.HOUR, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.MILLISECOND, 0);
                } else {
                    startTimeText.setVisibility(View.VISIBLE);
                    endTimeText.setVisibility(View.VISIBLE);
                }
            }
        });

        event = new Event();
    }

    public void moreOptionsClick(View view) {
        findViewById(R.id.text_more_event_options).setVisibility(View.GONE);
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
        finish();
        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
    }

    public void saveClick(View view) {
        event.setTitle(eventTitle.getText().toString());

        Tools.copy_date((long) startDateText.getTag(), startTime);
        Tools.copy_time((long) startTimeText.getTag(), startTime);
        Tools.copy_date((long) endDateText.getTag(), endTime);
        Tools.copy_time((long) endTimeText.getTag(), endTime);
        if (switchAllDay.isChecked()) {
            startTime.set(Calendar.HOUR, 0);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);
            startTime.set(Calendar.MILLISECOND, 0);

            endTime.add(Calendar.DAY_OF_MONTH, 1);
            endTime.set(Calendar.HOUR, 0);
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
        event.setSharing(shareSwitch.isChecked());
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

        finish();
        overridePendingTransition(R.anim.activity_in_reverse, R.anim.activity_out_reverse);
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
        TimePickerDialog dialog = new TimePickerDialog(this, listener, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true);
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
