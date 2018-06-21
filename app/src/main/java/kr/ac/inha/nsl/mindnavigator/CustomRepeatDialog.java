package kr.ac.inha.nsl.mindnavigator;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;


public class CustomRepeatDialog extends DialogFragment {

    private ViewGroup root;
    private TextView endsOnDate;
    private Calendar selectedDay = Calendar.getInstance();
    private Calendar endDatePick = Calendar.getInstance();
    private CheckBox[] checkBoxesArray = new CheckBox[8];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.dialog_custom_repeat, container, true);
        init();
        return root;
    }

    public void init() {
        checkBoxesArray[1] = root.findViewById(R.id.sun);
        checkBoxesArray[2] = root.findViewById(R.id.mon);
        checkBoxesArray[3] = root.findViewById(R.id.tue);
        checkBoxesArray[4] = root.findViewById(R.id.wed);
        checkBoxesArray[5] = root.findViewById(R.id.thu);
        checkBoxesArray[6] = root.findViewById(R.id.fri);
        checkBoxesArray[7] = root.findViewById(R.id.sat);

        selectedDay.setTimeInMillis(getArguments().getLong("selectedDayMillis", 0));
        checkBoxesArray[selectedDay.get(Calendar.DAY_OF_WEEK)].setChecked(true);


        endsOnDate = root.findViewById(R.id.txt_ends_on_date);
        final ViewGroup weekModeView = root.findViewById(R.id.repeat_week_mode);
        Spinner spinner = root.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(), R.array.repeat_array_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItemPosition() == 1) {
                    weekModeView.setVisibility(View.VISIBLE);
                } else weekModeView.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: "OK" button function to be implemented
            }
        });

        root.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    public void clickEndDate(){

    }

    public CustomRepeatDialog() {

    }
}
