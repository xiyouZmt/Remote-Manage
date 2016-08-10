package com.example.manager.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manager.Activity.DiskActivity;
import com.example.manager.Activity.HomeActivity;
import com.example.manager.Activity.MouseActivity;
import com.example.manager.Application.App;
import com.example.manager.R;
import com.example.manager.ResideMenu.ResideMenu;
import com.example.manager.SpeechRecognize.Speech;
import com.example.manager.Thread.SendCommand;

public class ToolsFragment extends Fragment {

    private App app;
    private View view;
    private View searchWindow;
    private View volume_brightness;
    private PopupWindow popupWindow;
    private EditText keyWords;
    private LinearLayout menu;
    private LinearLayout linear_power;
    private LinearLayout linear_volume;
    private LinearLayout linear_brightness;
    private LinearLayout linear_mouse;
    private LinearLayout linear_speech;
    private LinearLayout linear_computer;
    private LinearLayout linear_tools;
    private LinearLayout linear_search;
    private LinearLayout linear_screen;
    private Button connect;
    private int pos = 0;
    private String power;
    private String type ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_tools, null);
        initViews();
        setListener();

        return view;
    }

    public class ToolsListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            app = (App) getActivity().getApplication();
            if( (v.getId() != R.id.menu && v.getId() != R.id.connect)
                    && !app.getUser().connected){
                Toast.makeText(getActivity(), "设备未连接，请先连接设备", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent();
            switch (v.getId()){
                case R.id.menu :
                    HomeActivity.resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                    break;
                case R.id.linear_power :
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("电源").setSingleChoiceItems(R.array.power, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pos = which;
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (pos){
                                case 0 :
                                    power = "shutdown -s";
                                    break;
                                case 1 :
                                    power = "shutdown -h";
                                    break;
                                case 2 :
                                    power = "shutdown -r";
                                    break;
                            }
                            dialog.dismiss();
                            AlertDialog.Builder dialog1 = new AlertDialog.Builder(getActivity());
                            dialog1.setTitle("提示").setMessage("确定执行此操作?").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e("error", String.valueOf(app.getUser().connected));
                                    String data = "{'command':'power','type':'" + power + "'}";
                                    SendCommand ct = new SendCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
                                    Thread t = new Thread(ct, "SendCommand");
                                    t.start();
                                }
                            }).create().show();
                        }
                    }).create().show();
                    pos = 0;
                    break;
                case R.id.linear_volume :
                    type = "volume";
                    popupWindow = new PopupWindow(volume_brightness, dpToPx(300), dpToPx(50), true);
                    popupWindow.setBackgroundDrawable(new ColorDrawable()); //也可为0x00000000，完全透明
                    popupWindow.showAtLocation(volume_brightness, Gravity.CENTER,0,0);
                    break;
                case R.id.linear_brightness :
                    type = "brightness";
                    popupWindow = new PopupWindow(volume_brightness, dpToPx(300), dpToPx(50), true);
                    popupWindow.setBackgroundDrawable(new ColorDrawable()); //也可为0x00000000，完全透明
                    popupWindow.showAtLocation(volume_brightness, Gravity.CENTER,0,0);
                    break;
                case R.id.linear_mouse :
                    intent.setClass(getActivity(), MouseActivity.class);
                    startActivity(intent);
                    break;
                case R.id.linear_speech :
                    Speech speech = new Speech(getActivity(), app);
                    speech.GetWordFromVoice();
//                    VoiceToWord voiceToWord = new VoiceToWord(getActivity(), app.getUser().appId);
//                    voiceToWord.GetWordFromVoice();

                    break;
                case R.id.linear_computer :
                    String data = "{\"command\":\"driver\",\"operation\":\"getDisk\"}";
                    SendCommand driverThread = new SendCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
                    Thread driver = new Thread(driverThread, "SendCommand");
                    driver.start();
                    intent.setClass(getActivity(), DiskActivity.class);
                    startActivity(intent);
                    break;
                case R.id.linear_tools :
                    AlertDialog.Builder dialog2 = new AlertDialog.Builder(getActivity());
                    dialog2.setTitle("快捷工具").setSingleChoiceItems(R.array.tools, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pos = which;
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String command = null;
                            switch (pos){
                                case 0 :
                                    command = "explorer";
                                    break;
                                case 1 :
                                    command = "cmd /c start taskmgr" ;
                                    break;
                                case 2 :
                                    command = "cmd /c start";
                                    break;
                                case 3 :
                                    command = "cmd /c start control";
                                    break;
                                case 4 :
                                    command = "notepad";
                                    break;
                                case 5 :
                                    command = "calc" ;
                                    break;
                                case 6 :
                                    command = "dvdplay Windows";
                                    break;
                                case 7 :
                                    command = "write";
                                    break;
                                case 8 :
                                    command = "mspaint";
                                    break;
                                case 9 :
                                    command = "cmd /c start start www.baidu.com";
                                    break;
                            }
                            String data  = "{\"command\":\"tools\",\"type\":\"" + command + "\"}";
                            SendCommand cmd = new SendCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
                            Thread t_cmd = new Thread(cmd, "SendCommand");
                            t_cmd.start();
                            pos = 0;
                        }
                    }).create().show();
                    break;
                case R.id.linear_search :
                    popupWindow = new PopupWindow(searchWindow, dpToPx(300), dpToPx(110), true);
                    popupWindow.setBackgroundDrawable(new ColorDrawable()); //也可为0x00000000，完全透明
                    popupWindow.showAtLocation(searchWindow, Gravity.CENTER, 0, 0);
                    break;
                case R.id.linear_screen :
                    data  = "{\"command\":\"screenShot\"}";
                    SendCommand screenThread = new SendCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
                    Thread screen = new Thread(screenThread, "SendCommand");
                    screen.start();
                    Toast.makeText(getActivity(), "截屏成功，已保存至E:/QuickSend", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.connect :
                    if(app.getUser().connected){
                        Toast.makeText(getActivity(), "设备已连接!", Toast.LENGTH_SHORT).show();
                    } else {
                        HomeActivity.viewPager.setCurrentItem(2);
                    }
                    break;
                case R.id.cancel :
                    popupWindow.dismiss();
                    break;
                case R.id.submit :
                    if(!keyWords.getText().toString().equals("")) {
                        data = "{\"command\":\"tools\",\"type\":\"cmd /c start start www.baidu.com/s?wd=" + keyWords.getText().toString() + "\"}";
                        SendCommand cmd = new SendCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
                        Thread t_cmd = new Thread(cmd, "SendCommand");
                        t_cmd.start();
                    }
                    break;
            }
        }
    }

    public class SeekBarListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(type.equals("volume")){
                String data = "{\"command\":\"volume\",\"type\":\"" + seekBar.getProgress() + "\"}";
                SendCommand ct = new SendCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
                Thread t = new Thread(ct, "SendCommand");
                t.start();
            } else {
                String data = "{\"command\":\"brightness\",\"type\":\"" + seekBar.getProgress() + "\"}";
                SendCommand ct = new SendCommand(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
                Thread t = new Thread(ct, "SendCommand");
                t.start();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    public int dpToPx(float dp){
        float px = getResources().getDisplayMetrics().density;
        return (int)(dp * px + 0.5f);
    }

    public void setListener(){
        menu.setOnClickListener(new ToolsListener());
        linear_power.setOnClickListener(new ToolsListener());
        linear_volume.setOnClickListener(new ToolsListener());
        linear_brightness.setOnClickListener(new ToolsListener());
        linear_mouse.setOnClickListener(new ToolsListener());
        linear_speech.setOnClickListener(new ToolsListener());
        linear_computer.setOnClickListener(new ToolsListener());
        linear_tools.setOnClickListener(new ToolsListener());
        linear_search.setOnClickListener(new ToolsListener());
        linear_screen.setOnClickListener(new ToolsListener());
        connect.setOnClickListener(new ToolsListener());
    }

    public void initViews(){
        menu = (LinearLayout) view.findViewById(R.id.menu);
        LinearLayout back = (LinearLayout) view.findViewById(R.id.back);
        TextView title = (TextView) view.findViewById(R.id.fileName);
        LinearLayout search = (LinearLayout) view.findViewById(R.id.search);
        search.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        menu.setVisibility(View.VISIBLE);
        title.setText("实用工具");
        linear_power = (LinearLayout) view.findViewById(R.id.linear_power);
        linear_volume = (LinearLayout) view.findViewById(R.id.linear_volume);
        linear_brightness = (LinearLayout) view.findViewById(R.id.linear_brightness);
        linear_mouse = (LinearLayout) view.findViewById(R.id.linear_mouse);
        linear_speech = (LinearLayout) view.findViewById(R.id.linear_speech);
        linear_computer = (LinearLayout) view.findViewById(R.id.linear_computer);
        linear_tools = (LinearLayout) view.findViewById(R.id.linear_tools);
        linear_search = (LinearLayout) view.findViewById(R.id.linear_search);
        linear_screen = (LinearLayout) view.findViewById(R.id.linear_screen);
        connect = (Button) view.findViewById(R.id.connect);
        searchWindow = getActivity().getLayoutInflater().inflate(R.layout.search_window, null);
        volume_brightness = getActivity().getLayoutInflater().inflate(R.layout.volume_brightness, null);
        SeekBar seekBar = (SeekBar) volume_brightness.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBarListener());
        Button cancel = (Button) searchWindow.findViewById(R.id.cancel);
        Button submit = (Button) searchWindow.findViewById(R.id.submit);
        keyWords = (EditText) searchWindow.findViewById(R.id.keyWords);
        cancel.setOnClickListener(new ToolsListener());
        submit.setOnClickListener(new ToolsListener());
    }

}
