package org.anothermonitor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by mileszhou on 16/5/24.
 */
public class ActivityTesting extends Activity {
    private DecimalFormat mFormat = new DecimalFormat("##,###,##0"), mFormatPercent = new DecimalFormat("##0.0"),
            mFormatTime = new DecimalFormat("0.#");
    private boolean cpuTotal, cpuAM,
            memUsed, memAvailable, memFree, cached, threshold,
            settingsShown, canvasLocked, orientationChanged;
    private int intervalRead, intervalUpdate, intervalWidth, statusBarHeight, navigationBarHeight, animDuration=200,
            settingsHeight, orientation, processesMode, graphicMode;
    private SharedPreferences mPrefs;

    private ViewGraphic mVG;
    private ServiceReader mSR;
    private Handler mHandler = new Handler(), mHandlerVG = new Handler();
    private Thread mThread;
    private Runnable drawRunnable = new Runnable() {
        @SuppressWarnings("unchecked")
        @SuppressLint("NewApi")
        @Override
        public void run() {
            mHandler.postDelayed(this, intervalUpdate);
            if (mSR != null) { // finish() could have been called from the BroadcastReceiver
                mHandlerVG.post(drawRunnableGraphic);

                setTextLabelCPU(null, mTVCPUTotalP, mSR.getCPUTotalP());
                if (processesMode == C.processesModeShowCPU)
                    setTextLabelCPU(null, mTVCPUAMP, mSR.getCPUAMP());
                else setTextLabelCPU(null, mTVCPUAMP, null, mSR.getMemoryAM());

                setTextLabelMemory(mTVMemUsed, mTVMemUsedP, mSR.getMemUsed());
                setTextLabelMemory(mTVMemAvailable, mTVMemAvailableP, mSR.getMemAvailable());
                setTextLabelMemory(mTVMemFree, mTVMemFreeP, mSR.getMemFree());
                setTextLabelMemory(mTVCached, mTVCachedP, mSR.getCached());
                setTextLabelMemory(mTVThreshold, mTVThresholdP, mSR.getThreshold());

                for (int n=0; n<mLProcessContainer.getChildCount(); ++n) {
                    LinearLayout l = (LinearLayout) mLProcessContainer.getChildAt(n);
                    setTextLabelCPUProcess(l);
                    setTextLabelMemoryProcesses(l);
                }
            }
        }
    }, drawRunnableGraphic = new Runnable() { // http://stackoverflow.com/questions/18856376/android-why-cant-i-create-a-handler-in-new-thread
        @Override
        public void run() {
            mThread = new Thread() {
                @Override
                public void run() {
                    Canvas canvas = null;
                    if (!canvasLocked) { // http://stackoverflow.com/questions/9792446/android-java-lang-illegalargumentexception
                        canvas = mVG.lockCanvas();
                        if (canvas != null) {
                            canvasLocked = true;
                            mVG.onDrawCustomised(canvas, mThread);

                            // https://github.com/AntonioRedondo/AnotherMonitor/issues/1
                            // http://stackoverflow.com/questions/23893813/canvas-restore-causing-underflow-exception-in-very-rare-cases
                            try {
                                mVG.unlockCanvasAndPost(canvas);
                            } catch (IllegalStateException e) {
                                Log.w("Activity main: ", e.getMessage());
                            }

                            canvasLocked = false;
                        }
                    }
                }
            };
            mThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        mHandler.post(drawRunnable);
        ////


        mVG.setService(mSR);
        mVG.setParameters(cpuTotal, cpuAM, memUsed, memAvailable, memFree, cached, threshold);















        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(WINDOW_SERVICE);
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
    private void setTextLabelCPU(TextView absolute, TextView percent, List<Float> values, @SuppressWarnings("unchecked") List<Integer>... valuesInteger) {
        if (valuesInteger.length == 1) {
            percent.setText(mFormatPercent.format(valuesInteger[0].get(0) * 100 / (float) mSR.getMemTotal()) + C.percent);
            mTVMemoryAM.setVisibility(View.VISIBLE);
            mTVMemoryAM.setText(mFormat.format(valuesInteger[0].get(0)) + C.kB);
        } else if (!values.isEmpty()) {
            percent.setText(mFormatPercent.format(values.get(0)) + C.percent);
            mTVMemoryAM.setVisibility(View.INVISIBLE);
        }
    }
}
