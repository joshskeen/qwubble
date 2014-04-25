package com.bignerdranch.qwubble;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
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

public class QwubbleDialogFragment extends DialogFragment {

    public static final String QWUBBLE_DATA = "QwubbleData";
    private static final String TAG = "QwubbleDialogFragment";
    private static String mRegID;
    private TextView mQuestionText;
    private ListView mAnswersView;
    private IQwubble mIQwubble;
    private ImageView mImageView;
    private View mAnswerQuestionButton;
    private EditText mAnswerQuestionEditText;
    private TextView noAnswersFound;
    private ArrayAdapter<AnswerData> mAnswerDataArray;

    public static QwubbleDialogFragment newInstance(IQwubble iQwubble, String regId) {
        QwubbleDialogFragment qwubbleDialogFragment = new QwubbleDialogFragment();
        Bundle bundle = new Bundle();
        mRegID = regId;
        bundle.putSerializable(QWUBBLE_DATA, iQwubble);
        qwubbleDialogFragment.setArguments(bundle);
        return qwubbleDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater i = getActivity().getLayoutInflater();

        View view = i.inflate(R.layout.qwubble_dialog_fragment, null);

        loadQwubbleAnswers();
        loadQwubbleImage();

        mQuestionText = (TextView) view.findViewById(R.id.qwubbleQuestion);
        mImageView = (ImageView) view.findViewById(R.id.qwubbleImage);
        mAnswersView = (ListView) view.findViewById(R.id.qwubbleAnswers);
        mAnswerQuestionButton = view.findViewById(R.id.answerQuestionButton);
        mAnswerQuestionEditText = (EditText) view.findViewById(R.id.mAnswerQuestionEditText);
        noAnswersFound = (TextView) view.findViewById(R.id.noAnswersFound);
        mAnswersView.setEmptyView(noAnswersFound);

        mQuestionText.setText(mIQwubble.getQuestion());
        Debug.d("QUBBLE ID: " + mIQwubble.getId());

        mAnswerQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QwubbleWebservice.getService().postAnswer(mIQwubble.getId(), mRegID, mAnswerQuestionEditText.getText().toString(), new Callback<Void>() {
                    @Override
                    public void success(Void aVoid, Response response) {
                        mAnswerQuestionEditText.setText("");
                        Toast.makeText(getActivity(), "Answer Posted!", Toast.LENGTH_LONG);
                        dismiss();
                    }
                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.d(TAG, "FAILURE");
                    }
                });
            }
        });

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );

        return b.create();
    }

    //get the qwubble answers, and display them in the list
    private void loadQwubbleAnswers() {
        new AsyncTask<Void, Void, List<AnswerData>>() {
            List<AnswerData> mAnswerDatas;

            @Override
            protected List<AnswerData> doInBackground(Void... params) {
                QwubbleWebservice.getService().getAnswers(mIQwubble.getId(), new Callback<List<AnswerData>>() {
                    @Override
                    public void success(List<AnswerData> answerDatas, Response response) {
                        mAnswerDatas = answerDatas;
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Debug.e(TAG, "OH NO!!!! - retrgoit error: " + retrofitError.getCause());
                    }
                });
                return mAnswerDatas;
            }

            @Override
            protected void onPostExecute(final List<AnswerData> answerDatas) {
//                Log.d(TAG, "GOT: " + answerDatas);
//                super.onPostExecute(answerDatas);
//                mAnswerDataArray = new ArrayAdapter<AnswerData>(getActivity(), R.layout.answer_item, answerDatas) {
//                    @Override
//                    public View getView(int position, View convertView, ViewGroup parent) {
//                        if (convertView == null) {
//                            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
//                            View inflate = layoutInflater.inflate(R.layout.answer_item, parent, false);
//                            TextView answerText = (TextView) inflate.findViewById(R.id.mAnswerDisplayTV);
//                            answerText.setText(getItem(position).answer);
//                            return inflate;
//                        }
//                        return super.getView(position, convertView, parent);
//
//                    }
//                };
//                mAnswersView.setAdapter(mAnswerDataArray);
            }
        }.execute();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIQwubble = (IQwubble) getArguments().getSerializable(QWUBBLE_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
