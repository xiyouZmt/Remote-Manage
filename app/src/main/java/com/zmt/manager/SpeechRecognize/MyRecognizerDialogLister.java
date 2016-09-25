package com.zmt.manager.SpeechRecognize;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.zmt.manager.Application.App;
import com.zmt.manager.Thread.SendCommand;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class MyRecognizerDialogLister implements RecognizerDialogListener{

	private App app;
	private Context context;

	public MyRecognizerDialogLister(Context context) {
		this.context = context;
		app = (App)((FragmentActivity)context).getApplication();
	}

	@Override
	public void onResult(RecognizerResult results, boolean isLast) {
		String text = JsonParser.parseIatResult(results.getResultString());
		System.out.println(text);
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		String type = "";
		if(text.contains("关机")){
			type = "shutdown -s";
		} else if(text.contains("重启")){
			type = "shutdown -r";
		} else if(text.contains("睡眠")){
			type = "shutdown -h";
		} else if(text.contains("资源管理器") || text.contains("我的电脑")){
			type = "explorer";
		} else if(text.contains("任务管理器")){
			type = "cmd /c start taskmgr";
		} else if(text.contains("命令行")){
			type = "cmd /c start";
		} else if(text.contains("控制面板")){
			type = "cmd /c start control";
		} else if(text.contains("记事本")){
			type = "cmd /c start notepad";
		} else if(text.contains("计算器")){
			type = "calc";
		} else if(text.contains("视频")){
			type = "dvdplay Windows";
		} else if(text.contains("音乐")  || text.contains("歌")){
//			type = "cmd /c start \"\" \"E:/song/Avril Lavigne - 17.mp3\"";
			type = "music";
		} else if(text.contains("写字")){
			type = "write";
		} else if(text.contains("画")){
			type = "mspaint";
		} else if(text.contains("搜索")) {
			if(text.indexOf("索") < text.length() - 1){
				String key = text.substring(text.indexOf("索") + 1);
				type = "cmd /c start start www.baidu.com/s?wd=" + key;
			} else {
				type = "cmd /c start start www.baidu.com";
			}
		} else if(text.contains("浏览器")) {
			type = "cmd /c start start www.baidu.com";
		} else if(!text.equals("") && !text.equals("。")){
			type = "cmd /c start start www.baidu.com/s?wd=" + text;
		}
		if(!type.equals("")){
			String data = "{'command':'speech','type':'" + type + "'}";
			SendCommand ct = new SendCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
			Thread t = new Thread(ct, "SendCommand");
			t.start();
		}
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
