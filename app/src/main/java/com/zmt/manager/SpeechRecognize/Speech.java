package com.zmt.manager.SpeechRecognize;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.zmt.manager.Application.App;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

/**
 * Created by Dangelo on 2016/6/1.
 */
public class Speech {

    private Context context;
    private RecognizerDialog dialog;
    private com.iflytek.cloud.speech.SpeechRecognizer speechRecognizer;
    private SharedPreferences sharedPreferences;
    private RecognizerDialogListener recognizerDialogListener;

    public Speech(Context context, App app) {
        SpeechUser.getUser().login(context, null, null, "appid=" + app.getUser().appId, new Listener());
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        dialog = new RecognizerDialog(context);
        Log.d("dialog", "successfully");
        this.context = context;
    }

    public class Listener implements SpeechListener{

        @Override
        public void onEvent(int i, Bundle bundle) {

        }

        @Override
        public void onData(byte[] bytes) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {
            if(speechError != null){
                Log.d("login", "Successfully login");
            }
        }
    }

    public void GetWordFromVoice(){
        boolean isShowDialog = sharedPreferences.getBoolean("iat_show", true);
        if(isShowDialog){
            showDialog();
        } else if(speechRecognizer == null){
            speechRecognizer = com.iflytek.cloud.speech.SpeechRecognizer.createRecognizer(context);
            if(speechRecognizer.isListening()){
                speechRecognizer.stopListening();
            }
        }
    }

    public void showDialog(){
        if(dialog == null) {
            /**
             * 初始化听写Dialog
             */
            dialog = new RecognizerDialog(context);
        }
        /**
         * 获取引擎参数
         */
        String engine = sharedPreferences.getString("iat_engine", "iat");
        /**
         * 清空Grammar_ID，防止识别后进行听写时Grammar_ID的干扰
         */
        dialog.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
        /**
         * 设置听写Dialog的引擎
         */
        dialog.setParameter(SpeechConstant.DOMAIN, engine);
        /**
         * 设置采样率参数，支持8K和16K
         */
        String rate = sharedPreferences.getString("sf", "sf");
        if(rate.equals("rate8k")) {
            dialog.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        } else {
            dialog.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
        }
        if(recognizerDialogListener == null) {
            recognizerDialogListener = new MyRecognizerDialogLister(context);
        }
        /**
         * 显示听写对话框
         */
        dialog.setListener(recognizerDialogListener);
        dialog.show();
    }
}
