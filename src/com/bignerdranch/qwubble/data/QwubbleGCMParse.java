package com.bignerdranch.qwubble.data;

import com.google.gson.annotations.SerializedName;

public class QwubbleGCMParse {

    @SerializedName("type")
    public String type;

    @SerializedName("question")
    public QuestionData mQuestion;


}
