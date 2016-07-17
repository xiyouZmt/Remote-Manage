package com.example.manager.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manager.Activity.HomeActivity;
import com.example.manager.Application.App;
import com.example.manager.CheckBox.SmoothCheckBox;
import com.example.manager.R;
import com.example.manager.ResideMenu.ResideMenu;
import com.example.manager.Thread.SocketThread;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectFragment extends Fragment {

    private App app;
    private View view;
    private LinearLayout menu;
    private EditText count;
    private Button connect;
    private SmoothCheckBox smoothCheckBox;
    public  static ConnectHandler connectHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_connect,null);
        initViews();
        setListener();

        return view;
    }

    public class ConnectListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.menu :
                    HomeActivity.resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                    break;
                case R.id.connect :
                    if(count.getText().toString().equals("")){
                        Toast.makeText(getActivity(), "请输入IP地址", Toast.LENGTH_SHORT).show();
                    } else {
                        app.getUser().IP = count.getText().toString();
                        SocketThread st = new SocketThread(app.getUser().socket, count.getText().toString(), app.getUser().port);
                        Thread t = new Thread(st, "SocketThread");
                        t.start();
                    }
                    break;
            }
        }
    }

    public class ConnectHandler extends Handler{
        public void handleMessage(Message msg){
            App app = (App) getActivity().getApplication();
            switch (msg.what){
                case 0x000 :
                    app.getUser().connected = true;
                    Toast.makeText(getActivity(), "连接成功!", Toast.LENGTH_SHORT).show();
                    break;
                case 0x111 :
                    app.getUser().connected = false;
                    Toast.makeText(getActivity(), "连接失败，请重新连接", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public boolean checkIP(String IP){
        String str =  "([1-9]|[1-9]//d|1//d{2}|2[0-4]//d|25[0-5])(//.(//d|[1-9]//d|1//d{2}|2[0-4]//d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(str);
        Matcher matcher = pattern.matcher(IP);
        return matcher.matches();
    }

    public void setListener(){
        menu.setOnClickListener(new ConnectListener());
        connect.setOnClickListener(new ConnectListener());
    }

    public void initViews(){
        menu = (LinearLayout) view.findViewById(R.id.menu);
        LinearLayout back = (LinearLayout) view.findViewById(R.id.back);
        TextView title = (TextView) view.findViewById(R.id.fileName);
        LinearLayout search = (LinearLayout) view.findViewById(R.id.search);
        search.setVisibility(View.GONE);
        back.setVisibility(View.GONE);
        menu.setVisibility(View.VISIBLE);
        title.setText("连接设备");
        count = (EditText) view.findViewById(R.id.count);
        smoothCheckBox = (SmoothCheckBox) view.findViewById(R.id.SmoothCheckBox);
        connect = (Button) view.findViewById(R.id.connect);
        connectHandler = new ConnectHandler();
        app = (App)getActivity().getApplication();
    }

}
