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
import android.widget.Toast;

public class CustomNotificationDialog extends DialogFragment {

    //region Variables
    private String customNotifText;
    private String customNotifTimeTxt;
    private ViewGroup root;
    private EditText numberTxt;
    private int minutes;
    private boolean day = false, hour = false;
    private boolean before = true;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.dialog_custom_notification, container, true);
        init();
        return root;
    }

    private void init() {
        numberTxt = root.findViewById(R.id.number);
        Spinner spinnerFirst = root.findViewById(R.id.spinner_1st);
        Spinner spinnerSecond = root.findViewById(R.id.spinner_2nd);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(), R.array.reminder_array_spinner_1st, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFirst.setAdapter(adapter);
        spinnerFirst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItemPosition() == 1) {
                    customNotifText = " hour(s)";
                    hour = true;
                } else if (parent.getSelectedItemPosition() == 2) {
                    customNotifText = " day(s)";
                    day = true;
                } else customNotifText = " minute(s)";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapter = ArrayAdapter.createFromResource(root.getContext(), R.array.reminder_array_spinner_2nd, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSecond.setAdapter(adapter);
        spinnerSecond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItemPosition() == 0)
                    before = true;
                else if (parent.getSelectedItemPosition() == 1)
                    before = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (getArguments().getBoolean("isEventNotification")) {
            spinnerSecond.setEnabled(false);
        }


        root.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!numberTxt.getText().toString().equals("")) {

                    if (day)
                        minutes = Integer.parseInt(numberTxt.getText().toString()) * 24 * 60;
                    else if (hour)
                        minutes = Integer.parseInt(numberTxt.getText().toString()) * 60;
                    else minutes = Integer.parseInt(numberTxt.getText().toString());

                    customNotifTimeTxt = numberTxt.getText().toString() + customNotifText; // setting reminder time-text for notification text
                    if (before) {
                        minutes = -minutes; // setting negative time when before is selected
                        customNotifText = customNotifText + " before";
                    } else customNotifText = customNotifText + " after";

                    customNotifText = numberTxt.getText().toString() + customNotifText; // setting customized reminder text


                    if (getActivity() instanceof EventActivity) {
                        ((EventActivity) getActivity()).setCustomNotifParams(minutes, customNotifText, customNotifTimeTxt);
                    } else if (getActivity() instanceof InterventionsActivity) {
                        ((InterventionsActivity) getActivity()).setCustomNotifParams(minutes, customNotifText, customNotifTimeTxt);
                    }

                    dismiss();
                }
                else{
                    Toast.makeText(getActivity(), "Please, enter the number.", Toast.LENGTH_SHORT).show();
                }
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
