package com.bignerdranch.qwubble.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuestionData implements Serializable, IQwubble{

    @SerializedName("id")
    public int id;

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

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public String getAnswer() {
        return null;
    }

    @Override
    public int getId() {
        return id;
    }

}