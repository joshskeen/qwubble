package com.bignerdranch.qwubble.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuestionData implements Serializable, IQwubble{

    public static final String QWUBBLE_DEFAULT_IMG = "http://res.cloudinary.com/big-nerd-ranch/image/upload/v1398461928/varying_qubbles_kyc5bb.png";
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
        if(imageUrl == null){
            return QWUBBLE_DEFAULT_IMG;
        }
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