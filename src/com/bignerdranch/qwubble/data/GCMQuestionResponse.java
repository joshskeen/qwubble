package com.bignerdranch.qwubble.data;

import com.google.gson.annotations.SerializedName;

public class GCMQuestionResponse {

    @SerializedName("type")
    public String type;

    @SerializedName("question")
    public QuestionData mQuestionData;

    @Override
    public String toString() {
        return "GCMQuestionResponse{" +
                "type='" + type + '\'' +
                ", mQuestionData=" + mQuestionData +
                '}';
    }
}
