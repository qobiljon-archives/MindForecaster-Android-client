package kr.ac.inha.nsl.mindnavigator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class InterventionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interventions);
        init();
    }

    //region Variables
    TextView interv_text;
    View interv_choice;
    ViewGroup interv_list;

    Button[] tabButtons;
    //endregion

    private void init() {
        interv_choice = findViewById(R.id.intervention_choice);
        interv_text = findViewById(R.id.intervention_text);
        interv_list = findViewById(R.id.interventions_list);
        tabButtons = new Button[]{
                findViewById(R.id.button_self_intervention),
                findViewById(R.id.button_systems_intervention),
                findViewById(R.id.button_peer_interventions)
        };

        interv_text.setVisibility(View.GONE);
        interv_choice.setVisibility(View.GONE);
        tabButtons[0].callOnClick();
    }

    public void tabClicked(View view) {
        // Clear out visibility and previously set button color
        interv_text.setVisibility(View.GONE);
        interv_choice.setVisibility(View.GONE);
        for (Button button : tabButtons)
            button.setBackgroundResource(R.drawable.bg_interv_method_unchecked_view);

        // Act upon the click event
        switch (view.getId()) {
            case R.id.button_self_intervention:
                interv_text.setVisibility(View.VISIBLE);
                break;
            case R.id.button_systems_intervention:
                interv_choice.setVisibility(View.VISIBLE);
                interv_list.removeViews(1, interv_list.getChildCount() - 1);
                break;
            case R.id.button_peer_interventions:
                interv_choice.setVisibility(View.VISIBLE);
                interv_list.removeViews(1, interv_list.getChildCount() - 1);
                break;
            default:
                break;
        }
    }
}
