package com.noteapp;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.noteapp.Data.DatabaseHelper;
import com.noteapp.Model.User;

public class Sign_up extends AppCompatActivity {

    private TextInputEditText email, pass, rePass;
    private TextInputLayout emailInput, passInput, rePassInput;
    private FloatingActionButton signIn;
    private ImageView back;
    View focusView = null;
    boolean cancel = false;
    private String getEmail, getPwd, getRePass;
    private DatabaseHelper databaseHelper;
    private RelativeLayout sign_up_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        databaseHelper = new DatabaseHelper(this);
        init();

    }

    private void init() {
        bindResources();
    }

    private void bindResources() {
        sign_up_parent = findViewById(R.id.sign_up_parent);
        emailInput = findViewById(R.id.text_email);
        passInput = findViewById(R.id.text_pass);
        rePass = findViewById(R.id.edit_re_pass);
        email = findViewById(R.id.edit_email);
        pass = findViewById(R.id.edit_pass);
        rePassInput = findViewById(R.id.text_re_pass);
        signIn = findViewById(R.id.btn_sign_in);
        back = findViewById(R.id.back);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    hideKeyBoard();

                    //check if entered email exists in database
                    if (!databaseHelper.checkUser(getEmail)) {
                        //add user to database
                        databaseHelper.addUser(new User(getEmail, getPwd));
                        registerUserSucess();
                    } else {
                        registerUserFailed();
                    }

                } else {
                    focusView.requestFocus();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        rePassInput.setError(null);

        getEmail = email.getText().toString().trim();
        getPwd = pass.getText().toString().trim();
        getRePass = rePass.getText().toString().trim();

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
        } else if (TextUtils.isEmpty(getRePass)) {
            rePassInput.setError(getString(R.string.enter_pass));
            focusView = rePass;
            cancel = true;
            return false;
        } else if (getPwd.length() < 6) {
            passInput.setError(getString(R.string.invalid_pass));
            focusView = pass;
            cancel = true;
            return false;
        } else if (getRePass.length() < 6) {
            rePassInput.setError(getString(R.string.invalid_pass));
            focusView = rePass;
            cancel = true;
            return false;
        } else if (!getRePass.equals(getPwd)) {
            rePassInput.setError(getString(R.string.pass_not_match));
            focusView = rePassInput;
            cancel = true;
            return false;
        } else {
            return true;
        }
    }

    private void registerUserSucess() {
        Snackbar.make(sign_up_parent, R.string.register_sucess, Snackbar.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, 1000);

    }

    private void registerUserFailed() {
        Snackbar.make(sign_up_parent, R.string.register_failed, Snackbar.LENGTH_LONG).show();

    }


}
