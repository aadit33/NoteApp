package com.noteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noteapp.Data.DatabaseHelper;

public class Login extends AppCompatActivity {
    private TextInputEditText email, pass;
    private TextInputLayout emailInput, passInput;
    private FloatingActionButton login;
    private TextView textv_sign_up;
    View focusView = null;
    boolean cancel = false;
    private String getEmail, getPwd;
    private DatabaseHelper databaseHelper;
    private RelativeLayout login_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        databaseHelper = new DatabaseHelper(this);
        init();
    }

    private void init() {
        bindResources();
    }

    private void bindResources() {
        login_parent = findViewById(R.id.login_parent);
        emailInput = findViewById(R.id.text_email);
        passInput = findViewById(R.id.text_pass);
        email = findViewById(R.id.edit_email);
        pass = findViewById(R.id.edit_pass);
        login = findViewById(R.id.btn_login_in);
        textv_sign_up = findViewById(R.id.textv_sign_up);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    hideKeyBoard();
                    if (databaseHelper.checkUser(getEmail, getPwd)) {

                        loginInSucess();
                    } else {
                        failedSucess();

                    }

                } else {
                    focusView.requestFocus();
                }
            }
        });


        textv_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Sign_up.class));
            }
        });
    }


    private void hideKeyBoard() {
        try {
            if (getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void getLoginData() {
        emailInput.setError(null);
        passInput.setError(null);

        getEmail = email.getText().toString().trim();
        getPwd = pass.getText().toString().trim();

    }

    private boolean validate() {

        String EMAIL_REGEX = "^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$";

        getLoginData();
        if (TextUtils.isEmpty(getEmail)) {
            emailInput.setError(getString(R.string.email_field_required));
            focusView = email;
            cancel = true;
            return false;
        } else if (!getEmail.matches(EMAIL_REGEX)) {
            emailInput.setError(getString(R.string.enter_valid_email));
            focusView = email;
            cancel = true;
            return false;
        } else if (TextUtils.isEmpty(getPwd)) {
            passInput.setError(getString(R.string.enter_pass));
            focusView = pass;
            cancel = true;
            return false;
        } else if (getPwd.length() < 6) {
            passInput.setError(getString(R.string.invalid_pass));
            focusView = pass;
            cancel = true;
            cancel = true;
            return false;
        } else {
            return true;
        }
    }

    private void loginInSucess() {
        Snackbar.make(login_parent, R.string.login_sucess, Snackbar.LENGTH_LONG).show();

        String userId = databaseHelper.getUserId(getEmail);
        System.out.println("UserId" + userId);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Login.this);
        prefs.edit().putBoolean("Islogin", true).apply();
        prefs.edit().putString("user_id", userId).apply();
        startActivity(new Intent(Login.this, NoteListActivity.class));
        finish();
    }

    private void failedSucess() {
        Snackbar.make(login_parent, R.string.login_failed, Snackbar.LENGTH_LONG).show();
    }
}

