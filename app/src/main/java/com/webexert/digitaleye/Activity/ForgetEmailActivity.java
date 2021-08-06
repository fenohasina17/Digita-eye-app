package com.webexert.digitaleye.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.webexert.digitaleye.R;
import com.webexert.digitaleye.Retrofit.ApiClient;
import com.webexert.digitaleye.Retrofit.ApiInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetEmailActivity extends AppCompatActivity {

    private Context context;
    private EditText etEmail;
    private ImageView ivBack;
    private MaterialButton btnSendCode;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_email);

        initialization();

        listener();
    }

    private void listener() {

        ivBack.setOnClickListener(v -> onBackPressed());

        btnSendCode.setOnClickListener(v -> {
            if(!validation())
                return;

            sendEmailRequest();
        });
    }

    private void initialization() {
        context = this;
        etEmail = findViewById(R.id.etEmail);
        ivBack = findViewById(R.id.ivBack);
        btnSendCode = findViewById(R.id.btnSendCode);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
    }

    private Boolean validation(){

        if(etEmail.getText().toString().trim().equalsIgnoreCase("")){
            etEmail.setError("This field is required");
            etEmail.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString().trim()).matches()){
            etEmail.setError("Enter valid email address");
            etEmail.requestFocus();
            return false;
        }

        return true;
    }

    private void sendEmailRequest(){
        progressDialog.show();
        ApiClient.getClient()
                .create(ApiInterface.class)
                .sendEmail(etEmail.getText().toString().trim())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
                        progressDialog.dismiss();
                        if(response.isSuccessful()){
                            try {
                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));

                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}