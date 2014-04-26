package com.bignerdranch.qwubble;

import retrofit.mime.TypedInput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {

    public static String getCloudinaryUrl(String url, int size) {
        return "http://res.cloudinary.com/dcu4qkwdf/image/fetch/w_" + size + ",h_" + size + ",r_max,c_thumb,g_face,c_fill,t_png,/" + url;
    }

    public static String getCloudinaryUrl(String url) {
        return getCloudinaryUrl(url, MainActivity.QWUBBLE_WIDTH);
    }


    public static void debugConverter(TypedInput body) {
        try {
            String stringFromInputStream = getStringFromInputStream(body.in());
            System.out.println("got: " + stringFromInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}


