package com.webexert.digitaleye.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.webexert.digitaleye.Activity.AddUserActivity;
import com.webexert.digitaleye.Activity.SurveyActivity;
import com.webexert.digitaleye.Adapter.UserAdapter;
import com.webexert.digitaleye.Helper.PreferenceManager;
import com.webexert.digitaleye.Model.DeleteUserResponseModel;
import com.webexert.digitaleye.Model.UserDataModel;
import com.webexert.digitaleye.Model.UserResponseModel;
import com.webexert.digitaleye.R;
import com.webexert.digitaleye.Retrofit.ApiClient;
import com.webexert.digitaleye.Retrofit.ApiInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private Context context;
    private final String TAG = "HomeFragResponse";
    private final Integer ADD_USER_REQUEST = 1;
    private final Integer QUIZ_REQUEST = 2;
    private final Integer UPDATE_USER_REQUEST = 3;
    private RecyclerView rvUsers;
    private TextView tvNotFound;
    private FloatingActionButton fabAddUser;
    private ArrayList<UserDataModel> userData = new ArrayList<>();
    private UserAdapter userAdapter;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialization(view);

        listener();

        getUsersRequest();
    }

    private void listener() {
        userAdapter.setOnClickListener(new UserAdapter.UserInterface() {
            @Override
            public void OnItemClickListener(View view, int index) {
                if(userData.get(index).getSurvey() == 0 ){
                    startActivityForResult(new Intent(context, SurveyActivity.class)
                            .putExtra("position",index)
                            .putExtra("id",userData.get(index).getId()),QUIZ_REQUEST);
                }else {
                    alreadyCompletedDialog();
                }
            }

            @Override
            public void OnDeleteClickListener(View view, int index) {
                deleteDialog(index);
            }

            @Override
            public void OnEditClickListener(View view, int index) {
                startActivityForResult(new Intent(context, AddUserActivity.class).
                        putExtra("type","update")
                        .putExtra("position",index)
                        .putExtra("data",userData.get(index)),UPDATE_USER_REQUEST);
            }
        });

        fabAddUser.setOnClickListener(v -> startActivityForResult(new Intent(context, AddUserActivity.class).
                putExtra("type","new"),ADD_USER_REQUEST));
    }

    private void initialization(View view) {
        context = requireContext();
        rvUsers = view.findViewById(R.id.rvUsers);
        tvNotFound = view.findViewById(R.id.tvNotFound);
        fabAddUser = view.findViewById(R.id.fabAddUser);
        userAdapter = new UserAdapter(context,userData);
        rvUsers.setAdapter(userAdapter);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if(requestCode == ADD_USER_REQUEST && resultCode == Activity.RESULT_OK){
            UserDataModel userDataModel = intent.getParcelableExtra("data");
            userDataModel.setSurvey(0);
            userData.add(userDataModel);
            userAdapter.notifyDataSetChanged();
            tvNotFound.setVisibility(View.GONE);
        }else if(requestCode == QUIZ_REQUEST && resultCode == Activity.RESULT_OK){
            int position = intent.getIntExtra("position",0);
            userData.get(position).setSurvey(1);
            userAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        }else if(requestCode == UPDATE_USER_REQUEST && resultCode == Activity.RESULT_OK){
            UserDataModel userDataModel = intent.getParcelableExtra("data");
            int position = intent.getIntExtra("position",0);
            userData.set(position,userDataModel);
            userAdapter.notifyDataSetChanged();
        }
    }

    private void getUsersRequest(){
        progressDialog.show();
        ApiClient.getClient()
                .create(ApiInterface.class)
                .getUsers(new PreferenceManager(context).getAccessToken())
                .enqueue(new Callback<UserResponseModel>() {
                    @Override
                    public void onResponse(@NotNull Call<UserResponseModel> call, @NotNull Response<UserResponseModel> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful()){
                            if(!response.body().getError()){
                                tvNotFound.setVisibility(View.GONE);
                                userData.addAll(response.body().getData());
                                userAdapter.notifyDataSetChanged();
                            }else
                                tvNotFound.setVisibility(View.VISIBLE);
                        }else
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(@NotNull Call<UserResponseModel> call, @NotNull Throwable t) {
                        progressDialog.dismiss();
                    }
                });

    }

    private void deleteUserRequest(int index){
        progressDialog.show();
        ApiClient.getClient()
                .create(ApiInterface.class)
                .deleteUser(userData.get(index).getId(),new PreferenceManager(context).getAccessToken())
                .enqueue(new Callback<DeleteUserResponseModel>() {
                    @Override
                    public void onResponse(@NotNull Call<DeleteUserResponseModel> call, @NotNull Response<DeleteUserResponseModel> response) {
                        progressDialog.dismiss();
                        if(response.isSuccessful()){
                            if(!response.body().getError()){
                                userData.remove(index);
                                userAdapter.notifyDataSetChanged();
                                if(userData.size() == 0) tvNotFound.setVisibility(View.VISIBLE);
                            }else
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(@NotNull Call<DeleteUserResponseModel> call, @NotNull Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteDialog(int index){
        new AlertDialog.Builder(context)
                .setTitle("Alert...!")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dialog.dismiss();
                    deleteUserRequest(index);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void alreadyCompletedDialog(){
        new AlertDialog.Builder(context)
                .setTitle("Alert...!")
                .setMessage("You have already completed the today survey against this student!")
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

}