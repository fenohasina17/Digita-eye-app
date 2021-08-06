package com.webexert.digitaleye.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.webexert.digitaleye.Activity.ChangePasswordActivity;
import com.webexert.digitaleye.Activity.UpdateProfileActivity;
import com.webexert.digitaleye.Helper.AddressManager;
import com.webexert.digitaleye.Helper.PreferenceManager;
import com.webexert.digitaleye.Model.LoginDataModel;
import com.webexert.digitaleye.Model.UpdateProfileResponseModel;
import com.webexert.digitaleye.R;
import com.webexert.digitaleye.Retrofit.ApiClient;
import com.webexert.digitaleye.Retrofit.ApiInterface;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private Context context;
    private final Integer CHANGE_PASS_REQUEST = 1;
    private final Integer UPDATE_PROFILE_REQUEST = 2;
    private CircleImageView civUserImage;
    private TextView tvUserName,tvGuardianType;
    private Button btnChangePassword,btnUpdateProfile;
    private LoginDataModel loginDataModel;
    private ProfileInterface profileInterface;
    private PreferenceManager preferenceManager;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public ProfileFragment(ProfileInterface profileInterface) {
        this.profileInterface = profileInterface;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialization(view);

        listener();

        assert getArguments() != null;
        loginDataModel = getArguments().getParcelable("userData");

        setProfile(new AddressManager().getImageAddress()+ loginDataModel.getImage(),
                loginDataModel.getName(),
                loginDataModel.getIsParent(),
                loginDataModel.getIsEmployee());

    }

    @SuppressLint("SetTextI18n")
    private void setProfile(String image,String name,Integer isParent,Integer isEmployee) {
        Glide.with(context)
                .asBitmap()
                .load(image)
                .into(civUserImage);

        tvUserName.setText(name);

        tvGuardianType.setText("");

        if (isParent == 1) {
            tvGuardianType.setText("Parent");
        }

        if (isEmployee == 1) {
            if(tvGuardianType.getText().toString().trim().equalsIgnoreCase(""))
                tvGuardianType.setText("Employee");
            else
                tvGuardianType.setText(tvGuardianType.getText().toString().trim()+" , Employee");
        }
    }

    private void listener() {
        btnChangePassword.setOnClickListener(v -> {
//            startActivityForResult(new Intent(context, ChangePasswordActivity.class), CHANGE_PASS_REQUEST);

            progressDialog.show();
            ApiClient.getClient().create(ApiInterface.class)
                    .resetPassword(preferenceManager.getEmail())
                    .enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {

                            if(response.isSuccessful())
                            {
                                try {
                                    progressDialog.dismiss();
                                    JSONObject object = new JSONObject(new Gson().toJson(response.body()));
                                    boolean errorFlag = object.getBoolean("error");
                                    if(!errorFlag)
                                    {
                                        Toast.makeText(context, "check your emil for reset password"  , Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(context, "Error "  , Toast.LENGTH_LONG).show();
                                    }


                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(context, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {

                        }
                    });
        });

        btnUpdateProfile.setOnClickListener(v -> {
            startActivityForResult(new Intent(context, UpdateProfileActivity.class)
                    .putExtra("data", loginDataModel), UPDATE_PROFILE_REQUEST);



        });
    }

    private void initialization(View view) {
        context = requireContext();
        civUserImage = view.findViewById(R.id.civUserImage);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvGuardianType = view.findViewById(R.id.tvGuardianType);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        preferenceManager=new PreferenceManager(context);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == CHANGE_PASS_REQUEST && resultCode == Activity.RESULT_OK){
            Toast.makeText(context, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
        }else if(requestCode == UPDATE_PROFILE_REQUEST && resultCode == Activity.RESULT_OK){
            assert intent != null;
            UpdateProfileResponseModel.Data  data = intent.getParcelableExtra("data");
            loginDataModel.setImage(data.getImage());
            loginDataModel.setName(data.getName());
            loginDataModel.setIsParent(Integer.parseInt(data.getIsParent()));
            loginDataModel.setIsEmployee(Integer.parseInt(data.getIsEmployee()));
            setProfile(new AddressManager().getImageAddress()+ loginDataModel.getImage(),
                    loginDataModel.getName(),
                    loginDataModel.getIsParent(),
                    loginDataModel.getIsEmployee());
            profileInterface.OnUpdateProfileListener(loginDataModel);
        }
    }

    public interface ProfileInterface{

        void OnUpdateProfileListener(LoginDataModel loginDataModel);

    }

}