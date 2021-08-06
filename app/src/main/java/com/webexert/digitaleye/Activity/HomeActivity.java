package com.webexert.digitaleye.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.webexert.digitaleye.Fragment.HomeFragment;
import com.webexert.digitaleye.Fragment.PolicyFragment;
import com.webexert.digitaleye.Fragment.ProfileFragment;
import com.webexert.digitaleye.Helper.AddressManager;
import com.webexert.digitaleye.Helper.PreferenceManager;
import com.webexert.digitaleye.Model.LoginDataModel;
import com.webexert.digitaleye.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements ProfileFragment.ProfileInterface {

    private Context context;
    //private final String TAG = "HomeResponse";
    private BottomNavigationView bottomNavigationView;
    private CircleImageView civProfileImage;
    private TextView tvUserName;
    private LinearLayout lvLogout;
    private Fragment profileFragment,policyFragment,userFragment,currentFragment;
    public LoginDataModel loginDataModel;
    private PreferenceManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialization();

        listener();


        loginDataModel = getIntent().getExtras().getParcelable("userData");
        setProfile(new AddressManager().getImageAddress()+loginDataModel.getImage(),
                loginDataModel.getName());

        if (userFragment == null)
            userFragment = new HomeFragment();
        setupFragment(userFragment);
    }


    public void setProfile(String image,String name){

        Glide.with(context)
                .asBitmap()
                .load(image)
                .into(civProfileImage);

        tvUserName.setText(name);
    }

    private void listener() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.menuUser){
                if (userFragment == null){
                    userFragment = new HomeFragment();
                    addFragment(currentFragment,userFragment);
                }else {
                    replaceFragment(currentFragment,userFragment);
                }
                return true;
            }else if(item.getItemId() == R.id.menuProfile){
                if (profileFragment == null){
                    profileFragment = new ProfileFragment(this);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("userData",loginDataModel);
                    profileFragment.setArguments(bundle);
                    addFragment(currentFragment,profileFragment);
                }else {
                    replaceFragment(currentFragment,profileFragment);
                }
                return true;
            }else if(item.getItemId() == R.id.menuPolicy){
                if (policyFragment == null){
                    policyFragment = new PolicyFragment();
                    addFragment(currentFragment,policyFragment);
                }else {
                    replaceFragment(currentFragment,policyFragment);
                }
                return true;
            }
            return false;
        });

        lvLogout.setOnClickListener(v -> logoutDialog());

    }


    private void logoutDialog(){
        new AlertDialog.Builder(context)
                .setTitle("Alert...!")
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    dialog.dismiss();
                    PreferenceManager preferenceManager = new PreferenceManager(this);
                    preferenceManager.saveEmail(null);
                    preferenceManager.savePassword(null);
                    startActivity(new Intent(context,LoginActivity.class));
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }



    private void initialization() {

        context = this;
        manager=new PreferenceManager(context);
        Toast.makeText(context, ""+manager.getEmail(), Toast.LENGTH_SHORT).show();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        civProfileImage = findViewById(R.id.civProfileImage);
        tvUserName = findViewById(R.id.tvUserName);
        lvLogout = findViewById(R.id.lvLogout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void OnUpdateProfileListener(LoginDataModel loginDataModel) {
        this.loginDataModel = loginDataModel;
        setProfile(new AddressManager().getImageAddress()+loginDataModel.getImage(),
                loginDataModel.getName());
    }


    private void setupFragment(Fragment add){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container,add)
                .addToBackStack(null)
                .commit();
        currentFragment = add;
    }

    private void addFragment(Fragment hide,Fragment add){
        getSupportFragmentManager().beginTransaction()
                .hide(hide)
                .add(R.id.container,add)
                .commit();
        currentFragment = add;
    }

    private void replaceFragment(Fragment hide,Fragment show){
        getSupportFragmentManager().beginTransaction()
                .hide(hide)
                .show(show)
                .commit();
        currentFragment = show;
    }


}