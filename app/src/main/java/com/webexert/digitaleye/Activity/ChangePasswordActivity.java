package com.webexert.digitaleye.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.webexert.digitaleye.Helper.PreferenceManager;
import com.webexert.digitaleye.R;
import com.webexert.digitaleye.Retrofit.ApiClient;
import com.webexert.digitaleye.Retrofit.ApiInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private Context context;
    private final String TAG = "ChangePassResponse";
    private EditText etCurrentPassword,etNewPassword,etConfirmPassword;
    private Button btnUpdate;
    private ImageView ivBack;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initialization();

        listener();

    }

    private void listener() {
        btnUpdate.setOnClickListener(v -> {
            if(!validation())
                return;

            changePasswordRequest();
        });

        ivBack.setOnClickListener(v -> onBackPressed());
    }

    private void initialization() {
        context = this;
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        ivBack = findViewById(R.id.ivBack);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
    }

    private boolean validation(){

        if(etCurrentPassword.getText().toString().trim().equalsIgnoreCase("")){
            etCurrentPassword.setError("This field is required");
            etCurrentPassword.requestFocus();
            return false;
        }


        if(etNewPassword.getText().toString().trim().equalsIgnoreCase("")){
            etNewPassword.setError("This field is required");
            etNewPassword.requestFocus();
            return false;
        }

        if(etNewPassword.getText().toString().trim().length() < 6){
            etNewPassword.setError("Password must contain 6 characters");
            etNewPassword.requestFocus();
            return false;
        }

        if(etConfirmPassword.getText().toString().trim().equalsIgnoreCase("")){
            etConfirmPassword.setError("This field is required");
            etConfirmPassword.requestFocus();
            return false;
        }

        if(!etNewPassword.getText().toString().trim().equalsIgnoreCase(etConfirmPassword.getText().toString().trim())){
            etConfirmPassword.setError("Password not matched");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void changePasswordRequest(){

        progressDialog.show();

        ApiClient.getClient()
                .create(ApiInterface.class)
                .changePassword(etCurrentPassword.getText().toString().trim(),
                        etNewPassword.getText().toString().trim(),
                        etConfirmPassword.getText().toString().trim(),
                        new PreferenceManager(context).getAccessToken())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
                        progressDialog.dismiss();
                        if(response.isSuccessful()){
                            try {
                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                if(!jsonObject.getBoolean("error")){
                                    setResult(RESULT_OK,new Intent());
                                    finish();
                                }else
                                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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