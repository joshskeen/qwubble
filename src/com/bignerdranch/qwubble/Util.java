package com.bignerdranch.qwubble;

public class Util {

    public static String getCloudinaryUrl(String url, int size) {
        return "http://res.cloudinary.com/dcu4qkwdf/image/fetch/w_"+ size +",h_"+size+",r_max,c_thumb,g_face,c_fill,t_png,/" + url;
    }

    public static String getCloudinaryUrl(String url) {
        return getCloudinaryUrl(url, 150);
    }

}
