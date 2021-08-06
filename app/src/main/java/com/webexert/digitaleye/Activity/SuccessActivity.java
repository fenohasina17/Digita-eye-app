package com.webexert.digitaleye.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.webexert.digitaleye.Helper.PreferenceManager;
import com.webexert.digitaleye.R;

public class SuccessActivity extends AppCompatActivity {

    private PreferenceManager manager;
    private MaterialButton mBtnLogin;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        initView();

        manager.ClearAll();
        manager.setSignUpStatus(0);
        Intent intent=getIntent();

//        String msg=intent.getStringExtra("msg");
//        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();



        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(context,LoginActivity.class));
            }
        });
    }

    private void initView() {
        context=this;
        mBtnLogin = (MaterialButton) findViewById(R.id.btnLogin);
        manager=new PreferenceManager(context);
    }
}