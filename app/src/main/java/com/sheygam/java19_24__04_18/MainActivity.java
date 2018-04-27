package com.sheygam.java19_24__04_18;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginBtn, regBtn;
    private EditText inputEmail, inputPassword;
    private ProgressBar myProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginBtn = findViewById(R.id.login_btn);
        regBtn = findViewById(R.id.reg_btn);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        myProgress = findViewById(R.id.my_progress);

        loginBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);
    }

    private void showProgress(boolean isShow){
        myProgress.setVisibility(isShow?View.VISIBLE:View.INVISIBLE);
        inputPassword.setEnabled(!isShow);
        inputEmail.setEnabled(!isShow);
        loginBtn.setEnabled(!isShow);
        regBtn.setEnabled(!isShow);
    }

    private boolean isEmailValid(@NonNull String email){
        return !email.isEmpty() && email.contains("@");
    }

    private boolean isPasswordValid(@NonNull String password){
        return  password.length()>=4;
    }

    private void emailValidError(String error){
        inputEmail.setError(error);
    }

    private void passwordValidError(String error){
        inputPassword.setError(error);
    }

    private void showError(String error){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(error)
                .setPositiveButton("Ok",null)
                .setCancelable(false)
                .create()
                .show();
    }

    private void showNextView(){
        Toast.makeText(this, "Request success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.reg_btn){
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();

            if(!isEmailValid(email)){
                emailValidError("Email without @");
                return;
            }

            if(!isPasswordValid(password)){
                passwordValidError("Password min 4 symbols");
                return;
            }

            new RegTask(email,password).execute();
        }else if(v.getId() == R.id.login_btn){
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();

            if(!isEmailValid(email)){
                emailValidError("Email without @");
                return;
            }

            if(!isPasswordValid(password)){
                passwordValidError("Password min 4 symbols");
                return;
            }

            new LoginTask(email,password).execute();
        }
    }

    class RegTask extends AsyncTask<Void,Void,String>{

        private String email, password;
        private boolean isSuccess = true;

        public RegTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "Registration ok!";

            try {
                String token = HttpProvider.getInstance().registration(email,password);
                //Todo save token
                Log.d("MY_TAG", "doInBackground: token: " + token);
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
                result = "Connection error! Check your internet!";
            }catch (Exception e) {
                e.printStackTrace();
                isSuccess = false;
                result = e.getMessage();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            showProgress(false);
            if(isSuccess){
                showNextView();
            }else{
                showError(s);
            }
        }
    }


    class LoginTask extends AsyncTask<Void,Void,String>{

        private String email, password;
        private boolean isSuccess = true;

        public LoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "Login ok!";

            try {
                String token = HttpProvider.getInstance().login(email,password);
                //Todo save token
                Log.d("MY_TAG", "doInBackground: token: " + token);
            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
                result = "Connection error! Check your internet!";
            }catch (Exception e) {
                e.printStackTrace();
                isSuccess = false;
                result = e.getMessage();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            showProgress(false);
            if(isSuccess){
                showNextView();
            }else{
                showError(s);
            }
        }
    }
}
