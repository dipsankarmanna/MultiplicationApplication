package com.example.multiplication_application;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

import com.example.multiplication_application.databinding.ActivityMainBinding;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    TextToSpeech textToSpeech;
    String value = "";
    int min = 1, max = 10;
    Random random1, random2;
    int showMe1, showMe2;
    int k = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        listen(k);

    }

    private void listen(int j) {
        if (k <= 10) {
            binding.result.setText("");
            textToSpeech = new TextToSpeech(getApplicationContext(),
                    new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if (i == TextToSpeech.SUCCESS) {
                                int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                                random1 = new Random();
                                random2 = new Random();
                                showMe1 = min + random1.nextInt(max);
                                showMe2 = min + random2.nextInt(max);
                                binding.inputText.setTranslationX(800);
                                binding.inputText.setAlpha(1);
                                binding.inputText.animate().translationX(0).alpha(1).setDuration(450).setStartDelay(400).start();
                                binding.result.setTranslationY(800);
                                binding.result.setAlpha(1);
                                binding.ques.setText("Question No " + k);
                                binding.inputText.setText(showMe1 + " * " + showMe2);
                                res(false);
                                textToSpeech.speak("Question is " + showMe1 + " multiply by " + showMe2, TextToSpeech.QUEUE_FLUSH, null);
                                binding.result.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(600).start();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        SpeakNow();
                                        //Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
                                    }
                                }, 2500);
                            }
                        }
                    });
        }
    }

    private void SpeakNow() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell Your Answer...");
        res(true);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            value = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            res(false);
            binding.result.setVisibility(View.VISIBLE);
            binding.result.setText("Your Answer is " + value);
            int ans = showMe1 * showMe2;
            if (value.equals(Integer.toString(ans))) {
                textToSpeech.speak("Your Answer is correct", TextToSpeech.QUEUE_FLUSH, null);
                //binding.result.setText("");
            } else {
                textToSpeech.speak("Your Answer is wrong", TextToSpeech.QUEUE_FLUSH, null);
                //listen(k++);
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                        listen(k++);
                }
            }, 2300);
        }
    }
    private void res(Boolean val){

        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, val);
        amanager.setStreamMute(AudioManager.STREAM_ALARM, val);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, val);
        amanager.setStreamMute(AudioManager.STREAM_RING, val);
        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, val);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}