package kr.ac.inha.nsl.mindnavigator;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

public class NotifSettingsDialog extends DialogFragment {

    //region Variables
    ViewGroup root;
    TextView sundayTxt, everyMorningTxt, everyEveningTxt;
    static Calendar sunday, everyMorning, everyEvening;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.dialog_notif_settings, container, true);
        init();
        return root;
    }

    public NotifSettingsDialog() {
    }

    private void init() {
        sundayTxt = root.findViewById(R.id.txt_sunday_time);
        everyMorningTxt = root.findViewById(R.id.txt_everymorning_time);
        everyEveningTxt = root.findViewById(R.id.txt_everyevening_time);

        sundayTxt.setTag(sunday.getTimeInMillis());
        everyMorningTxt.setTag(everyMorning.getTimeInMillis());
        everyEveningTxt.setTag(everyEvening.getTimeInMillis());

        sundayTxt.setText(String.format(Locale.US,
                "%02d:%02d",
                sunday.get(Calendar.HOUR_OF_DAY),
                sunday.get(Calendar.MINUTE))
        );

        everyMorningTxt.setText(String.format(Locale.US,
                "%02d:%02d",
                everyMorning.get(Calendar.HOUR_OF_DAY),
                everyMorning.get(Calendar.MINUTE))
        );

        everyEveningTxt.setText(String.format(Locale.US,
                "%02d:%02d",
                everyEvening.get(Calendar.HOUR_OF_DAY),
                everyEvening.get(Calendar.MINUTE))
        );

        root.findViewById(R.id.txt_sunday_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTimeClick(v.findViewById(R.id.txt_sunday_time));
            }
        });

        root.findViewById(R.id.txt_everymorning_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTimeClick(v.findViewById(R.id.txt_everymorning_time));
            }
        });

        root.findViewById(R.id.txt_everyevening_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTimeClick(v.findViewById(R.id.txt_everyevening_time));
            }
        });

        root.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Tools.addSundayNotif(getActivity(), sunday, "Do you have a new schedule for the next week?");
                Tools.addDailyNotif(getActivity(), everyMorning, "Do you have a new schedule today?");
                Tools.addDailyNotif(getActivity(), everyEvening, "Please, evaluate today's events!");
                dismiss();
            }
        });

        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    public void pickTimeClick(View view) {

        MyOnTimeSetListener listener = new MyOnTimeSetListener(view) {
            @Override
            public void onTimeSet(TimePicker picker, int hourOfDay, int minute) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);

                view.setTag(cal.getTimeInMillis());
                ((TextView) view).setText(String.format(Locale.US,
                        "%02d:%02d",
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE))
                );

                switch (view.getId()) {
                    case R.id.txt_sunday_time:
                        sunday = (Calendar) cal.clone();
                        sunday.set(Calendar.DAY_OF_WEEK, 1);
                        sunday.set(Calendar.SECOND, 0);
                        break;
                    case R.id.txt_everymorning_time:
                        everyMorning = (Calendar) cal.clone();
                        everyMorning.set(Calendar.SECOND, 0);
                        break;
                    case R.id.txt_everyevening_time:
                        everyEvening = (Calendar) cal.clone();
                        everyEvening.set(Calendar.SECOND, 0);
                        break;
                    default:
                        break;
                }
            }
        };
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((long) view.getTag());
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), listener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        dialog.show();

    }

    private abstract class MyOnTimeSetListener implements TimePickerDialog.OnTimeSetListener {
        MyOnTimeSetListener(View view) {
            this.view = view;
        }

        View view;
    }

}
