package com.webexert.digitaleye.Retrofit;

import com.webexert.digitaleye.Model.AddUserResponseModel;
import com.webexert.digitaleye.Model.DeleteUserResponseModel;
import com.webexert.digitaleye.Model.QuestionResponseModel;
import com.webexert.digitaleye.Model.QuizResponseModel;
import com.webexert.digitaleye.Model.RegisterResponseModel;
import com.webexert.digitaleye.Model.UpdateProfileResponseModel;
import com.webexert.digitaleye.Model.UserResponseModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @Multipart
    @POST("api/register")
    Call<Object> register(@Part MultipartBody.Part image,
                                         @Part ("name") RequestBody name,
                                         @Part ("email") RequestBody email,
                                         @Part ("phone") RequestBody phone,
                                         @Part ("password") RequestBody password,
                                         @Part ("password_confirmation") RequestBody confirmPassword,
                                         @Part ("first_name") RequestBody firstName,
                                         @Part ("last_name") RequestBody lastName,
                                         @Part ("is_employee") RequestBody isEmployee,
                                         @Part ("is_parent") RequestBody isParent,
                                         @Part ("gender") RequestBody gender);

    @Multipart
    @POST("api/profile-update")
    Call<UpdateProfileResponseModel> updateProfile(@Part MultipartBody.Part image,
                                                   @Part ("name") RequestBody name,
                                                   @Part ("first_name") RequestBody firstName,
                                                   @Part ("last_name") RequestBody lastName,
                                                   @Part ("phone") RequestBody phone,
                                                   @Part ("is_employee") RequestBody isEmployee,
                                                   @Part ("is_parent") RequestBody isParent,
                                                   @Part ("gender") RequestBody gender,
                                                   @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("api/login")
    Call<Object> login(@Field ("email") String email,
                                   @Field ("password") String password);

    @Multipart
    @POST("api/student-add")
    Call<Object> addUser(@Part MultipartBody.Part image,
                         RequestBody schoolIDBody, RequestBody nameBody, RequestBody firstNameBody, @Part("student_id") RequestBody studentID,
                         @Part("full_name") RequestBody name,
                         @Part("first_name") RequestBody firstName,
                         @Part("last_name") RequestBody lastName,
                         @Part("email") RequestBody email,
                         @Part("phone") RequestBody phone,
                         @Part("address") RequestBody address,
                         @Part("city") RequestBody city,
                         @Part("state") RequestBody state,
                         @Part("zip") RequestBody zip,
                         @Part("school_name") RequestBody schoolName,
                         @Header("Authorization") String token);

    @Multipart
    @POST("api/student-edit")
    Call<AddUserResponseModel> updateUser(@Part MultipartBody.Part image,
                                          @Part("id") RequestBody Id,
                                          @Part("student_id") RequestBody studentID,
                                          @Part("full_name") RequestBody name,
                                          @Part("first_name") RequestBody firstName,
                                          @Part("last_name") RequestBody lastName,
                                          @Part("email") RequestBody email,
                                          @Part("phone") RequestBody phone,
                                          @Part("state") RequestBody state,
                                          @Part("city") RequestBody city,
                                          @Part("zip") RequestBody zip,
                                          @Part("address") RequestBody address,
                                          @Part("school_name") RequestBody schoolName,
                                          @Header("Authorization") String token);

    @GET("api/get-students")
    Call<UserResponseModel> getUsers(@Header("Authorization") String token);

    @GET("api/student-delete")
    Call<DeleteUserResponseModel> deleteUser(@Query("id") Integer id,
                                             @Header("Authorization") String token);

    @GET("api/get-questions")
    Call<QuestionResponseModel> getQuestions(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("api/add-quiz")
    Call<QuizResponseModel> submitQuiz(@Field("data") String data,
                                       @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("api/change-password")
    Call<Object> changePassword(@Field("old_password") String oldPassword,
                                                     @Field("password") String password,
                                                     @Field("password_confirmation") String confirmPassword,
                                                     @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("api/reset-password")
    Call<Object> sendEmail(@Field("email") String email);


    @POST("api/confirm-code")
    Call<Object> ConfirmCode(@Query("email") String email,@Query("code") String code);


    @FormUrlEncoded
    @POST("api/send-email")
    Call<Object> resendVerificationCode(@Field("email") String email);

    @POST("api/reset-password")
    Call<Object> resetPassword(@Query("email") String email);

}
