package com.droid.ray.droidturnoff;


import android.app.AlertDialog;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.Display;
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
    private boolean killService = false;

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
        Log.d("DroidTurnOff", "DroidHeadService - onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        InicializarVariavel();
        InicializarAcao();
        AtualizarPosicao();
        Log.d("DroidTurnOff", "DroidHeadService - onCreate");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //call widget update methods/services/broadcasts
        Log.d("DroidTurnOff", "onTouch - Neworientation: " + newConfig.orientation);
        //GravarPosicaoAtual();
        AtualizarPosicao();
    }

    private void Vibrar(int valor) {
        try {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(valor);
        } catch (Exception ex) {
            Log.d("DroidTurnOff", "Vibrar: " + ex.getMessage());
        }
    }

    private void AtualizarPosicao() {
        try {
            if (DroidPreferences.GetInteger(context, "orientationActual") == 2 || getResources().getConfiguration().orientation == 2) {
                params.x = DroidPreferences.GetInteger(context, "params.y");
                params.y = DroidPreferences.GetInteger(context, "params.x");
            } else {
                params.x = DroidPreferences.GetInteger(context, "params.x");
                params.y = DroidPreferences.GetInteger(context, "params.y");
            }

            windowManager.updateViewLayout(chatHead, params);

        } catch (Exception ex) {
            Log.d("DroidTurnOff", "InicializarVariavel: " + ex.getMessage());
        }

        Log.d("DroidTurnOff", "onTouch - x: " + DroidPreferences.GetInteger(context, "params.x"));
        Log.d("DroidTurnOff", "onTouch - y: " + DroidPreferences.GetInteger(context, "params.y"));
    }

    private void GravarPosicaoAtual() {
        try {
            DroidPreferences.SetInteger(context, "params.x", params.x);
            DroidPreferences.SetInteger(context, "params.y", params.y);
            DroidPreferences.SetInteger(context, "orientationActual", getResources().getConfiguration().orientation);
        } catch (Exception ex) {
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

                    try {
                        killService = true;
                        stopSelf();
                    }
                    catch (Exception ex)
                    {
                        Log.d("DroidTurnOff", "stopSelf: " + ex.getMessage());
                    }


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
                    GravarPosicaoAtual();
                    return true;
            }

            return true;
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DroidTurnOff", "DroidHeadService - onStartCommand");
        return START_NOT_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);

        if (!killService) {
            Intent broadcastIntent = new Intent("com.droid.ray.droidturnoff.ACTION_RESTART_SERVICE");
            sendBroadcast(broadcastIntent);
            Log.d("DroidTurnOff", "DroidHeadService - onDestroy");
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("DroidTurnOff", "DroidHeadService - onUnbind");
        return super.onUnbind(intent);

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("DroidTurnOff", "DroidHeadService - onTaskRemoved");
        
        if (!killService) {
            Intent broadcastIntent = new Intent("com.droid.ray.droidturnoff.ACTION_RESTART_SERVICE");
            sendBroadcast(broadcastIntent);
            Log.d("DroidTurnOff", "DroidHeadService - onDestroy");
        }
    }

    @Override
    public void onTrimMemory(int level) {
        Log.d("DroidTurnOff", "DroidHeadService - onTrimMemory");
        super.onTrimMemory(level);
    }

    @Override
    public void onLowMemory() {
        Log.d("DroidTurnOff", "DroidHeadService - onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d("DroidTurnOff", "DroidHeadService - onRebind");
        super.onRebind(intent);
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





