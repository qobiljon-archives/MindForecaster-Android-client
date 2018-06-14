package kr.ac.inha.nsl.mindnavigator;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class CustomNotificationDialog extends DialogFragment {

    private ViewGroup root;
    private EditText numberTxt;
    private Spinner spinner;
    private int minutes;
    private boolean day = false, hour = false, minute = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.dialog_custom_notification, container, true);
        init();
        return root;
    }

    private void init() {
        numberTxt = root.findViewById(R.id.number);
        spinner = root.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(), R.array.reminder_array_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItemPosition() == 1)
                    hour = true;
                else if (parent.getSelectedItemPosition() == 2)
                    day = true;
                else
                    minute = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (day)
                    minutes = Integer.parseInt(numberTxt.getText().toString()) * 24 * 60;
                else if (hour)
                    minutes = Integer.parseInt(numberTxt.getText().toString()) * 60;

                if (getActivity() instanceof EventActivity)
                    ((EventActivity) getActivity()).setNotifMinutes(minutes);
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


    public CustomNotificationDialog() {
    }
}
