package com.webexert.digitaleye.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.webexert.digitaleye.Fragment.BottomSheetPhotoPickerDialog;
import com.webexert.digitaleye.Helper.AddressManager;
import com.webexert.digitaleye.Helper.FileURI;
import com.webexert.digitaleye.Helper.PreferenceManager;
import com.webexert.digitaleye.Model.AddUserResponseModel;
import com.webexert.digitaleye.Model.UserDataModel;
import com.webexert.digitaleye.R;
import com.webexert.digitaleye.Retrofit.ApiClient;
import com.webexert.digitaleye.Retrofit.ApiInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserActivity extends AppCompatActivity implements BottomSheetPhotoPickerDialog.BottomSheetListener {

    private Context context;
    private final int REQUEST_CAMERA_IMAGE = 1;
    private final int REQUEST_GALLERY_IMAGE = 2;
    private final int IMAGE_PERMISSIONS = 3;
    private CircleImageView civProfileImage;
    private ImageView ivUploadImage,ivCancelImage,ivBack;
    private EditText etStudentID,etFirstName,etLastName,etEmail,etPhone,etState,etCity,etZip,etAddress,etSchoolName,etEmergencyContact,etEmergencyPhone;
    private TextView tvMale,tvFemale,tvTitle;
    private Button btnAdd;
    private Integer gender = 1;
    private String currentPhotoPath = null;
    private ProgressDialog progressDialog;
    private UserDataModel userDataModel;
    private Boolean isAdd;
    private String studentId;
    private String userDataModelFirstName;
    private String name;
    private String image;
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String state;
    private String city;
    private String zip;
    private String address;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        initialization();

        listener();

        if(getIntent().getStringExtra("type").equals("update")){
            isAdd = false;
            tvTitle.setText("Update User");
            btnAdd.setText("Update");
            userDataModel = getIntent().getParcelableExtra("data");
            setData(new AddressManager().getImageAddress()+userDataModel.getImage(),
                    userDataModel.getStudentId(),
                    userDataModel.getFirstName(),
                    userDataModel.getLastName(),
                    userDataModel.getEmail(),
                    userDataModel.getPhone(),
                    userDataModel.getState(),
                    userDataModel.getCity(),
                    userDataModel.getZip(),
                    userDataModel.getAddress(),
                    userDataModel.getCity(),

                    userDataModel.getSchoolName(),
                    userDataModel.getEmergencyContact(),
                    userDataModel.getEmergencyPhone(),
                    Integer.parseInt(userDataModel.getGender()));
        }else {
            isAdd = true;
            tvTitle.setText("Add User");
            btnAdd.setText("Add");
        }
    }

    private void setData( String s,String studentId, String userDataModelFirstName, String name, String image, String id, String firstName, String lastName, String email, String phone,String address, String state, String city, String zip, Integer gender){
      
        this.studentId = studentId;
        this.userDataModelFirstName = userDataModelFirstName;
        this.name = name;
        this.image = image;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.state = state;
        this.city = city;
        this.zip = zip;

        Glide.with(context)
                .asBitmap()
                .load(image)
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

        etStudentID.setText(id);
        etFirstName.setText(firstName);
        etLastName.setText(lastName);
        etEmail.setText(email);
        etEmail.setVisibility(View.GONE);
        etPhone.setText(phone);
        etAddress.setText(address);
        etState.setText(state);
        etCity.setText(city);
        etZip.setText(zip);
        int schoolName = 0;
        etSchoolName.setText(schoolName);
        int emergencyPhone = 0;
        etEmergencyPhone.setText(emergencyPhone);
        int emergencyContact = 0;
        etEmergencyContact.setText(emergencyContact);

        this.gender = gender;
        if(gender == 1)
            changeGenderBackground(tvMale,tvFemale);
        else
            changeGenderBackground(tvFemale,tvMale);


    }


    private void listener() {

        ivBack.setOnClickListener(v -> onBackPressed());

        btnAdd.setOnClickListener(v -> {
            if(!validation())
              return;

            if(isAdd)
                addUserRequest();
            else
                updateUserRequest();
        });

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




                ActivityCompat.requestPermissions( this,
                        new String[] {  Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE  },
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
        etStudentID = findViewById(R.id.etStudentID);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etState = findViewById(R.id.etState);
        etCity = findViewById(R.id.etCity);
        etZip = findViewById(R.id.etZip);
        etSchoolName = findViewById(R.id.etSchoolName);
        etEmergencyContact = findViewById(R.id.etEmergencyContact);
        etEmergencyPhone = findViewById(R.id.etEmergencyPhone);
        tvMale = findViewById(R.id.tvMale);
        tvFemale = findViewById(R.id.tvFemale);
        tvTitle = findViewById(R.id.tvTitle);
        btnAdd = findViewById(R.id.btnAdd);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");

        PreferenceManager preferenceManager = new PreferenceManager(context);
    }

    private boolean validation(){

        if(civProfileImage.getDrawable() == null){
            Toast.makeText(context, "Upload Image", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(etStudentID.getText().toString().trim().equalsIgnoreCase("")){
            etStudentID.setError("This field is required");
            etStudentID.requestFocus();
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

        if(etPhone.getText().toString().trim().equalsIgnoreCase("")){
            etPhone.setError("This field is required");
            etPhone.requestFocus();
            return false;
        }

        if(etAddress.getText().toString().trim().equalsIgnoreCase("")){
            etAddress.setError("This field is required");
            etAddress.requestFocus();
            return false;
        }

        if(etState.getText().toString().trim().equalsIgnoreCase("")){
            etState.setError("This field is required");
            etState.requestFocus();
            return false;
        }

        if(etCity.getText().toString().trim().equalsIgnoreCase("")){
            etCity.setError("This field is required");
            etCity.requestFocus();
            return false;
        }

        if(etZip.getText().toString().trim().equalsIgnoreCase("")){
            etZip.setError("This field is required");
            etZip.requestFocus();
            return false;
        }

        if(etSchoolName.getText().toString().trim().equalsIgnoreCase("")){
            etSchoolName.setError("This field is required");
            etSchoolName.requestFocus();
            return false;
        }


        if(etEmergencyContact.getText().toString().trim().equalsIgnoreCase("")){
            etEmergencyContact.setError("This field is required");
            etEmergencyContact.requestFocus();
            return false;
        }


        if(etEmergencyPhone.getText().toString().trim().equalsIgnoreCase("")){
            etEmergencyPhone.setError("This field is required");
            etEmergencyPhone.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void OnCameraClickListener(View view) {
       // currentPhotoPath = new ImageGetterHelper(context).takeCameraImage(AddUserActivity.this,REQUEST_CAMERA_IMAGE);

        ImagePicker.with(AddUserActivity.this)
                .crop()
                .cameraOnly()
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start(REQUEST_CAMERA_IMAGE);
    }

    @Override
    public void OnGalleryClickListener(View view) {
      //  new ImageGetterHelper(context).pickGalleryImage(AddUserActivity.this,REQUEST_GALLERY_IMAGE);

        ImagePicker.with(AddUserActivity.this)
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

                    assert data != null;
                    currentPhotoPath = FileURI.getPathFromUri(context,data.getData());
                    Log.d("PhotoPath", "onActivityResult: "+currentPhotoPath);
                    Glide.with(context)
                            .asBitmap()
                            .load(BitmapFactory.decodeFile(currentPhotoPath))
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

    private void addUserRequest(){

        progressDialog.show();

        MultipartBody.Part imageBody = MultipartBody.Part.createFormData("image", new File(currentPhotoPath).getName(),
                RequestBody.create(new File(currentPhotoPath), MediaType.parse("multipart/form-data")));
        RequestBody schoolIDBody =  RequestBody.create(etStudentID.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody nameBody =  RequestBody.create(etFirstName.getText().toString().trim()+" "+etLastName.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody firstNameBody =  RequestBody.create(etFirstName.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody lastNameBody =  RequestBody.create(etLastName.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody emailBody =  RequestBody.create(etEmail.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody phoneBody =  RequestBody.create(etPhone.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody addressBody = RequestBody.create(etAddress.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody stateBody = RequestBody.create(etState.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody cityBody = RequestBody.create(etCity.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody zipBody = RequestBody.create(etZip.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody schoolNameBody =  RequestBody.create(etSchoolName.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody emergencyContactBody =  RequestBody.create(etEmergencyContact.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody emergencyPhoneBody =  RequestBody.create(etEmergencyPhone.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody genderBody =  RequestBody.create(String.valueOf(gender),
                MediaType.parse("multipart/form-data"));

        ApiClient.getClient()
                .create(ApiInterface.class)
                .addUser(imageBody,
                        schoolIDBody,
                        nameBody,
                        firstNameBody,
                        lastNameBody,
                        emailBody,
                        phoneBody,
                        addressBody,
                        stateBody,
                        cityBody,
                        zipBody,
                        schoolNameBody,
                        emergencyContactBody,
                        emergencyPhoneBody,
                        genderBody,
                        new PreferenceManager(context).getAccessToken())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
                        progressDialog.dismiss();
                        if(response.isSuccessful()){
                            try {
                                JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                if(!jsonObject.getBoolean("error")){
                                    UserDataModel userDataModel = new Gson().fromJson(jsonObject.getJSONObject("data").toString(),UserDataModel.class);
                                    setResult(RESULT_OK,new Intent()
                                            .putExtra("data",userDataModel));
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



    private void updateUserRequest(){

        progressDialog.show();

        MultipartBody.Part imageBody = null;
        if(currentPhotoPath != null){
            imageBody = MultipartBody.Part.createFormData("image", new File(currentPhotoPath).getName(),
                    RequestBody.create(new File(currentPhotoPath), MediaType.parse("multipart/form-data")));
        }
        RequestBody schoolIDBody =  RequestBody.create(etStudentID.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody nameBody =  RequestBody.create(etFirstName.getText().toString().trim()+" "+etLastName.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody firstNameBody =  RequestBody.create(etFirstName.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody lastNameBody =  RequestBody.create(etLastName.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody emailBody =  RequestBody.create(etEmail.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody phoneBody =  RequestBody.create(etPhone.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody stateBody=  RequestBody.create(etState.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody schoolNameBody =  RequestBody.create(etSchoolName.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody emergencyContactBody =  RequestBody.create(etEmergencyContact.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody emergencyPhoneBody =  RequestBody.create(etEmergencyPhone.getText().toString().trim(),
                MediaType.parse("multipart/form-data"));
        RequestBody genderBody =  RequestBody.create(String.valueOf(gender),
                MediaType.parse("multipart/form-data"));
        RequestBody IdBody =  RequestBody.create(userDataModel.getId().toString(),
                MediaType.parse("multipart/form-data"));

        ApiClient.getClient()
                .create(ApiInterface.class)
                .updateUser(imageBody, IdBody, schoolIDBody, nameBody, firstNameBody, lastNameBody, emailBody, phoneBody, stateBody, schoolNameBody, emergencyContactBody, emergencyPhoneBody, genderBody, new PreferenceManager(context).getAccessToken())
                .enqueue(new Callback<AddUserResponseModel>() {
                    @Override
                    public void onResponse(@NotNull Call<AddUserResponseModel> call, @NotNull Response<AddUserResponseModel> response) {
                        progressDialog.dismiss();
                        if(response.isSuccessful()){
                            assert response.body() != null;
                            if(!response.body().getError()){
                                setResult(RESULT_OK,new Intent()
                                        .putExtra("position",getIntent().getIntExtra("position",0))
                                        .putExtra("data",response.body().getData()));
                                finish();
                            }else
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NotNull Call<AddUserResponseModel> call, @NotNull Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }


}