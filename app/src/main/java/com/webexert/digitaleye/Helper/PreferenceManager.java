package com.webexert.digitaleye.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.webexert.digitaleye.R;


public class PreferenceManager {

    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public PreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.preference), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAccessToken(String accessToken){
        editor.putString(context.getResources().getString(R.string.access_token),accessToken);
        editor.apply();
    }

    public void setSignUpStatus(int status)
    {
        editor.putInt(context.getString(R.string.signup_status),status);
        editor.apply();
    }


    public int getSignUpStatus()
    {
        return sharedPreferences.getInt(context.getResources().getString(R.string.signup_status),0);
    }


    public String getAccessToken(){
        return sharedPreferences.getString(context.getResources().getString(R.string.access_token),null);
    }

    public void saveEmail(String email){
        editor.putString(context.getResources().getString(R.string.email),email);
        editor.apply();
    }

    public String getEmail(){
        return sharedPreferences.getString(context.getResources().getString(R.string.email),null);
    }

    public void savePassword(String password){
        editor.putString(context.getResources().getString(R.string.password),password);
        editor.apply();
    }

    public String getPassword(){
        return sharedPreferences.getString(context.getResources().getString(R.string.password),null);
    }

    public void ClearAll(){

        editor.clear();
    }

}
