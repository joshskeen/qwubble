package com.bignerdranch.qwubble.data;

import java.io.Serializable;

public interface IQwubble extends Serializable {

    public String getImageUrl();

    public String getQuestion();

    public String getAnswer();

    public int getId();
}
