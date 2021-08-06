package com.webexert.digitaleye.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.webexert.digitaleye.Fragment.BottomSheetPhotoPickerDialog;
import com.webexert.digitaleye.Helper.AddressManager;
import com.webexert.digitaleye.Helper.FileURI;
import com.webexert.digitaleye.Helper.ImageGetterHelper;
import com.webexert.digitaleye.Helper.PreferenceManager;
import com.webexert.digitaleye.Model.LoginDataModel;
import com.webexert.digitaleye.Model.RegisterResponseModel;
import com.webexert.digitaleye.Model.UpdateProfileResponseModel;
import com.webexert.digitaleye.R;
import com.webexert.digitaleye.Retrofit.ApiClient;
import com.webexert.digitaleye.Retrofit.ApiInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity implements BottomSheetPhotoPickerDialog.BottomSheetListener {

    private Context context;
    private final String TAG = "UpdateProfileRes";
    private final int REQUEST_CAMERA_IMAGE = 1;
    private final int REQUEST_GALLERY_IMAGE = 2;
    private final int IMAGE_PERMISSIONS = 3;
    private CircleImageView civProfileImage;
    private ImageView ivUploadImage,ivCancelImage,ivBack;
    private EditText etFirstName,etLastName,etPhone;
    private TextView tvMale,tvFemale;
    private CheckBox cbEmployee,cbParent;
    private Button btnUpdate;
    private Integer gender = 1,isEmployee = 0,isParent = 0;
    private String currentPhotoPath = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        
        initialization();

        listener();

        setData();
    }

    private void setData(){
        LoginDataModel loginDataModel = getIntent().getParcelableExtra("data");

        Glide.with(context)
                .asBitmap()
                .load(new AddressManager().getImageAddress()+loginDataModel.getImage())
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        ivUploadImage.setVisibility(View.GONE);
                        ivCancelImage.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(civProfileImage);

        etFirstName.setText(loginDataModel.getFirstName());
        etLastName.setText(loginDataModel.getLastName());
        etPhone.setText(loginDataModel.getPhone().toString());

        gender = loginDataModel.getGender();
        if(gender == 1)
            changeGenderBackground(tvMale,tvFemale);
        else
            changeGenderBackground(tvFemale,tvMale);

        isParent = loginDataModel.getIsParent();
        isEmployee = loginDataModel.getIsEmployee();

        if(isParent == 1)
            cbParent.setChecked(true);

        if(isEmployee == 1)
            cbEmployee.setChecked(true);

    }

    private void listener() {

        ivBack.setOnClickListener(v -> onBackPressed());

        tvMale.setOnClickListener(v -> {
            gender = 1;
            changeGenderBackground(tvMale,tvFemale);
        });

        tvFemale.setOnClickListener(v -> {
            gender = 0;
            changeGenderBackground(tvFemale,tvMale);
        });

        ivUploadImage.setOnClickListener(v -> {
            if ( ContextCompat.checkSelfPermission( context, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( context, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE  },
                        IMAGE_PERMISSIONS );
            }else {
                new BottomSheetPhotoPickerDialog(this).show(getSupportFragmentManager().beginTransaction(),"image");
            }
        });

        ivCancelImage.setOnClickListener(v -> {
            ivCancelImage.setVisibility(View.GONE);
            civProfileImage.setImageDrawable(null);
            ivUploadImage.setVisibility(View.VISIBLE);
        });

        cbParent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
                isParent = 1;
            else
                isParent = 0;
        });

        cbEmployee.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
                isEmployee = 1;
            else
                isEmployee = 0;
        });

        btnUpdate.setOnClickListener(v -> {
            if(!validation())
                return;

            updateProfileRequest();
        });

    }

    private void changeGenderBackground(TextView selected,TextView notSelected){
        selected.setTextColor(getResources().getColor(R.color.purple));
        selected.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.selected_gender_bg,null));
        notSelected.setTextColor(getResources().getColor(R.color.white));
        notSelected.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.not_selected_gender_bg,null));
    }

    private void initialization() {
        context = this;
        ivBack = findViewById(R.id.ivBack);
        civProfileImage = findViewById(R.id.civProfileImage);
        ivUploadImage = findViewById(R.id.ivUploadImage);
        ivCancelImage = findViewById(R.id.ivCancelImage);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        tvMale = findViewById(R.id.tvMale);
        tvFemale = findViewById(R.id.tvFemale);
        cbEmployee = findViewById(R.id.cbEmployee);
        cbParent = findViewById(R.id.cbParent);
        btnUpdate = findViewById(R.id.btnUpdate);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
    }

    @Override
    public void OnCameraClickListener(View view) {
       // currentPhotoPath = new ImageGetterHelper(context).takeCameraImage(UpdateProfileActivity.this,REQUEST_CAMERA_IMAGE);

        ImagePicker.with(UpdateProfileActivity.this)
                .crop()
                .cameraOnly()
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start(REQUEST_CAMERA_IMAGE);
    }

    @Override
    public void OnGalleryClickListener(View view) {
       // new ImageGetterHelper(context).pickGalleryImage(UpdateProfileActivity.this,REQUEST_GALLERY_IMAGE);
        ImagePicker.with(UpdateProfileActivity.this)
                .crop()
                .galleryOnly()
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start(REQUEST_GALLERY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            switch (requestCode){

                case REQUEST_CAMERA_IMAGE:

                    currentPhotoPath = FileURI.getPathFromUri(context,data.getData());
                    Log.d("PhotoPath", "onActivityResult: "+currentPhotoPath);
                    Glide.with(context)
                            .asBitmap()
                            .load(currentPhotoPath)
                            .into(civProfileImage);
                    ivUploadImage.setVisibility(View.GONE);
                    ivCancelImage.setVisibility(View.VISIBLE);
                    break;

                case REQUEST_GALLERY_IMAGE:

                    assert data != null;
                    currentPhotoPath = FileURI.getPathFromUri(context,data.getData());
                    Log.d("PhotoPath", "onActivityResult: "+currentPhotoPath);
                    Glide.with(context)
                            .asBitmap()
                            .load(currentPhotoPath)
                            .into(civProfileImage);
                    ivUploadImage.setVisibility(View.GONE);
                    ivCancelImage.setVisibility(View.VISIBLE);
                    break;

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMAGE_PERMISSIONS) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new BottomSheetPhotoPickerDialog(this).show(getSupportFragmentManager().beginTransaction(),"image");
            } else {
                Toast.makeText(context, "Need Permissions for this Action", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean validation(){

        if(civProfileImage.getDrawable() == null){
            Toast.makeText(context, "Upload Image", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(etFirstName.getText().toString().trim().equalsIgnoreCase("")){
            etFirstName.setError("This field is required");
            etFirstName.requestFocus();
            return false;
        }

        if(etLastName.getText().toString().trim().equalsIgnoreCase("")){
            etLastName.setError("This field is required");
            etLastName.requestFocus();
            return false;
        }

        if(etPhone.getText().toString().trim().equalsIgnoreCase("")){
            etPhone.setError("This field is required");
            etPhone.requestFocus();
            return false;
        }

        return true;
    }


    private void updateProfileRequest(){

        progressDialog.show();
        MultipartBody.Part imageBody = null;
        if(currentPhotoPath != null){
            imageBody = MultipartBody.Part.createFormData("image", new File(currentPhotoPath).getName(),
                    RequestBody.create(new File(currentPhotoPath), MediaType.parse("multipart/form-data")));
        }
        RequestBody nameBody =  RequestBody.create(etFirstName.getText().toString().trim()+" "+etLastName.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody firstNameBody =  RequestBody.create(etFirstName.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody lastNameBody =  RequestBody.create(etLastName.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody phoneBody =  RequestBody.create(etPhone.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody genderBody =  RequestBody.create(String.valueOf(gender),
                MediaType.parse("multipart/form-data"));
        RequestBody isParentBody =  RequestBody.create(String.valueOf(isParent),
                MediaType.parse("multipart/form-data"));
        RequestBody isEmployeeBody =  RequestBody.create(String.valueOf(isEmployee),
                MediaType.parse("multipart/form-data"));

        ApiClient.getClient()
                .create(ApiInterface.class)
                .updateProfile(imageBody,
                        nameBody,
                        firstNameBody,
                        lastNameBody,
                        phoneBody,
                        isEmployeeBody,
                        isParentBody,
                        genderBody,
                        new PreferenceManager(context).getAccessToken())
                .enqueue(new Callback<UpdateProfileResponseModel>() {
                    @Override
                    public void onResponse(@NotNull Call<UpdateProfileResponseModel> call, @NotNull Response<UpdateProfileResponseModel> response) {
                        progressDialog.dismiss();
                        if(response.isSuccessful())
                        {
                            assert response.body() != null;
                            if(!response.body().getError()){
                                setResult(RESULT_OK,new Intent()
                                        .putExtra("data",response.body().getData()));
                                finish();
                            }
                            else
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else
                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NotNull Call<UpdateProfileResponseModel> call, @NotNull Throwable t) {

                    }
                });

    }


}