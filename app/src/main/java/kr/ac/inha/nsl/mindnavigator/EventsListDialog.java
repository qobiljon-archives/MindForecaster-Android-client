package kr.ac.inha.nsl.mindnavigator;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EventsListDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.dialog_daily_eventlist, container, true);
        init();
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("HOME", "DIALOG HOME ACTIVITY RESULT");
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case MainActivity.EVENT_ACTIVITY:
                    if (getActivity() instanceof MainActivity)
                        ((MainActivity) getActivity()).updateCalendarView();
                    dismiss();
                    break;
                default:
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // region Variables

    private ViewGroup root;

    private View.OnClickListener onEventClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), EventActivity.class);
            intent.putExtra("eventId", (long) view.getTag());
            startActivityForResult(intent, MainActivity.EVENT_ACTIVITY);
        }
    };
    //endregion

    public EventsListDialog() {

    }

    private void init() {
        Calendar selectedDay = Calendar.getInstance();
        selectedDay.setTimeInMillis(getArguments().getLong("selectedDayMillis"));

        TextView dateTxt = root.findViewById(R.id.cell_date);

        dateTxt.setText(String.format(Locale.US,
                "%s, %02d %s %02d",
                selectedDay.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()),
                selectedDay.get(Calendar.DAY_OF_MONTH),
                selectedDay.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                selectedDay.get(Calendar.YEAR)
        ));

        ArrayList<Event> dayEvents = Event.getOneDayEvents(selectedDay);
        for (Event event : dayEvents) {
            getActivity().getLayoutInflater().inflate(R.layout.event_element_dailyview, root);
            ViewGroup view = (ViewGroup) root.getChildAt(root.getChildCount() - 1);
            view.setTag(event.getEventId());
            view.setOnClickListener(onEventClickListener);
            TextView titleText = view.findViewById(R.id.event_title_text_view);
            TextView dateText = view.findViewById(R.id.event_date_text_view);
            TextView stressLevel = view.findViewById(R.id.stress_lvl_box);

            stressLevel.setText(String.valueOf(event.getStressLevel()));
            stressLevel.setBackgroundColor(Tools.stressLevelToColor(event.getStressLevel()));
            titleText.setText(event.getTitle());

            dateText.setText(String.format(Locale.US,
                    "%02d:%02d - %02d:%02d",
                    event.getStartTime().get(Calendar.HOUR),
                    event.getStartTime().get(Calendar.MINUTE),
                    event.getEndTime().get(Calendar.HOUR),
                    event.getEndTime().get(Calendar.MINUTE))
            );
        }

        //Inflating a "Close" button to the end of dialog
        getActivity().getLayoutInflater().inflate(R.layout.button_close, root);
        Button btnClose = root.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
