package org.benzkuai.yijing;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-2-5
 * Time: 下午3:11
 * To change this template use File | Settings | File Templates.
 */
public class BookService extends Service {

    private WindowManager m_windowManager = null;
    private View m_viewClock = null;
    private TextView m_textViewClock = null;
    private int m_autoTime = 15;
    private int m_second = 15;

    private static final String XQJ_SP_FILE = "xqj_sp_file";
    private static final String KEY_SETTING_AUTO_TIME = "key_setting_auto_time";


    /**
     * 同activity一样要manifest文件中声明
     */
    public BookService() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onCreate() {
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.

        // view
        m_viewClock = LayoutInflater.from(this).inflate(R.layout.clock, null);
        m_windowManager = (WindowManager)getSystemService("window");
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        wmParams.type = 2002;
        wmParams.flags |= 8;
        wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP; // 调整悬浮窗口至左上角
        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.format = 1;

        // 增加容器到system是需要权限的（权限：SYSTEM_ALERT_WINDOW），否则报异常
        m_windowManager.addView(m_viewClock, wmParams);

        // 读出秒数
        SharedPreferences sp = getSharedPreferences(XQJ_SP_FILE, Context.MODE_PRIVATE);
        m_autoTime = sp.getInt(KEY_SETTING_AUTO_TIME, 15); //默认15s

        // 秒表框
        m_second = m_autoTime;
        m_textViewClock = (TextView)m_viewClock.findViewById(R.id.id_textView_clock);
        m_textViewClock.setText(""+m_second);

        // 启动
        enableAuto();

    }

    @Override
    public void onDestroy() {
        disableAuto();
        m_windowManager.removeView(m_viewClock);
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updateTime()
    {
        if (m_second <= 5)
        {
            m_textViewClock.setTextColor(0xffff0000);
        }
        else
        {
            m_textViewClock.setTextColor(0xffffffff);
        }
        m_textViewClock.setText(m_second+"");
    }

    /**
     * 自动翻页秒表
     */
    private Handler m_handlerAuto = new Handler();
    private Runnable m_runnableAuto = new Runnable()
    {
        @Override
        public void run()
        {
            try {
                m_second--;
                if (m_second < 1)
                {
                    m_second = m_autoTime;
                }
                updateTime();

                enableAuto();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    } ;

    public void enableAuto()
    {
        m_handlerAuto.postDelayed(m_runnableAuto, 1000);// 自动翻页秒表
    }

    public void disableAuto()
    {
        m_handlerAuto.removeCallbacks(m_runnableAuto);
    }
}