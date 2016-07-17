package com.example.manager.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.manager.Fragment.ConnectFragment;
import com.example.manager.Fragment.FileFragment;
import com.example.manager.Fragment.ToolsFragment;
import com.example.manager.R;
import com.example.manager.ResideMenu.ResideMenu;
import com.example.manager.ResideMenu.ResideMenuItem;
import com.example.manager.Utils.ActionBarUtil;
import com.example.manager.ViewPager.MyViewPager;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity{

    public static ResideMenu resideMenu;
    public static MyViewPager viewPager;
    private List<Fragment> fragmentList;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        initView();
        /**
         * 初始化侧边栏
         */
        resideMenu = new ResideMenu(this);
        resideMenu.attachToActivity(this);
        resideMenu.setBackground(R.drawable.user_background);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        String [] title = {"文件管理", "实用工具", "连接设备"};
        int [] image = {R.drawable.files, R.drawable.tools, R.drawable.connect};
        ResideMenuItem[] resideMenuItem = new ResideMenuItem[title.length];
        for(int i = 0; i < title.length; i++){
            resideMenuItem[i] = new ResideMenuItem(this,image[i],title[i]);
            resideMenu.addMenuItem(resideMenuItem[i],ResideMenu.DIRECTION_LEFT);
            resideMenuItem[i].setTag(i);
            resideMenuItem[i].setOnClickListener(new HomeListener());
        }
        FileFragment fileFragment = new FileFragment();
        ToolsFragment toolsFragment = new ToolsFragment();
        ConnectFragment connectFragment = new ConnectFragment();

        fragmentList.add(fileFragment);
        fragmentList.add(toolsFragment);
        fragmentList.add(connectFragment);

        /**
         * 初始化viewPager
         */
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public Fragment getItem(int i) {
                return fragmentList.get(i);
            }
        };
        viewPager.setAdapter(adapter);
    }

    public class HomeListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch ((Integer)v.getTag()){
                case 0 :
                    viewPager.setCurrentItem(0);
                    break;
                case 1 :
                    viewPager.setCurrentItem(1);
                    break;
                case 2 :
                    viewPager.setCurrentItem(2);
                    break;
            }
            resideMenu.closeMenu();
        }
    }

    public void initView(){
        viewPager = (MyViewPager) findViewById(R.id.viewPager);
        fragmentList = new ArrayList<>();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(resideMenu.isOpened()){
                resideMenu.closeMenu();
                return true;
            } else {
                if (System.currentTimeMillis() - exitTime > 2000) {
                    Toast.makeText(HomeActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
            }
        }
        return true;
    }
}
