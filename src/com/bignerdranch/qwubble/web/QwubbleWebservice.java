package com.bignerdranch.qwubble.web;

import com.bignerdranch.qwubble.data.AnswerData;
import com.bignerdranch.qwubble.data.QuestionData;
import com.google.gson.Gson;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
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
    public void postRegistration(String registrationId, Callback<Void> callback) {
        getService().postRegistration(registrationId, callback);
    }

    @Override
    public void getAnswers(int questionId, Callback<List<AnswerData>> callback) {
        getService().getAnswers(69, callback);
    }

    @Override
    public void postQuestion(String registrationId, String question, Callback<QuestionData> callback) {
        getService().postQuestion(registrationId, question, callback);
    }

    @Override
    public void postAnswer(int questionId,String registrationId, String answer, Callback<Void> callback) {
        getService().postAnswer(questionId, registrationId, answer, callback);
    }

}

