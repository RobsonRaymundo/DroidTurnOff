package com.droid.ray.droidturnoff;


import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class DroidHeadService extends Service {
    private WindowManager windowManager;
    private ImageView chatHead;

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private Context context;
    private View.OnTouchListener onTouchListener;
    private Intent mIntentService;

    public enum EnumStateButton {
        CLOSE,
        VIEW
    }

    private EnumStateButton StateButton;

    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
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
        TimeSleep(1000);
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

    private void Vibrar(int valor) {
        try {
            TimeSleep(500);
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(valor);
            TimeSleep(500);
        } catch (Exception ex) {
            Log.d("DroidTurnOff", "Vibrar: " + ex.getMessage());
        }
    }

    private void InicializarVariavel() {
        context = getBaseContext();

        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        onTouchListener = new TouchListener();

        chatHead = new ImageView(context);
        chatHead.setImageResource(R.mipmap.stoprec);
        StateButton = EnumStateButton.VIEW;
        params.gravity = Gravity.CENTER;
        windowManager.addView(chatHead, params);

        try {
            params.x = DroidPreferences.GetInteger(context, "params.x");
            params.y = DroidPreferences.GetInteger(context, "params.y");
            windowManager.updateViewLayout(chatHead, params);
        } catch (Exception ex) {
            Log.d("DroidTurnOff", "InicializarVariavel: " + ex.getMessage());
        }
    }

    private void InicializarAcao() {
        chatHead.setOnTouchListener(onTouchListener);
    }


    public class TouchListener implements View.OnTouchListener {

        private GestureDetector gestureDetector = new GestureDetector(DroidHeadService.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (StateButton == EnumStateButton.VIEW) {
                    chatHead.setImageResource(R.mipmap.closerec);
                    StateButton = EnumStateButton.CLOSE;
                } else {
                    chatHead.setImageResource(R.mipmap.stoprec);
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
                } else {
                    Vibrar(100);
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
                    return true;
                case MotionEvent.ACTION_MOVE:
                    Integer totalMoveX = (int) (event.getRawX() - initialTouchX);
                    params.x = initialX + totalMoveX;
                    Integer totalMoveY = (int) (event.getRawY() - initialTouchY);
                    params.y = initialY + totalMoveY;
                    windowManager.updateViewLayout(chatHead, params);
                    try {
                        DroidPreferences.SetInteger(context, "params.x", params.x);
                        DroidPreferences.SetInteger(context, "params.y", params.y);
                    } catch (Exception ex) {
                    }
                    return true;
            }

            return true;
        }

    }


    @Override
    public void onDestroy() {
        if (chatHead != null) windowManager.removeView(chatHead);
        super.onDestroy();
    }

    public static void turnOffScreen(final Context context) {
        // turn off screen
        try {
            if (!DroidShowDeviceAdmin.EnabledAdminAndLock(context)) {
                Intent intent = new Intent(context, DroidMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        } catch (Exception ex) {
            Toast.makeText(context, R.string.device_admin_not_enabled,
                    Toast.LENGTH_LONG).show();
            Log.d("DroidTurnOff", "turnOffScreen: " + ex.getMessage());

        }
    }


}





