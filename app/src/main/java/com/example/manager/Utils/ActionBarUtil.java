package com.example.manager.Utils;

import android.app.ActionBar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.manager.R;

/**
 * Created by Dangelo on 2016/4/3.
 */
public class ActionBarUtil {
    public static void initActionBar(ActionBar actionBar, String title, int current){
        actionBar.setCustomView(R.layout.actionbar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        LinearLayout menu = (LinearLayout) actionBar.getCustomView().findViewById(R.id.menu);
        LinearLayout back = (LinearLayout) actionBar.getCustomView().findViewById(R.id.back);
        TextView fileName = (TextView) actionBar.getCustomView().findViewById(R.id.fileName);
        LinearLayout search = (LinearLayout) actionBar.getCustomView().findViewById(R.id.search);
        if(current == 0x111){
            fileName.setText(title);
            menu.setVisibility(View.VISIBLE);
            back.setVisibility(View.INVISIBLE);
            search.setVisibility(View.INVISIBLE);
        } else if (current == 0x222){
            fileName.setText(title);
        } else if(current == 0x333){
            search.setVisibility(View.INVISIBLE);
            back.setVisibility(View.INVISIBLE);
            fileName.setVisibility(View.INVISIBLE);
        } else {
            fileName.setText(title);
        }
    }
}
