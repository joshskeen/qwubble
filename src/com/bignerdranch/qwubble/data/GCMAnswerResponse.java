package com.bignerdranch.qwubble.data;

import com.google.gson.annotations.SerializedName;

public class GCMAnswerResponse {

    @SerializedName("type")
    public String type;

    @SerializedName("answer")
    public AnswerData mAnswerData;

    @Override
    public String toString() {
        return "GCMQuestionResponse{" +
                "type='" + type + '\'' +
                ", mQuestionData=" + mAnswerData +
                '}';
    }
}
