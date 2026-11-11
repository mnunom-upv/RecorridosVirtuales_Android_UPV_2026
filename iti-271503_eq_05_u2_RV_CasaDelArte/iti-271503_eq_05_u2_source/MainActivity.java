package upv.pm.may_ago_26.iti_271503.pgu2_eq05;

/*
Ver enlace para detalles de interacción con el Control de XBox
https://developer.android.com/training/game-controllers/controller-input
*/

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
public class MainActivity extends CardboardActivity {

    final static int UP       = 0;
    final static int LEFT     = 1;
    final static int RIGHT    = 2;
    final static int DOWN     = 3;

    private CardboardView mCardboardView;
    private TextView tv_bienvenida;
    private TextView tv_pause;
    private CubeRenderer mRenderer;
    private Vibrator mVibrator;

    int Joystick_Izquierdo_Derecho;


    @SuppressLint("ClickableViewAccessibility") // Medicina Necesaria
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  bloquear orientacion
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Joystick_Izquierdo_Derecho=0;

        mCardboardView = findViewById(R.id.cardboard_view);

        tv_bienvenida = findViewById(R.id.welcomeText);
        tv_pause = findViewById(R.id.pauseText);

        new android.os.Handler().postDelayed(() -> {
            tv_bienvenida.setVisibility(View.GONE);
        }, 8000);


        mRenderer = new CubeRenderer(this);
        mCardboardView.setRenderer(mRenderer);
        setCardboardView(mCardboardView);

        mRenderer.reiniciarPosiciones();
        
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onCardboardTrigger() {
        super.onCardboardTrigger();
        if (mVibrator != null) {
            mVibrator.vibrate(50);
        }
        if (mRenderer != null) {
            mRenderer.reiniciarPosiciones();
        }
    }

    // botones control (A,B,X,Y)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BUTTON_A:
                    // descender
                    mRenderer.MoveUpward(-5f);
                    break;
                case KeyEvent.KEYCODE_BUTTON_Y:
                    // elevarse
                    mRenderer.MoveUpward(5f);
                    break;
                case KeyEvent.KEYCODE_BUTTON_X:
                    // reiniciar
                    mRenderer.reiniciarPosiciones();
                    break;
                case KeyEvent.KEYCODE_BUTTON_START:
                    if (tv_pause.getVisibility() == View.GONE) {
                        tv_pause.setVisibility(View.VISIBLE);
                    } else {
                        tv_pause.setVisibility(View.GONE);
                    }
                    return true;

            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BUTTON_Y:
                    return true;
                case KeyEvent.KEYCODE_BUTTON_A:
                    return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }


    // ----- Para mando conectado (Pad direccional o cruceta) ----------------------------
    Dpad dpad = new Dpad();

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {

        InputDevice inputDevice = event.getDevice();

        //DPAD
        if (Dpad.isDpadDevice(event)) {
            int press = dpad.getDirectionPressed(event);
            switch (press) {
                case LEFT:
                    mRenderer.StrafeRight( -10f );
                    return true;
                case RIGHT:
                    mRenderer.StrafeRight( 10f );
                    return true;
                case UP:
                    mRenderer.MoveForward( -10f );
                    return true;
                case DOWN:
                    mRenderer.MoveForward( 10f ) ;
                    return true;
            }
        }

        // JOYSTICK
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {

            float lx = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_X, -1);  // izquierda/derecha
            float ly = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Y, -1);  // adelante/atrás

            float rx = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Z, -1);   // rotar izquierda/derecha
            float ry = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_RZ, -1);  // rotar arriba/abajo

            float velocidadMovimiento = 5f;
            float velocidadRotacion = 5f;

            // Movimiento con joystick izquierdo
            if (Math.abs(ly) > 0.1f)
                mRenderer.MoveForward(ly * velocidadMovimiento);
            if (Math.abs(lx) > 0.1f)
                mRenderer.StrafeRight(lx * velocidadMovimiento);

            // Rotación con joystick derecho
            if (Math.abs(rx) > 0.1f)
                mRenderer.RotateY(-rx * velocidadRotacion);
            if (Math.abs(ry) > 0.1f)
                mRenderer.RotateX(-ry * velocidadRotacion);
        }

        return super.onGenericMotionEvent(event);

    }

    private static float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range = device.getMotionRange(axis, event.getSource());
        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value = historyPos < 0 ? event.getAxisValue(axis):event.getHistoricalAxisValue(axis, historyPos);
            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
    }
}