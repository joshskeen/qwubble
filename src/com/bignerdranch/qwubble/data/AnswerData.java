package com.bignerdranch.qwubble.data;

import com.google.gson.annotations.SerializedName;

public class AnswerData extends QwubbleData {

    @SerializedName("id")
    public int id;

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
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public String getAnswer() {
        return answer;
    }

    @Override
    public int getId() {
        return id;
    }
}
