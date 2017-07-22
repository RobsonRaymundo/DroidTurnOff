package com.droid.ray.droidturnoff;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;


public class DroidHeadService extends Service {
    private WindowManager windowManager;
    private ImageView chatHead;

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private int orientationEvent;
    private Context context;
    private AsyncTask asyncTask;
    private View.OnTouchListener onTouchListener;
    private String chamadaPorComandoTexto;
    private SensorManager sensorManager;
    public static boolean closeSensorProximity;
    public static boolean openSensorProximity;
    public static boolean currentCloseSensorProximity;

    private boolean necessarioComandoDepoisDoInit = false;
    private boolean aceitaComandoPorVoz;
    private SensorEventListener sensorEventListener;
    private SpeechRecognizer stt;
    private Intent mIntentRecognizer;
    private Intent mIntentService;


    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;


    public enum EnumStateButton {
        CLOSE,
        VIEW
    }

    private EnumStateButton StateButton;


    OrientationEventListener myOrientationEventListener;

    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    private void TimeSleep(Integer seg) {
        try {
            Thread.sleep(seg);
        } catch (Exception ex) {
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mIntentService = intent;


    }

    @Override
    public void onCreate() {

        super.onCreate();
        InicializarVariavel();
        InicializarAcao();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //call widget update methods/services/broadcasts

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void InicializarVariavel() {
        context = getBaseContext();

        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        onTouchListener = new TouchListener();

        chatHead = new ImageView(context);
        chatHead.setImageResource(R.mipmap.viewrec);
        StateButton = EnumStateButton.VIEW;
        params.gravity = Gravity.CENTER;
        windowManager.addView(chatHead, params);
    }

    private void InicializarAcao() {
        chatHead.setOnTouchListener(onTouchListener);

        myOrientationEventListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int arg0) {
                // TODO Auto-generated method stub
                orientationEvent = arg0;
            }
        };


    }


    public class TouchListener implements View.OnTouchListener {

        private GestureDetector gestureDetector = new GestureDetector(DroidHeadService.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (StateButton == EnumStateButton.VIEW) {
                    chatHead.setImageResource(R.mipmap.closerec);
                    StateButton = EnumStateButton.CLOSE;
                }
                else
                {
                    chatHead.setImageResource(R.mipmap.viewrec);
                    StateButton = EnumStateButton.VIEW;
                }
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (StateButton == EnumStateButton.VIEW) {
                    turnOffScreen(context);
                }
                else
                {
                    context.stopService(mIntentService);

                }
                return super.onSingleTapConfirmed(e);
            }
        });

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    if (myOrientationEventListener.canDetectOrientation()) {
                        myOrientationEventListener.enable();
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    Integer totalMoveX = (int) (event.getRawX() - initialTouchX);
                    params.x = initialX + totalMoveX;
                    Integer totalMoveY = (int) (event.getRawY() - initialTouchY);
                    params.y = initialY + totalMoveY;
                    windowManager.updateViewLayout(chatHead, params);
                    return true;
            }

            return true;
        }

    }



    static void turnOffScreen(final Context context){
        // turn off screen
        try {
            DevicePolicyManager policyManager = (DevicePolicyManager) context
                    .getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminReceiver = new ComponentName(context,
                    ScreenOffAdminReceiver.class);
            boolean admin = policyManager.isAdminActive(adminReceiver);
            if (admin) {

                policyManager.lockNow();
            } else {

                Toast.makeText(context, R.string.device_admin_not_enabled,
                        Toast.LENGTH_LONG).show();
            }

        }
        catch (Exception ex)
        {
            Log.d("DroidTurnOff", "Erro: " + ex.getMessage() );
        }
    }

}





