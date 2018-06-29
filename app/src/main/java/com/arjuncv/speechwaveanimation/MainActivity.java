package com.arjuncv.speechwaveanimation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import speechwaveanimation.arjuncv.com.speechwaveanimation.R;


public class MainActivity extends AppCompatActivity implements RecognitionListener {

    private TextView tvVoiceSpeech, tvTryAgain;
    private SpeechRecognizer recognizer;
    private static final float QUIT_RMSDB_MAX = 2f;
    private static final float MEDIUM_RMSDB_MAX = 5.5f;
    private boolean isPlaying;
    private View voiceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSpeech();
        startSpeech();
    }

    private void startSpeech() {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                            getPackageName());
                    intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

                    recognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                    recognizer.setRecognitionListener(MainActivity.this);
                    recognizer.startListening(intent);
                }
            });
    }

    private void initSpeech() {
        tvVoiceSpeech =findViewById(R.id.tv_voice_speech);
        voiceView = findViewById(R.id.view_small_circle);
        tvTryAgain = findViewById(R.id.tv_try_again);
        tvTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeech();
            }
        });
    }


    // speech receiver's listener
    @Override
    public void onBeginningOfSpeech() {
        tvVoiceSpeech.setText("Please speak");
    }

    @Override
    public void onResults(Bundle results) {
        if (!isDestroyed()) {
            ArrayList<String> voiceResults = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (voiceResults != null && voiceResults.get(0) != null) {
                String strKey = voiceResults.get(0);
                callSearchAPI(strKey);
            }
        }
    }

    private void callSearchAPI(String strKey) {
        //ur search logic here
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onError(int error) {
        int message = R.string.error_message;
        switch (error) {
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = R.string.voice_no_match;
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = R.string.permission_denied;
                break;
            case SpeechRecognizer.ERROR_AUDIO:
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = R.string.check_network_toast_msg;
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = R.string.check_network_toast_msg;
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                break;
            case SpeechRecognizer.ERROR_SERVER:
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = R.string.voice_timeout;
                break;
            default:
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        if (!isDestroyed()) {
            ArrayList<String> voiceResults = partialResults
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            if (voiceResults != null && voiceResults.get(0) != null) {
                if (tvVoiceSpeech.getText().equals(getString(R.string.listening))) {
                    tvVoiceSpeech.setText("");
                }
                tvVoiceSpeech.setTextColor(getResources().getColor(R.color.black));
                tvVoiceSpeech.setText(voiceResults.get(0));
                tvVoiceSpeech.append(" ");
            }
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        ObjectAnimator waveAnimator;
        if (rmsdB < QUIT_RMSDB_MAX) {
            // quiet
            waveAnimator = ObjectAnimator.
                    ofPropertyValuesHolder(voiceView, PropertyValuesHolder.ofFloat("scaleX", 0.4f),
                            PropertyValuesHolder.ofFloat("scaleY", 0.4f));

        } else if (rmsdB >= QUIT_RMSDB_MAX && rmsdB < MEDIUM_RMSDB_MAX) {
            // medium
            waveAnimator = ObjectAnimator.
                    ofPropertyValuesHolder(voiceView, PropertyValuesHolder.ofFloat("scaleX", 0.8f),
                            PropertyValuesHolder.ofFloat("scaleY", 0.8f));
        } else {
            // loud
            waveAnimator = ObjectAnimator.
                    ofPropertyValuesHolder(voiceView, PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                            PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        }

        waveAnimator.setDuration(90);
        waveAnimator.setInterpolator(new LinearInterpolator());

        if (!isPlaying) {
            waveAnimator.start();
        }
        waveAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isPlaying = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(205, 205);
                params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.iv_voice_speech);
                params.addRule(RelativeLayout.ALIGN_TOP, R.id.iv_voice_speech);
                params.addRule(RelativeLayout.ALIGN_LEFT, R.id.iv_voice_speech);
                params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.iv_voice_speech);
                voiceView.setLayoutParams(params);
                voiceView.requestLayout();
                isPlaying = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(205, 205);
                params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.iv_voice_speech);
                params.addRule(RelativeLayout.ALIGN_TOP, R.id.iv_voice_speech);
                params.addRule(RelativeLayout.ALIGN_LEFT, R.id.iv_voice_speech);
                params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.iv_voice_speech);
                voiceView.setLayoutParams(params);
                voiceView.requestLayout();
                isPlaying = false;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    //kill recognizer on destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recognizer != null) {
            recognizer.stopListening();
            recognizer.destroy();
        }
    }
}