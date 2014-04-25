package com.bignerdranch.qwubble.web;

import com.bignerdranch.qwubble.data.AnswerData;
import com.bignerdranch.qwubble.data.QuestionData;
import com.google.gson.Gson;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.http.Field;
import retrofit.http.Path;
import retrofit.mime.TypedInput;

import java.lang.reflect.Type;
import java.util.List;

public class QwubbleWebservice implements QwubbleWebInterface {

    public static final String HEADER_ACCEPT = "Accept";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";

    public static QwubbleWebInterface getService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://qwubble-api.herokuapp.com/api")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade requestFacade) {
                        requestFacade.addHeader(HEADER_ACCEPT, APPLICATION_JSON);
                    }
                })
                .setConverter(new GsonConverter(new Gson()) {
                    @Override
                    public Object fromBody(TypedInput body, Type type) throws ConversionException {
                        //Util.debugConverter(body);
                        return super.fromBody(body, type);
                    }
                })
                .build();
        return restAdapter.create(QwubbleWebInterface.class);
    }

    @Override
    public void postRegistration(@Field("registration_id") String registrationId, Callback<Void> callback) {
        getService().postRegistration(registrationId, callback);
    }

    @Override
    public void getAnswers(@Path("question_id") int questionId, Callback<List<AnswerData>> callback) {
        getService().getAnswers(questionId, callback);
    }

    @Override
    public void postQuestion(@Field("registration_id") String registrationId, @Field("question") String question, Callback<QuestionData> callback) {
        getService().postQuestion(registrationId, question, callback);
    }

    @Override
    public void postAnswer(@Field("question_id") String questionId, @Field("registration_id") String registrationId, @Field("answer") String answer, Callback<Void> callback) {
        getService().postAnswer(questionId, registrationId, answer, callback);
    }

}

