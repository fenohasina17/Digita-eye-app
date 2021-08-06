package com.webexert.digitaleye.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.webexert.digitaleye.Helper.PreferenceManager;
import com.webexert.digitaleye.R;
import com.webexert.digitaleye.Retrofit.ApiClient;
import com.webexert.digitaleye.Retrofit.ApiInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodeVerification extends AppCompatActivity {

    private Animation wooble;
    private final String TAG = "HELLO";
    private PinView mFirstPinView;
    private MaterialButton mBtnVerification;
    private Context context;
    private ProgressDialog progressDialog;
    String code = null;
    String email = null;
    private TextView mTvNotYou;
    private PreferenceManager preferenceManager;
    private TextView mUserEmail;
    private TextView mTvResendCode;
    private TextView mTvCounter;
    boolean isResend = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);
        context=this;
        preferenceManager=new PreferenceManager(context);
        email = preferenceManager.getEmail();

        initView();

        listners();
        countDownTimer();


    }

    private void listners() {
        mBtnVerification.setOnClickListener(v -> {

            progressDialog.show();
            code = Objects.requireNonNull(mFirstPinView.getText()).toString();
            Toast.makeText(context, "" + code, Toast.LENGTH_LONG).show();

            ApiClient.getClient().create(ApiInterface.class).ConfirmCode(email, code)
                    .enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject object = new JSONObject(new Gson().toJson(response.body()));
                                    boolean errorFlag = object.getBoolean("error");

                                    if (!errorFlag)
                                    {
                                        startActivity(new Intent(context, SuccessActivity.class));
                                    }
                                    if (errorFlag) {
                                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.EFFECT_HEAVY_CLICK));
                                        } else {
                                            //deprecated in API 26
                                            v.vibrate(500);
                                        }
                                        mFirstPinView.setText("");
                                        mFirstPinView.startAnimation(wooble);
                                        Toast.makeText(CodeVerification.this, "wrong code", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();

                                    Toast.makeText(context, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(context, "Not Successfull", Toast.LENGTH_SHORT).show();
                            }

                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(@NotNull Call<Object> call, @NotNull Throwable t) {

                            Toast.makeText(context, "" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        });
        mTvNotYou.setOnClickListener(v -> {
            preferenceManager.ClearAll();
            preferenceManager.setSignUpStatus(0);
            startActivity(new Intent(context, LoginActivity.class));
        });
        mTvResendCode.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if(isResend)
                {
                    resendCode();
                    isResend=false;
                    mTvResendCode.setTextColor(getColor(R.color.black));
                    countDownTimer();
                }
            }
        });
    }

    private void resendCode() {

        progressDialog.show();
        ApiClient.getClient().create(ApiInterface.class).resendVerificationCode(email)
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if (response.isSuccessful()) {

                            try {
                                JSONObject object = new JSONObject(new Gson().toJson(response.body()));
                                boolean errorFlag = object.getBoolean("error");
                                Toast.makeText(context, ""+errorFlag, Toast.LENGTH_SHORT).show();
                                if (!errorFlag) {
                                    Toast.makeText(context, "resend send new code successfully", Toast.LENGTH_SHORT).show();
                                }
                                if (errorFlag)
                                {
                                    Toast.makeText(context, "Error sending code", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {


                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.EFFECT_HEAVY_CLICK));
                        }
                        Toast.makeText(context, ""+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }
                });
    }


    private boolean checkResponse(Response<Object> response) {
        if (response.body() != null) {
            return response.isSuccessful();
        }
        return false;
    }

    private void initView() {



        context = this;
        mFirstPinView = findViewById(R.id.firstPinView);
        mBtnVerification = findViewById(R.id.btnLogin);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        wooble = AnimationUtils.loadAnimation(context, R.anim.wooble);

        mTvNotYou = findViewById(R.id.tvNotYou);

        preferenceManager = new PreferenceManager(context);
        mUserEmail = (TextView) findViewById(R.id.userEmail);
        mUserEmail.setText(email);
        mTvResendCode = (TextView) findViewById(R.id.tvResendCode);
        mTvCounter = (TextView) findViewById(R.id.tvCounter);
    }

    private void countDownTimer(){

        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {

                mTvCounter.setText( String.format("00 : "+millisUntilFinished/1000));
            }

            public void onFinish() {
                mTvResendCode.setTextColor(getResources().getColor(R.color.purple));
                isResend = true;
            }

        }.start();
    }



}