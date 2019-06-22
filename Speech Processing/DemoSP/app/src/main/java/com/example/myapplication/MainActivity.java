package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class MainActivity extends AppCompatActivity implements RecognitionListener, View.OnClickListener {

    /* Keyword we are looking for to activate recognition */
    private static final String KWS_SEARCH = "keywords";

    /* We only need the keyphrase to start recognition */
    private static final String KEYPHRASE = "moojt";

    private static final String GRAMMAR_SEARCH = "tuvung";

    private static final String[] searchArray = {   "khoong",
                                                    "moojt",
                                                    "hai",
                                                    "ba",
                                                    "boosn",
                                                    "nawm",
                                                    "sasu",
                                                    "bary",
                                                    "tasm",
                                                    "chisn",
                                                    "muwowfi",
                                                    "coojng",
                                                    "truwf",
                                                    "nhaan",
                                                    "chia",
                                                    "chim",
                                                    "chos",
                                                    "mefo",
                                                    "gaf",
                                                    "chuoojt",
                                                    "heo"   };

    /* Recognition object */
    private SpeechRecognizer recognizer;

    TextView textView, title;
    Button startKwsSearch, startGrammaSearch, btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},1);
        } else {
            doSetupTask();
        }

        title = findViewById(R.id.title);
        textView = findViewById(R.id.textView);
        startKwsSearch = findViewById(R.id.startKwsSearch);
        startGrammaSearch = findViewById(R.id.startGrammaSearch);
        btnStop = findViewById(R.id.buttonStop);

        startKwsSearch.setOnClickListener(this);
        startGrammaSearch.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.startKwsSearch:
                doStopListening();
                Random random = new Random();
                int index = random.nextInt(searchArray.length);
                String searchText = searchArray[index];

                title.setText("Try to speech: " + convertToVietnamese(searchText).toUpperCase());
                recognizer.addKeyphraseSearch(KWS_SEARCH, searchText);
                doStartListening(KWS_SEARCH);
                break;

            case R.id.startGrammaSearch:
                doStopListening();
                title.setText("Try to speech any thing !");
                doStartListening(GRAMMAR_SEARCH);
                break;

            case R.id.buttonStop:
                doStopListening();
                break;
        }
    }

    private void doStopListening(){
        recognizer.stop();
    }

    private void doStartListening(String searchName) {
        recognizer.stop();
        textView.setText("");
        recognizer.startListening(searchName);

        /*
            int timeout = 10000;
            recognizer.startListening(searchName, timeout);
        */
    }

    @SuppressLint("StaticFieldLeak")
    private void doSetupTask(){
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            ProgressDialog dialog = new ProgressDialog(MainActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("Please wait...");
                dialog.show();
            }

            private void setupRecognizer(File assetsDir) throws IOException {
                recognizer = SpeechRecognizerSetup.defaultSetup()
                        .setAcousticModel(new File(assetsDir, "tuvung"))
                        .setDictionary(new File(assetsDir, "tuvung.dic"))
                        .setKeywordThreshold(1f)
                        // Disable this line if you don't want recognizer to save raw
                        // audio files to app's storage
                        //.setRawLogDir(assetsDir)
                        .getRecognizer();
                recognizer.addListener(MainActivity.this);

                // Create keyword-activation search.
                recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

//                // Create grammar-based search for "tuvung" recognition
                File digitsGrammar = new File(assetsDir, "tuvung.gram");
                recognizer.addGrammarSearch(GRAMMAR_SEARCH, digitsGrammar);

            }

            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(MainActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }
            @Override
            protected void onPostExecute(Exception result) {
                dialog.dismiss();

                if (result != null) {
                    Log.d("TAG", "SpeechRecognizer.failed(): " + result.getMessage());
                }
            }
        }.execute();
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doSetupTask();
            } else {
                finish();
            }
        }
    }


    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
        //
    }


    private String convertToVietnamese(String phones){
        String vietnameseText = "";
        vietnameseText = phones.replace("oojt" , "ột")
                                .replace("oosn" , "ốn")
                                .replace("awm" , "ăm")
                                .replace("asu" , "áu")
                                .replace("ary" , "ảy")
                                .replace("asm" , "ám")
                                .replace("isn" , "ín")
                                .replace("uwowfi" , "uời")
                                .replace("af" , "à")
                                .replace("efo" , "èo")
                                .replace("uoojt" , "uột")
                                .replace("os" , "ó")
                                .replace("oojng" , "ộng")
                                .replace("uwf" , "ừ")
                                .replace("aan" , "ân");
        return vietnameseText;
    }

    /*
        It will update the current hypothesis
    */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if(hypothesis == null)
            return;

        String text = convertToVietnamese(hypothesis.getHypstr()).toUpperCase();
        Log.d("TAG", "MainActivity.onPartialResult(): " +  text);
        textView.setText(text);
    }


    /*
        This method will fire when stop the recognizer
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = convertToVietnamese(hypothesis.getHypstr()).toUpperCase();
            Log.d("TAG", "MainActivity.onResult(): " +  text);
//          textView.setText(text);
        }
    }


    @Override
    public void onError(Exception e) {
        Log.d( "TAG" , "SpeechRecognizer.failed(): " +  e.getMessage());
    }


    @Override
    public void onTimeout() {
        //
    }
}
