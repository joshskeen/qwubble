package com.bignerdranch.qwubble;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bignerdranch.qwubble.data.AnswerData;
import com.bignerdranch.qwubble.data.IQwubble;
import com.bignerdranch.qwubble.web.QwubbleWebservice;
import org.andengine.util.debug.Debug;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by bphillips on 4/26/14.
 */
public class QwubbleDialogActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "QwubbleDialogActivity";

    public static final String EXTRA_REG_ID = "com.bignerdranch.qwubble.REG_ID";
    public static final String EXTRA_IQWUBBLE = "com.bignerdranch.qwubble.IQWUBBLE";

    TextView mQuestionTextView;
    EditText mAnswerQuestionEditText;
    IQwubble mIQwubble;
    String mRegID;
    private ImageView mImageView;
    private ListView mAnswersView;

    public static Intent getIntent(Context c, String regId, IQwubble qwubble) {
        Intent i = new Intent(c, QwubbleDialogActivity.class);

        i.putExtra(EXTRA_REG_ID, regId);
        i.putExtra(EXTRA_IQWUBBLE, qwubble);

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);

        mRegID = getIntent().getStringExtra(EXTRA_REG_ID);
        mIQwubble = (IQwubble) getIntent().getSerializableExtra(EXTRA_IQWUBBLE);

        mQuestionTextView = (TextView) findViewById(R.id.qwubbleQuestion);
        mAnswerQuestionEditText = (EditText) findViewById(R.id.mAnswerQuestionEditText);
        mImageView = (ImageView) findViewById(R.id.qwubbleImage);
        mAnswersView = (ListView) findViewById(R.id.qwubbleAnswers);

        findViewById(R.id.answerQuestionButton).setOnClickListener(this);

        loadQwubbleImage();
    }

    private void loadQwubbleImage() {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return downloadImage(Util.getCloudinaryUrl(mIQwubble.getImageUrl(), 200));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                mImageView.setImageBitmap(bitmap);

            }
        }.execute();
    }

    //get the qwubble answers, and display them in the list
    private void loadQwubbleAnswers() {

        QwubbleWebservice.getService().getAnswers(mIQwubble.getId(), new Callback<List<AnswerData>>() {
            @Override
            public void success(List<AnswerData> answerDatas, Response response) {
                if (answerDatas != null && answerDatas.size() > 0) {
                    ArrayAdapter<AnswerData> arrayAdapter = new ArrayAdapter<AnswerData>(QwubbleDialogActivity.this, R.layout.answer_item, answerDatas) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            if (convertView == null) {
                                LayoutInflater layoutInflater = getLayoutInflater();
                                convertView = layoutInflater.inflate(R.layout.answer_item, parent, false);
                            }
                            TextView answerText = (TextView) convertView.findViewById(R.id.mAnswerDisplayTV);
                            answerText.setText(getItem(position).answer);
                            return convertView;
                        }
                    };
                    mAnswersView.setAdapter(arrayAdapter);
                    mAnswersView.setSelection(arrayAdapter.getCount() - 1);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Debug.e(TAG, "OH NO!!!! - retrgoit error: " + retrofitError.getCause());
            }
        });

    }


    private Bitmap downloadImage(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("NOOOO", "Error getting the image from server : " + e.getMessage().toString());
        }
        return bm;
    }


    @Override
    public void onClick(View v) {
        QwubbleWebservice.getService().postAnswer(mIQwubble.getId(), mRegID, mAnswerQuestionEditText.getText().toString(), new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                mAnswerQuestionEditText.setText("");
                Toast.makeText(QwubbleDialogActivity.this, "Answer Posted!", Toast.LENGTH_LONG);
                finish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(TAG, "FAILURE");
            }
        });
    }
}
