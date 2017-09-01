package com.lancius.palle2patnam.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lancius.palle2patnam.R;
import com.lancius.palle2patnam.utils.JsonParser;
import com.lancius.palle2patnam.utils.SessionManager;
import com.lancius.palle2patnam.utils.WebServices;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lancius.palle2patnam.R.id.login_button_login;

/**
 * Created by lancius on 7/3/2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText usernameInput, newPasswordInput, confirmPasswordInput;
    TextView forgot_uname_pass;
    ProgressBar pBar;
    ProgressDialog pdialog;

    public static JsonParser jsonParser = new JsonParser();

    String username, newpassword, confirmPassword, message;
    static final String TAG_SUCCESS = "success";
    int success;

    RelativeLayout password_layout;
    SessionManager session;
    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
    private Pattern pattern;
    private Matcher matcher;
    Button submit;
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());

        usernameInput = (EditText) findViewById(R.id.login_input_mobile);

        newPasswordInput = (EditText) findViewById(R.id.login_input_new_password);
        confirmPasswordInput = (EditText) findViewById(R.id.login_input_conform_password);
        pattern = Pattern.compile(PASSWORD_PATTERN);
        submit = (Button) findViewById(login_button_login);
        password_layout = (RelativeLayout) findViewById(R.id.password_relative_layout);

        forgot_uname_pass = (TextView) findViewById(R.id.login_text_forgot_password);
        forgot_uname_pass.setPaintFlags(forgot_uname_pass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        usernameInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() == 10) {
                    username = usernameInput.getText().toString();
                    new IsMobileRegistered().execute();
                } else {
                    password_layout.setVisibility(View.GONE);
                }

            }
        });

        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        Submit(view);
        Submit(view);

    }

    public void Submit(View v) {
        newpassword = newPasswordInput.getText().toString();
        confirmPassword = confirmPasswordInput.getText().toString();
        if (newpassword.equals(confirmPassword)) {
            if (validate(newpassword))
                new AttemptSetPassword().execute();
            else {
                Snackbar.make(v, "Password must be 6 characters including 1 Uppercase letter, 1 Lowercase letter, 1 Digit and 1 Special Character(@#$%)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } else {
            Snackbar.make(v, "New Password and Confirm Password should be same.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }


    class IsMobileRegistered extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pBar = (ProgressBar) findViewById(R.id.login_mobile_progress_bar);
            pBar.setIndeterminate(false);
            pBar.setVisibility(View.VISIBLE);
        }
        //application creation and passing the params to server

        @Override
        protected String doInBackground(String... arguments) {
            //generating parameters to send
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("mobile", username));

            //getting jason object to post the arguments
            JSONObject json = null;
            try {
                try {
                    json = jsonParser.makeHttpRequest(WebServices.USER_LOGIN, "POST", params);
                }
                //try without catch throws an error
                catch (JSONException e1) {
                    e1.printStackTrace();

                }
                //craeting json response and printing
                Log.d("Jason response", json.toString());

                //checking for success
                try {
                    success = json.getInt(TAG_SUCCESS);
                    if (success == 0) {
                        message = json.getString("message");
                    } else {
                        message = json.getString("message");
                        showToast();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (StringIndexOutOfBoundsException ie) {
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            message = "Server Error Please Try Later";
                            showToast();
                        }
                    });
                }
            }

            return null;

        }

        private void showToast() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        protected void onPostExecute(String s) {
            pBar.setVisibility(View.GONE);
            if (success == 0) {
                password_layout.setVisibility(View.VISIBLE);

            }
        }
    }


    public boolean validate(final String password) {

        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    class AttemptSetPassword extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new ProgressDialog(LoginActivity.this);
            pdialog.setIndeterminate(false);
            pdialog.setCancelable(true);
            pdialog.setMessage("Attempting To Login !!");
            pdialog.show();
        }
        //application creation and passing the params to server

        @Override
        protected String doInBackground(String... arguments) {
            //generating parameters to send
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("mobile", username));
            params.add(new BasicNameValuePair("password", newpassword));

            //getting jason object to post the arguments
            JSONObject json = null;
            try {
                try {
                    json = jsonParser.makeHttpRequest(WebServices.USER_LOGIN, "POST", params);
                }
                //try without catch throws an error
                catch (JSONException e1) {
                    e1.printStackTrace();

                }
                //craeting json response and printing
                Log.d("Jason response", json.toString());

                //checking for success
                try {
                    success = json.getInt(TAG_SUCCESS);
                    if (success == 0) {
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                        message = json.getString("message");
                    } else {
                        message = json.getString("message");
                        showToast();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (StringIndexOutOfBoundsException ie) {
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            message = "Server Error Please Try Later";
                            showToast();
                        }
                    });
                }
            }

            return null;

        }

        private void showToast() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        protected void onPostExecute(String s) {
            pdialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}



