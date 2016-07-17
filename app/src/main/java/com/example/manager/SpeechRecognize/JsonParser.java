package com.example.manager.SpeechRecognize;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.text.TextUtils;

public class JsonParser {

	public static String parseIatResult(String json) {
		if(TextUtils.isEmpty(json))
			return "";
		StringBuilder ret = new StringBuilder();
		try {
			JSONTokener jsonTokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(jsonTokener);
			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				JSONObject obj = items.getJSONObject(0);
				ret.append(obj.getString("w"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return ret.toString();
	}
}
