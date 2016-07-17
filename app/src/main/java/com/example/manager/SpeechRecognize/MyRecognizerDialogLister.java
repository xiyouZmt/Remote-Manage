package com.example.manager.SpeechRecognize;

import android.content.Context;
import android.widget.Toast;

import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class MyRecognizerDialogLister implements RecognizerDialogListener{

	private Context context;

	public MyRecognizerDialogLister(Context context) {
		this.context = context;
	}

	@Override
	public void onResult(RecognizerResult results, boolean isLast) {
		String text = JsonParser.parseIatResult(results.getResultString());
		System.out.println(text);
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onError(SpeechError error) {
		int errorCoder = error.getErrorCode();
		switch (errorCoder) {
		case 10118:
			System.out.println("user don't speak anything");
			break;
		case 10204:
			System.out.println("can't connect to internet");
			break;
		default:
			break;
		}
	}
}
