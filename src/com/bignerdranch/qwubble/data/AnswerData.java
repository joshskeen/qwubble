package com.bignerdranch.qwubble.data;

import com.google.gson.annotations.SerializedName;

public class AnswerData {

    @SerializedName("id")
    public String id;

    @SerializedName("question")
    public String question;

    @SerializedName("answer")
    public String answer;

    @SerializedName("question_id")
    public int questionId;

    @Override
    public String toString() {
        return "AnswerData{" +
                "id='" + id + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", questionId=" + questionId +
                '}';
    }
}
