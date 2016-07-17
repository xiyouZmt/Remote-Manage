package com.example.manager.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manager.Application.App;
import com.example.manager.R;
import com.example.manager.Thread.CommandThread;

public class MouseActivity extends Activity {

    private App app;
    private TextView textView;
    private LinearLayout back;
    private LinearLayout up;
    private LinearLayout left;
    private LinearLayout down;
    private LinearLayout right;
    private LinearLayout home;
    private LinearLayout end;
    private Button leftClick;
    private Button rightClick;
    private GestureDetector gestureDetector;
    private long doubleClick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mouse);
        initViews();
        setListener();
    }

    public class MouseListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String data;
            switch (v.getId()){
                case R.id.back :
                    finish();
                    break;
                case R.id.up :
                    data = "{'command':'mouse','type':'up'}";
                    click(data);
                    break;
                case R.id.left :
                    data = "{'command':'mouse','type':'left'}";
                    click(data);
                    break;
                case R.id.down :
                    data = "{'command':'mouse','type':'down'}";
                    click(data);
                    break;
                case R.id.right :
                    data = "{'command':'mouse','type':'right'}";
                    click(data);
                    break;
                case R.id.home :
                    data = "{'command':'mouse','type':'home'}";
                    click(data);
                    break;
                case R.id.end :
                    data = "{'command':'mouse','type':'end'}";
                    click(data);
                    break;
                case R.id.leftClick :
                    data = "{'command':'mouse','type':'singleClick'}";
                    click(data);
                    break;
                case R.id.rightClick :
                    data = "{'command':'mouse','type':'rightClick'}";
                    click(data);
                    break;
            }
        }
    }

    public class TouchListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            return true;
        }
    }

    /**
     *      监测手势, 触发顺序：
     * onDown->onShowPress->onLongPress
     *
     * 点击一下非常快的（不滑动）Touchup：
     * onDown->onSingleTapUp->onSingleTapConfirmed
     * 点击一下稍微慢点的（不滑动）Touchup：
     * onDown->onShowPress->onSingleTapUp->onSingleTapConfirmed
     *
     * 滑屏：手指触动屏幕后，稍微滑动后立即松开
     * onDown-----》onScroll----》onScroll----》onScroll----》………----->onFling
     * 拖动
     * onDown------》onScroll----》onScroll------》onFiling
     * 可见，无论是滑屏，还是拖动，影响的只是中间OnScroll触发的数量多少而已，最终都会触发onFling事件！
     */
    public class GestureListener implements GestureDetector.OnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            /**
             * OnDown(MotionEvent e)：用户按下屏幕就会触发；
             */
            Toast.makeText(MouseActivity.this, "onDown", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            /**
             * onShowPress(MotionEvent e)：如果是按下的时间超过瞬间，
             * 而且在按下的时候没有松开或者是拖动的，那么onShowPress就会执行
             */
            Toast.makeText(MouseActivity.this, "onShowPress", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            /**
             * onSingleTapUp(MotionEvent e)：从名子也可以看出,一次单独的轻击抬起操作,也就是轻击一下屏幕，
             * 立刻抬起来，才会有这个触发，当然,如果除了Down以外还有其它操作,那就不再算是Single操作了,所以也就不会触发这个事件
             */
            String data = "{'command':'mouse','type':'singleClick'}";
            click(data);
            Toast.makeText(MouseActivity.this, "onSingleTapUp", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            /**
             * onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY)：在屏幕上拖动事件。
             * 无论是用手拖动view，或者是以抛的动作滚动，都会多次触发这个方法  在ACTION_MOVE动作发生时就会触发
             */
            int x = (int) distanceX;
            int y = (int) distanceY;
            String data = "{'command':'mouse','type':'move','width':'" + x + "','height':'" + y + "'}";
            CommandThread ct = new CommandThread(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
            Thread t = new Thread(ct,"CommandThread");
            t.start();
            Toast.makeText(MouseActivity.this, "onScroll", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            /**
             * onLongPress(MotionEvent e)：长按触摸屏，超过一定时长，就会触发这个事件
             */
            Toast.makeText(MouseActivity.this, "onLongPress", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            /**
             * onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) ：
             * 触发条件 ：X轴的坐标位移大于100，且移动速度大于200个像素/秒
             * 滑屏，用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
             * 参数解释：
             * e1：第1个ACTION_DOWN MotionEvent
             * e2：最后一个ACTION_MOVE MotionEvent
             * velocityX：X轴上的移动速度，像素/秒
             * velocityY：Y轴上的移动速度，像素/秒
             */
            Toast.makeText(MouseActivity.this, "onFling", Toast.LENGTH_SHORT).show();
            return e1.getX() - e2.getX() > 100 && Math.abs(velocityX) > 200;
        }
    }

    public void click(String data){
        CommandThread ct = new CommandThread(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
        Thread t = new Thread(ct, "CommandThread");
        t.start();
        if(System.currentTimeMillis() - doubleClick > 100){
            doubleClick = System.currentTimeMillis();
        } else {
            data = "{'command':'mouse','type':'doubleClick'}";
            ct = new CommandThread(app.getUser().socket, app.getUser().IP, app.getUser().port, data);
            t = new Thread(ct, "CommandThread");
            t.start();
            doubleClick = 0;
        }
    }

    public void setListener(){
        back.setOnClickListener(new MouseListener());
        textView.setOnTouchListener(new TouchListener());
        up.setOnClickListener(new MouseListener());
        left.setOnClickListener(new MouseListener());
        down.setOnClickListener(new MouseListener());
        right.setOnClickListener(new MouseListener());
        leftClick.setOnClickListener(new MouseListener());
        rightClick.setOnClickListener(new MouseListener());
        home.setOnClickListener(new MouseListener());
        end.setOnClickListener(new MouseListener());
    }

    public void initViews(){
        textView = (TextView) findViewById(R.id.touchMod);
        TextView title = (TextView) findViewById(R.id.fileName);
        LinearLayout search = (LinearLayout) findViewById(R.id.search);
        back = (LinearLayout) findViewById(R.id.back);
        up = (LinearLayout) findViewById(R.id.up);
        left = (LinearLayout) findViewById(R.id.left);
        down = (LinearLayout) findViewById(R.id.down);
        right = (LinearLayout) findViewById(R.id.right);
        home = (LinearLayout) findViewById(R.id.home);
        end = (LinearLayout) findViewById(R.id.end);
        leftClick = (Button) findViewById(R.id.leftClick);
        rightClick = (Button) findViewById(R.id.rightClick);
        search.setVisibility(View.GONE);
        title.setText("鼠标");
        gestureDetector = new GestureDetector(this, new GestureListener());
        app = (App) getApplication();
    }

}
