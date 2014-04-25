package com.bignerdranch.qwubble.web;

import com.bignerdranch.qwubble.data.AnswerData;
import com.bignerdranch.qwubble.data.QuestionData;
import retrofit.Callback;
import retrofit.http.*;

import java.util.List;

public interface QwubbleWebInterface {

    @POST("/registrations")
    @FormUrlEncoded
    public void postRegistration(@Field("registration_id") String registrationId, Callback<Void> callback);

    @GET("/answers/{question_id}")
    public void getAnswers(@Path("question_id") int questionId, Callback<List<AnswerData>> callback);

    @POST("/questions")
    @FormUrlEncoded
    public void postQuestion(@Field("registration_id") String registrationId, @Field("question") String question, Callback<QuestionData> callback);

    @POST("/answers")
    @FormUrlEncoded
    public void postAnswer(@Field("question_id") String questionId, @Field("registration_id") String registrationId, @Field("answer") String answer, Callback<Void> callback);


}