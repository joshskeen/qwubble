package com.bignerdranch.qwubble.data;

import com.google.gson.annotations.SerializedName;

public class QuestionResponse {

    @SerializedName("id")
    public String id;

    @SerializedName("registration_id")
    public String registrationId;

    @SerializedName("question")
    public String question;

    @SerializedName("image_url")
    public String imageUrl;

    @Override
    public String toString() {
        return "QuestionResponse{" +
                "id='" + id + '\'' +
                ", registrationId='" + registrationId + '\'' +
                ", question='" + question + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
