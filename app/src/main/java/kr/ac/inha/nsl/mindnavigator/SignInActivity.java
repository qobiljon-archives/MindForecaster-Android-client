package kr.ac.inha.nsl.mindnavigator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        init();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingPanel.setVisibility(View.GONE);
    }

    // region Variables
    private EditText userLogin;
    private EditText userPassword;
    private RelativeLayout loadingPanel;

    static SharedPreferences loginPrefs = null;
    static final String username = "username", password = "password";
    // endregion

    private void init() {
        // region Initialize UI Variables
        userLogin = findViewById(R.id.txt_login);
        userPassword = findViewById(R.id.txt_password);
        loadingPanel = findViewById(R.id.loadingPanel);
        // endregion

        if (loginPrefs == null)
            loginPrefs = getSharedPreferences("UserLogin", 0);

        if (loginPrefs.contains(SignInActivity.username) && loginPrefs.contains(SignInActivity.password)) {
            loadingPanel.setVisibility(View.VISIBLE);
            signIn(loginPrefs.getString(SignInActivity.username, null), loginPrefs.getString(SignInActivity.password, null));
        } else Toast.makeText(this, "No log in yet", Toast.LENGTH_SHORT).show();
    }

    public void signInClick(View view) {
        signIn(userLogin.getText().toString(), userPassword.getText().toString());
    }

    public void signUpClick(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void signIn(String username, String password) {
        loadingPanel.setVisibility(View.VISIBLE);

        Tools.execute(new MyRunnable(
                getString(R.string.url_user_login),
                username,
                password
        ) {
            @Override
            public void run() {
                String url = (String) args[0];
                String username = (String) args[1];
                String password = (String) args[2];

                try {
                    JSONObject body = new JSONObject();
                    body.put("username", username);
                    body.put("password", password);

                    JSONObject json = new JSONObject(Tools.post(url, body));

                    switch (json.getInt("result")) {
                        case Tools.RES_OK:
                            runOnUiThread(new MyRunnable(args) {
                                @Override
                                public void run() {
                                    String username = (String) args[1];
                                    String password = (String) args[2];

                                    SharedPreferences.Editor editor = SignInActivity.loginPrefs.edit();
                                    editor.putString(SignInActivity.username, username);
                                    editor.putString(SignInActivity.password, password);
                                    editor.apply();

                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                    finish();
                                }
                            });
                            break;
                        case Tools.RES_FAIL:
                            Thread.sleep(2000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignInActivity.this, "Failed to sign in.", Toast.LENGTH_SHORT).show();
                                    loadingPanel.setVisibility(View.GONE);
                                }
                            });
                            break;
                        case Tools.RES_SRV_ERR:
                            Thread.sleep(2000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignInActivity.this, "Failed to sign in. (SERVER SIDE ERROR)", Toast.LENGTH_SHORT).show();
                                    loadingPanel.setVisibility(View.GONE);
                                }
                            });
                            break;
                        default:
                            break;
                    }
                } catch (JSONException | InterruptedException | IOException e) {
                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignInActivity.this, "Failed to sign in.", Toast.LENGTH_SHORT).show();
                            loadingPanel.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }
}