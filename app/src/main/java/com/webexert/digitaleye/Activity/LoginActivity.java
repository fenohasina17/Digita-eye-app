package com.webexert.digitaleye.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.webexert.digitaleye.Helper.PreferenceManager;
import com.webexert.digitaleye.Model.LoginDataModel;
import com.webexert.digitaleye.R;
import com.webexert.digitaleye.Retrofit.ApiClient;
import com.webexert.digitaleye.Retrofit.ApiInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Context context;
    private EditText etEmail,etPassword;
    private MaterialButton btnLogin;
    private TextView tvCreateAccount,tvForgetPassword;
    private ProgressDialog progressDialog;
    private PreferenceManager preferenceManager;
    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialization();

        listener();
        if(preferenceManager.getSignUpStatus()==0)
        {
            if(preferenceManager.getEmail() != null){
                loginRequest(preferenceManager.getEmail(),preferenceManager.getPassword());
            }
        }
        else if(preferenceManager.getSignUpStatus()==1)
        {
         startActivity(new Intent(context,CodeVerification.class));


        }


    }

    private void listener() {
        tvCreateAccount.setOnClickListener(v -> startActivity(new Intent(context,RegisterActivity.class)));

        btnLogin.setOnClickListener(v -> {
            if(!validation())
                return;
            loginRequest(etEmail.getText().toString().trim(),etPassword.getText().toString().trim());
        });

        tvForgetPassword.setOnClickListener(v -> startActivity(new Intent(context,ForgetEmailActivity.class)));
    }

    private void initialization() {
        context = this;
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        preferenceManager = new PreferenceManager(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
    }

    private boolean validation(){

        if(etEmail.getText().toString().trim().equalsIgnoreCase("")){
            etEmail.setError("This field is required");
            etEmail.requestFocus();
            return false;
        }

        if(etPassword.getText().toString().trim().equalsIgnoreCase("")){
            etPassword.setError("This field is required");
            etPassword.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()){
            etEmail.setError("Enter valid email address");
            etEmail.requestFocus();
            return false;
        }
        return true;

    }

    private void loginRequest(String email,String password){

        progressDialog.show();

        ApiClient.getClient()
                .create(ApiInterface.class)
                .login(email,password)
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
                        progressDialog.dismiss();
                        if(response.isSuccessful()){
                            try {
                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                if(!jsonObject.getBoolean("error")){
                                    LoginDataModel loginDataModel = new Gson().fromJson(String.valueOf(jsonObject.getJSONObject("data")),LoginDataModel.class);
                                    preferenceManager.saveAccessToken("Bearer "+loginDataModel.getAccessToken());
                                    preferenceManager.saveEmail(email);
                                    preferenceManager.savePassword(password);
                                    startActivity(new Intent(context,HomeActivity.class)
                                            .putExtra("userData",loginDataModel));
                                    finish();
                                }else
                                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                if(jsonObject.getString("message")
                                        .toLowerCase().equalsIgnoreCase("Sorry! Verify your email First"))
                                {
                                    preferenceManager.setSignUpStatus(1);
                                    startActivity(new Intent(context,CodeVerification.class));
                                    preferenceManager.saveEmail(etEmail.getText().toString().trim());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }else
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}