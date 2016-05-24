package org.anothermonitor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by mileszhou on 16/5/24.
 */
public class ActivityTesting extends Activity {
    private DecimalFormat mFormat = new DecimalFormat("##,###,##0"), mFormatPercent = new DecimalFormat("##0.0"),
            mFormatTime = new DecimalFormat("0.#");
    private boolean cpuTotal, cpuAM,
            memUsed, memAvailable, memFree, cached, threshold,
            settingsShown, canvasLocked, orientationChanged;
    private int intervalRead, intervalUpdate, intervalWidth, statusBarHeight, navigationBarHeight, animDuration = 200,
            settingsHeight, orientation, processesMode, graphicMode;
    private SharedPreferences mPrefs;

    private ViewGraphic mVG;
    private ServiceReader mSR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        mVG.setService(mSR);
        mVG.setParameters(cpuTotal, cpuAM, memUsed, memAvailable, memFree, cached, threshold);


        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        // 悬浮所有页面之上
        lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 失去焦点
        lp.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE;
        TextView tv = new TextView(this);

        tv.setText("我是不是你最疼爱的人，你为什么不说话，握住是你冰冷的手动也不动让我好难过");
        tv.setBackgroundColor(Color.alpha(10));
        wm.addView(tv, lp);

    }

}
