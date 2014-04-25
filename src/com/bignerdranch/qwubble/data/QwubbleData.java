package com.bignerdranch.qwubble.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QwubbleData implements Serializable {

    public int mId;
    public String mQuestion = "do androids dream of electric sheep?";
    public String mUrl ="http://www.blogcdn.com/www.engadget.com/media/2009/03/3-22-09-taser-axom-system.jpg";
    public String mRegId;
    public List<AnswerData> mAnswers = new ArrayList<AnswerData>();

}
