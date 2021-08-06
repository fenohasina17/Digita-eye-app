package com.webexert.digitaleye.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.webexert.digitaleye.Helper.PreferenceManager;
import com.webexert.digitaleye.Model.AnswerDataModel;
import com.webexert.digitaleye.Model.AnswerResponseModel;
import com.webexert.digitaleye.Model.QuestionDataModel;
import com.webexert.digitaleye.Model.QuestionModel;
import com.webexert.digitaleye.Model.QuestionResponseModel;
import com.webexert.digitaleye.Model.QuizResponseModel;
import com.webexert.digitaleye.R;
import com.webexert.digitaleye.Retrofit.ApiClient;
import com.webexert.digitaleye.Retrofit.ApiInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyActivity extends AppCompatActivity {

    private Context context;
    private final String TAG = "SurveyResponse";
    private TextView tvPercentage,tvQuestion;
    private ImageView ivBack;
    private Button btnYes,btnNo,btnFinish;
    private ProgressBar progressBar;
    private ArrayList<QuestionDataModel> questionData = new ArrayList<>();
    private RelativeLayout rlQuiz;
    private LinearLayout llComplete;
    private Integer position = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        initialization();

        listener();

        getQuestionsRequest();

    }

    private void listener() {
        btnYes.setOnClickListener(v -> {
            moveToNext(true);
        });

        btnNo.setOnClickListener(v -> {
            moveToNext(false);
        });

        btnFinish.setOnClickListener(v -> {
            createAnswerJSON();
        });

        ivBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @SuppressLint("SetTextI18n")
    private void moveToNext(boolean answer){

        if(position < questionData.size())
            questionData.get(position).setAnswer(answer);

        if(position + 1 < questionData.size()){
            position = position + 1;
            progressBar.setProgress(position+1);
            tvPercentage.setText((position+1)+" / "+questionData.size());
            tvQuestion.setText(questionData.get(position).getName());
        }else {
            rlQuiz.setVisibility(View.GONE);
            llComplete.setVisibility(View.VISIBLE);
        }
    }

    private void initialization() {
        context = this;
        ivBack = findViewById(R.id.ivBack);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvQuestion = findViewById(R.id.tvQuestion);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);
        btnFinish = findViewById(R.id.btnFinish);
        rlQuiz = findViewById(R.id.rlQuiz);
        llComplete = findViewById(R.id.llComplete);
        progressBar = findViewById(R.id.progressBar);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
    }

    private void getQuestionsRequest(){

        progressDialog.show();

        ApiClient.getClient()
                .create(ApiInterface.class)
                .getQuestions(new PreferenceManager(context).getAccessToken())
                .enqueue(new Callback<QuestionResponseModel>() {
                    @Override
                    public void onResponse(@NotNull Call<QuestionResponseModel> call, @NotNull Response<QuestionResponseModel> response) {
                        progressDialog.dismiss();
                        if(response.isSuccessful()){
                            if(!response.body().getError()){
                                questionData.addAll(response.body().getData());
                                progressBar.setMax(questionData.size());
                                progressBar.setProgress(1);
                                tvPercentage.setText((position+1)+" / "+questionData.size());
                                tvQuestion.setText(questionData.get(position).getName());
                            }else
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(@NotNull Call<QuestionResponseModel> call, @NotNull Throwable t) {
                        progressDialog.dismiss();
                    }
                });

    }

    private void createAnswerJSON(){
        AnswerResponseModel answerResponseModel = new AnswerResponseModel();
        ArrayList<AnswerDataModel> answerData = new ArrayList<>();
        for (int i = 0 ; i < questionData.size() ; i ++){
            answerData.add(new AnswerDataModel(questionData.get(i).getId(),questionData.get(i).getAnswer()));
        }
        answerResponseModel.setStudentId(getIntent().getIntExtra("id",0));
        answerResponseModel.setQuestions(answerData);
        submitQuizRequest(new Gson().toJson(answerResponseModel));
    }

    private void submitQuizRequest(String data){
        progressDialog.show();

        ApiClient.getClient()
                .create(ApiInterface.class)
                .submitQuiz(data,new PreferenceManager(context).getAccessToken())
                .enqueue(new Callback<QuizResponseModel>() {
                    @Override
                    public void onResponse(@NotNull Call<QuizResponseModel> call, @NotNull Response<QuizResponseModel> response) {
                        progressDialog.dismiss();
                        if(response.isSuccessful()){
                            if(!response.body().getError()){
                                setResult(RESULT_OK,new Intent()
                                        .putExtra("position",getIntent().getIntExtra("position",0))
                                        .putExtra("message",response.body().getMessage()));
                                finish();
                            }else
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(@NotNull Call<QuizResponseModel> call, @NotNull Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}