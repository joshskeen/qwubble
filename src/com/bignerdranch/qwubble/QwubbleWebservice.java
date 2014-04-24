package com.bignerdranch.qwubble;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

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
                .build();
        return restAdapter.create(QwubbleWebInterface.class);
    }

    public void getPing(Callback<Void> callback) {
        getService().getPing(callback);
    }

    @Override
    public void postRegistration(@Field("registration_id") String registrationId, Callback<Void> callback) {
        getService().postRegistration(registrationId, callback);
    }
}

interface QwubbleWebInterface {

    @GET("/ping")
    public void getPing(Callback<Void> callback);

    @POST("/registrations")
    @FormUrlEncoded
    public void postRegistration(@Field("registration_id") String registrationId, Callback<Void> callback);

}