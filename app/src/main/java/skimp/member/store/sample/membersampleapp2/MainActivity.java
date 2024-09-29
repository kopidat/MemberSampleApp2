package skimp.member.store.sample.membersampleapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import skimp.store.lib.LibHelper;
import skimp.store.lib.member.SKIMP_Store_M_Lib;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_member_test = findViewById(R.id.btn_member_test);
        btn_member_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btn_member_test.onClick(View view)");

                // 정직원 인증
                SKIMP_Store_M_Lib.getInstance().auth(MainActivity.this, new LibHelper.OnResultListener() {
                    @Override
                    public void onResult(JSONObject result) {
                        Log.i(TAG, "auth.onResult(JSONObject result) = " + result);

                        try {
                            String code = result.getString(LibHelper.Result.KEY_code);
                            String msg = result.getString(LibHelper.Result.KEY_msg);
                            Log.d(TAG, "code / msg = " + code + " / " + msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        Button btn_partner_test = findViewById(R.id.btn_partner_test);
        btn_partner_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startSTT(MainActivity.this);
                FingerprintHelper fingerprintHelper = new FingerprintHelper();
                fingerprintHelper.startListening(MainActivity.this, new FingerprintHelper.Callback() {
                    @Override
                    public void onAuthenticationSucceeded() {

                    }

                    @Override
                    public void onAuthenticationFailed(int failMsgId, CharSequence failString) {

                    }

                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {

                    }
                });
            }
        });
    }


    private SpeechRecognizer mSpeechRecognizer;

//    // Create an intent that can start the Speech Recognizer activity
//    public void startSTTActivity(Activity activity, final ResultListener resultListener) {
//        PLog.i(TAG, "startSTTActivity(Activity activity, final ResultListener resultListener)");
//        PLog.d(TAG, "resultListener = " + resultListener);
//
//        mDefaultResultListener = resultListener;
//
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
////		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//
//        // Start the activity, the intent will be populated with the speech text
//        activity.startActivityForResult(intent, BaseActivity.REQ_CODE_STT);
//    }

    public void startSTT(Activity activity) {
        Log.i(TAG, "startSTT(Activity activity)");

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, activity.getPackageName());
//		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity.getApplicationContext());
        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
        mSpeechRecognizer.startListening(intent);
    }

    public void stopSTT() {
        if(mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer = null;
        }
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.i(TAG, "onActivityResult(int requestCode, int resultCode, Intent data)");
//        Log.d(TAG, "requestCode = " + requestCode);
//        Log.d(TAG, "resultCode = " + resultCode);
//        Log.d(TAG, "data = " + data);
//
//        if (requestCode == BaseActivity.REQ_CODE_STT) {
//            if (resultCode == Activity.RESULT_OK) {
//                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                String spokenText = results.get(0);
//
//                // Do something with spokenText
//                Log.d(TAG, "spokenText = " + spokenText);
//            }
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private RecognitionListener mRecognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.i(TAG, "onReadyForSpeech(Bundle params)");
            Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.",Toast.LENGTH_SHORT).show();
//            Utils.debugBundle(TAG, params);
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.i(TAG, "onBeginningOfSpeech()");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.i(TAG, "onRmsChanged(float rmsdB) = " + rmsdB);
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.i(TAG, "onBufferReceived(byte[] buffer) = " + buffer);
        }

        @Override
        public void onEndOfSpeech() {
            Log.i(TAG, "onEndOfSpeech()");
        }

        @Override
        public void onError(int error) {
            Log.e(TAG, "onError(int error) = " + error);
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

			Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어준다.
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            StringBuilder stringBuilder = new StringBuilder();
            for (String string : matches) {
                Log.d(TAG, "match string = " + string);
                stringBuilder.append(string);
            }
            Log.d(TAG, "matchStrings = " + stringBuilder.toString());
//            responseSimple(STT.RESULT_CODE_SUCCESS, stringBuilder.toString());
        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    };
}