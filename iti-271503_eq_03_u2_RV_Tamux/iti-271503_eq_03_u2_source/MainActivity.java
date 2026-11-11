package upv_pm.may_ago_26.iti_271503.pgu2_eq03;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {
    private GLSurfaceView glSurfaceView;
    private CubeRenderer renderer;
    private final Dpad dpad = new Dpad();

    private SensorManager sensorManager;
    private Sensor rotationVectorSensor;
    private final float[] rotationMatrix = new float[16];
    private final float[] remappedRotationMatrix = new float[16];
    private final float[] orientationAngles = new float[3];

    private static final float DEAD_ZONE = 0.28f;

    // =========================================================================
    //  MODO DE JUEGO
    //  false = MODO MINECRAFT: botones en pantalla, vista única, sin sensor.
    //  true  = MODO VR: dos ojos, la cámara se mueve con el giroscopio del
    //          teléfono (mueves la cabeza) y mantienes pulsado para avanzar.
    //
    //  YA NO ES UNA CONSTANTE: ahora se puede cambiar EN CALIENTE con el botón
    //  "VR/MC" de la pantalla o con el botón Y (o START) del mando.
    //  MODO_VR_INICIAL sólo dice con cuál de los dos ARRANCA la aplicación.
    // =========================================================================
    private static final boolean MODO_VR_INICIAL = true;
    private boolean modoVR = MODO_VR_INICIAL;

    // =========================================================================
    //  VELOCIDADES  (unidades por SEGUNDO / grados por SEGUNDO)
    //
    //  Escala del modelo: la puerta mide 40 unidades = 2.10 m, o sea que
    //  1 unidad ≈ 5.25 cm y 1 metro ≈ 19 unidades.
    //
    //  Antes estos números se sumaban una vez POR FRAME: 1.75 por frame a 60 fps
    //  son 105 u/s ≈ 5.5 m/s, casi el doble de lo que corre una persona, y en un
    //  teléfono a 120 Hz iba al doble todavía. Ahora son velocidades reales y se
    //  sienten igual en cualquier dispositivo.
    // =========================================================================
    /** Caminar: 45 u/s ≈ 2.4 m/s (paso rápido). Con el botón de correr se duplica. */
    private static final float VEL_CAMINAR   = 45f;
    /** Moverse de lado sin girar: un poco más lento, como en la vida real. */
    private static final float VEL_STRAFE    = 38f;
    /** Girar la vista horizontalmente con el stick derecho o los botones. */
    private static final float VEL_GIRO      = 110f;
    /** Mirar arriba / abajo. Más lento que el giro: da más precisión. */
    private static final float VEL_PITCH     = 80f;
    /** Empujón de un toque de tecla / D-pad (en unidades, no por segundo). */
    private static final float PASO_TECLA    = 8f;

    // Sensibilidad al arrastrar el dedo en modo Minecraft.
    private static final float SENS_ARRASTRE_MOVER = 0.5f;
    private static final float SENS_ARRASTRE_MIRAR = 0.10f;

    // =========================================================================
    //  AJUSTES DE LOS BOTONES EN PANTALLA (estilo Minecraft)
    // =========================================================================
    private static final int   BOTON_DP        = 60;   // lado de cada botón (dp)
    private static final int   SEPARACION_DP   = 4;    // hueco entre botones de una cruz
    private static final int   MARGEN_DP       = 22;   // separación desde el borde
    private static final int   BOTON_CHICO_DP  = 46;   // botones de la barra superior

    // Apariencia de los botones.
    private static final int   COLOR_BOTON     = 0x99202830; // relleno semitransparente
    private static final int   COLOR_BORDE     = 0xCCFFFFFF; // borde
    private static final int   COLOR_TEXTO     = Color.WHITE;

    private final Handler holdHandler = new Handler(Looper.getMainLooper());

    // Capas de interfaz.
    private FrameLayout capaMinecraft;   // cruces de movimiento y vista
    private FrameLayout barraSuperior;   // mapa / modo / reiniciar (siempre visible)
    private FrameLayout pantallaCarga;
    private Button btnModo;
    private Button btnMapa;

    // Controles de respaldo (arrastrar en zonas vacías): izquierda = mover, derecha = mirar.
    private float touchStartX;
    private float touchStartY;
    private boolean touchOnLeftSide;
    private long lastTouchMoveMs;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        glSurfaceView = findViewById(R.id.tamux_surface);
        glSurfaceView.setEGLContextClientVersion(2);

        renderer = new CubeRenderer(this);
        renderer.setModoVR(modoVR);            // el renderer dibuja 1 o 2 ojos según el modo
        glSurfaceView.setRenderer(renderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }

        glSurfaceView.setFocusable(true);
        glSurfaceView.setFocusableInTouchMode(true);
        glSurfaceView.setOnTouchListener((v, event) -> manejarTouch(event));

        // Las DOS capas se crean siempre; sólo se muestra la que toca. Antes se
        // creaba una u otra según el modo, y por eso el modo no podía cambiarse
        // sin reiniciar la aplicación.
        capaMinecraft = construirControlesEnPantalla();
        addContentView(capaMinecraft, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        barraSuperior = construirBarraSuperior();
        addContentView(barraSuperior, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        aplicarModo(false); // deja visible la capa correcta sin avisar al usuario

        // El foco se pide AL FINAL: si se pidiera antes, la capa de controles que
        // se añade encima se lo quitaría y el mando dejaría de funcionar.
        glSurfaceView.requestFocus();

        // ---- PANTALLA DE CARGA ----
        pantallaCarga = new FrameLayout(this);
        pantallaCarga.setBackgroundColor(Color.BLACK);
        android.widget.TextView textoCarga = new android.widget.TextView(this);
        textoCarga.setText("Cargando...");
        textoCarga.setTextColor(Color.WHITE);
        textoCarga.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        pantallaCarga.addView(textoCarga, textParams);
        addContentView(pantallaCarga,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
    }

    public void ocultarPantallaCarga() {
        runOnUiThread(() -> {
            if (pantallaCarga != null) {
                pantallaCarga.setVisibility(View.GONE);
            }
        });
    }

    // =========================================================================
    //  CAMBIO DE MODO  VR  <->  MINECRAFT
    // =========================================================================
    private void alternarModo() {
        modoVR = !modoVR;
        aplicarModo(true);
    }

    /** Deja la interfaz, el sensor y el renderer coherentes con el modo actual. */
    private void aplicarModo(boolean avisar) {
        renderer.setModoVR(modoVR);

        // Al salir de VR se apaga el giroscopio conservando hacia dónde mirabas;
        // al entrar, la cabeza toma el control de la inclinación.
        if (modoVR) renderer.prepararParaVR();
        else        renderer.desactivarSensor();

        // Nadie se queda caminando solo al cambiar de modo.
        detenerMovimiento();

        capaMinecraft.setVisibility(modoVR ? View.GONE : View.VISIBLE);
        // En VR la pantalla se ve a través del visor: cualquier botón dibujado
        // encima aparece duplicado y flotando delante de la escena. Se ocultan
        // todos y sólo queda el minimapa, que el renderer sí dibuja por ojo.
        // Para volver, hay un toque doble (ver manejarTouch) o el botón Y / START
        // del mando.
        barraSuperior.setVisibility(modoVR ? View.GONE : View.VISIBLE);
        if (btnModo != null) btnModo.setText(modoVR ? "VR" : "MC");

        actualizarSensor();
        glSurfaceView.requestFocus();

        if (avisar) {
            Toast.makeText(this,
                    modoVR ? "Modo VR (dos ojos)" : "Modo Minecraft (pantalla completa)",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /** Registra el giroscopio sólo si estamos en VR y la app está en primer plano. */
    private void actualizarSensor() {
        if (sensorManager == null || rotationVectorSensor == null) return;
        sensorManager.unregisterListener(this);
        if (modoVR) {
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    private void detenerMovimiento() {
        holdHandler.removeCallbacksAndMessages(null);
        renderer.setInputForward(0f);
        renderer.setInputStrafe(0f);
        renderer.setInputYaw(0f);
        renderer.setInputPitch(0f);
        renderer.setCorriendo(false);
    }

    private void alternarMapa() {
        boolean visible = renderer.alternarMapa();
        if (btnMapa != null) btnMapa.setAlpha(visible ? 1f : 0.45f);
    }

    // Toque doble en VR: la barra asoma unos segundos y se vuelve a esconder.
    private long ultimoToqueMs = 0L;
    private static final long BARRA_VISIBLE_MS = 6000L;

    private void mostrarBarraUnosSegundos() {
        if (barraSuperior == null) return;
        barraSuperior.setVisibility(View.VISIBLE);
        // Se usa el propio View para programar el ocultado: así no lo cancela
        // detenerMovimiento(), que limpia todos los mensajes de holdHandler.
        barraSuperior.removeCallbacks(ocultarBarra);
        barraSuperior.postDelayed(ocultarBarra, BARRA_VISIBLE_MS);
    }

    private final Runnable ocultarBarra = new Runnable() {
        @Override public void run() {
            if (modoVR && barraSuperior != null) barraSuperior.setVisibility(View.GONE);
        }
    };

    // =========================================================================
    //  BARRA SUPERIOR: mapa, modo y reiniciar. Visible en los DOS modos.
    // =========================================================================
    private FrameLayout construirBarraSuperior() {
        FrameLayout capa = new FrameLayout(this);
        capa.setFocusable(false);

        int lado = dp(BOTON_CHICO_DP);
        int sep = dp(8);
        int margen = dp(10);

        // MAPA
        btnMapa = crearBotonChico("MAP");
        btnMapa.setOnClickListener(v -> alternarMapa());
        FrameLayout.LayoutParams p1 = new FrameLayout.LayoutParams(lado, lado);
        p1.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        p1.setMargins(0, margen, (lado + sep) * 2, 0);
        capa.addView(btnMapa, p1);

        // MODO VR / MINECRAFT
        btnModo = crearBotonChico(modoVR ? "VR" : "MC");
        btnModo.setOnClickListener(v -> alternarModo());
        FrameLayout.LayoutParams p2 = new FrameLayout.LayoutParams(lado, lado);
        p2.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        capa.addView(btnModo, p2);

        // REINICIAR POSICIÓN
        Button btnReset = crearBotonChico("\u21BB");
        btnReset.setOnClickListener(v -> renderer.ReiniciarPosiciones());
        FrameLayout.LayoutParams p3 = new FrameLayout.LayoutParams(lado, lado);
        p3.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        p3.setMargins((lado + sep) * 2, margen, 0, 0);
        capa.addView(btnReset, p3);

        return capa;
    }

    // =========================================================================
    //  CONSTRUCCIÓN DE LOS BOTONES DE MOVIMIENTO (modo Minecraft)
    // =========================================================================
    private FrameLayout construirControlesEnPantalla() {
        FrameLayout capa = new FrameLayout(this);
        capa.setFocusable(false);

        int margen = dp(MARGEN_DP);
        int ladoCruz = dp(BOTON_DP) * 3 + dp(SEPARACION_DP) * 2; // tamaño real de una cruz 3x3

        // ---- CRUZ DE MOVIMIENTO (abajo-izquierda) ----
        FrameLayout cruzMovimiento = construirCruzMovimiento();
        FrameLayout.LayoutParams pMov = new FrameLayout.LayoutParams(ladoCruz, ladoCruz);
        pMov.gravity = Gravity.BOTTOM | Gravity.START;
        pMov.setMargins(margen, 0, 0, margen);
        capa.addView(cruzMovimiento, pMov);

        // ---- CRUZ DE VISTA / CÁMARA (abajo-derecha) ----
        // Mantener pulsado gira de forma continua: la velocidad la pone el
        // renderer en grados por segundo, no un Handler repitiendo saltitos.
        FrameLayout cruzVista = new FrameLayout(this);
        cruzVista.setLayoutParams(new FrameLayout.LayoutParams(ladoCruz, ladoCruz));
        int lado = dp(BOTON_DP);
        agregarEnCelda(cruzVista, crearBotonVista("\u25B2",  VEL_PITCH, false),
                lado, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0, 0, 0);
        agregarEnCelda(cruzVista, crearBotonVista("\u25BC", -VEL_PITCH, false),
                lado, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0, 0, 0);
        agregarEnCelda(cruzVista, crearBotonVista("\u25C0", -VEL_GIRO, true),
                lado, Gravity.START | Gravity.CENTER_VERTICAL, 0, 0, 0, 0);
        agregarEnCelda(cruzVista, crearBotonVista("\u25B6",  VEL_GIRO, true),
                lado, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 0, 0);

        FrameLayout.LayoutParams pVis = new FrameLayout.LayoutParams(ladoCruz, ladoCruz);
        pVis.gravity = Gravity.BOTTOM | Gravity.END;
        pVis.setMargins(0, 0, margen, margen);
        capa.addView(cruzVista, pVis);

        // ---- CORRER (mantener pulsado) — junto a la cruz de movimiento ----
        Button btnCorrer = crearBotonCorrer();
        FrameLayout.LayoutParams pCorrer = new FrameLayout.LayoutParams(dp(BOTON_DP), dp(BOTON_DP));
        pCorrer.gravity = Gravity.BOTTOM | Gravity.START;
        pCorrer.setMargins(margen + dp(BOTON_DP) * 3 + dp(SEPARACION_DP) * 2 + dp(10), 0, 0, margen);
        capa.addView(btnCorrer, pCorrer);

        return capa;
    }

    private FrameLayout construirCruzMovimiento() {
        int lado = dp(BOTON_DP);
        int sep = dp(SEPARACION_DP);
        int total = lado * 3 + sep * 2;

        FrameLayout c = new FrameLayout(this);
        c.setFocusable(false);
        c.setLayoutParams(new FrameLayout.LayoutParams(total, total));

        agregarEnCelda(c, crearBotonMantenido("\u25B2", -VEL_CAMINAR, true),
                lado, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0, 0, 0);
        agregarEnCelda(c, crearBotonMantenido("\u25BC", VEL_CAMINAR, true),
                lado, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0, 0, 0);
        agregarEnCelda(c, crearBotonMantenido("\u25C0", -VEL_STRAFE, false),
                lado, Gravity.START | Gravity.CENTER_VERTICAL, 0, 0, 0, 0);
        agregarEnCelda(c, crearBotonMantenido("\u25B6", VEL_STRAFE, false),
                lado, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 0, 0);
        return c;
    }

    /** Botón que mantiene una velocidad de desplazamiento mientras está pulsado. */
    @SuppressLint("ClickableViewAccessibility")
    private Button crearBotonMantenido(String etiqueta, final float velocidad, final boolean esAvance) {
        final Button b = crearBoton(etiqueta);
        b.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.6f);
                    if (esAvance) renderer.setInputForward(velocidad);
                    else          renderer.setInputStrafe(velocidad);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1f);
                    if (esAvance) renderer.setInputForward(0f);
                    else          renderer.setInputStrafe(0f);
                    return true;
            }
            return false;
        });
        return b;
    }

    /** Botón de cámara: gira o inclina de forma continua mientras lo mantienes. */
    @SuppressLint("ClickableViewAccessibility")
    private Button crearBotonVista(String etiqueta, final float velocidad, final boolean esGiro) {
        final Button b = crearBoton(etiqueta);
        b.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.6f);
                    if (esGiro) renderer.setInputYaw(velocidad);
                    else        renderer.setInputPitch(velocidad);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1f);
                    if (esGiro) renderer.setInputYaw(0f);
                    else        renderer.setInputPitch(0f);
                    return true;
            }
            return false;
        });
        return b;
    }

    @SuppressLint("ClickableViewAccessibility")
    private Button crearBotonCorrer() {
        final Button b = crearBoton("\u00BB");
        b.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.6f);
                    renderer.setCorriendo(true);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1f);
                    renderer.setCorriendo(false);
                    return true;
            }
            return false;
        });
        return b;
    }

    private void agregarEnCelda(FrameLayout padre, Button boton, int lado, int gravity,
                                int l, int t, int r, int b) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(lado, lado);
        lp.gravity = gravity;
        lp.setMargins(l, t, r, b);
        padre.addView(boton, lp);
    }

    /** Botón visual básico (rectángulo redondeado semitransparente con borde). */
    private Button crearBoton(String etiqueta) {
        Button b = new Button(this);
        b.setText(etiqueta);
        // Sin foco: si no, el D-pad del mando navega entre los botones en vez de
        // mover al personaje, y onKeyDown/onGenericMotionEvent nunca se ejecutan.
        b.setFocusable(false);
        b.setFocusableInTouchMode(false);
        b.setTextColor(COLOR_TEXTO);
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        b.setAllCaps(false);
        b.setPadding(0, 0, 0, 0);

        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(COLOR_BOTON);
        fondo.setCornerRadius(dp(12));
        fondo.setStroke(dp(2), COLOR_BORDE);
        b.setBackground(fondo);
        return b;
    }

    private Button crearBotonChico(String etiqueta) {
        Button b = crearBoton(etiqueta);
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        return b;
    }

    /** Convierte dp a píxeles según la densidad de la pantalla. */
    private int dp(int valor) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valor, getResources().getDisplayMetrics());
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
        renderer.reiniciarReloj();  // el primer frame tras volver no debe "saltar"
        actualizarSensor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) sensorManager.unregisterListener(this);
        detenerMovimiento();        // evita que un botón "quede" pulsado
        glSurfaceView.onPause();
    }

    // =========================================================================
    //  MANDO FÍSICO (gamepad / teclado)
    //
    //  MAPA DE BOTONES
    //  ---------------------------------------------------------------------
    //   Stick izquierdo ....... caminar y moverse de lado
    //   Stick derecho ......... mirar (girar / inclinar)
    //   Cruceta (D-pad) ....... paso corto en cada dirección
    //   A ..................... CORRER (mantener pulsado)
    //   B ..................... reiniciar posición
    //   X ..................... PONER / QUITAR EL MAPA
    //   Y ..................... CAMBIAR entre modo VR y modo Minecraft
    //   L1 / R1 ............... moverse de lado a la izquierda / derecha
    //   L2 / R2 ............... correr
    //   SELECT ................ poner / quitar el mapa (alternativa a X)
    //   START ................. cambiar de modo (alternativa a Y)
    //
    //  TECLADO (por si pruebas en emulador o con teclado Bluetooth)
    //   W A S D / flechas ..... moverse      Q E ..... girar
    //   M ..... mapa           V ..... modo   R ..... reiniciar
    //   SHIFT ..... correr
    // =========================================================================
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Las acciones de un solo disparo (mapa, modo, reinicio) no deben
        // repetirse mientras el botón sigue hundido.
        if (event.getRepeatCount() == 0) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BUTTON_X:
                case KeyEvent.KEYCODE_BUTTON_SELECT:
                case KeyEvent.KEYCODE_M:
                    alternarMapa();
                    return true;
                case KeyEvent.KEYCODE_BUTTON_Y:
                case KeyEvent.KEYCODE_BUTTON_START:
                case KeyEvent.KEYCODE_BUTTON_MODE:
                case KeyEvent.KEYCODE_V:
                    alternarModo();
                    return true;
                case KeyEvent.KEYCODE_BUTTON_B:
                case KeyEvent.KEYCODE_R:
                    renderer.ReiniciarPosiciones();
                    return true;
                case KeyEvent.KEYCODE_BUTTON_A:
                case KeyEvent.KEYCODE_BUTTON_L2:
                case KeyEvent.KEYCODE_BUTTON_R2:
                case KeyEvent.KEYCODE_SHIFT_LEFT:
                case KeyEvent.KEYCODE_SHIFT_RIGHT:
                    renderer.setCorriendo(true);
                    return true;
            }

            int dpadDirection = dpad.getDirectionPressed(event);
            if (dpadDirection != -1) {
                moverConDpad(dpadDirection);
                return true;
            }

            if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BUTTON_L1:
                        renderer.pulseStrafe(-PASO_TECLA);
                        return true;
                    case KeyEvent.KEYCODE_BUTTON_R1:
                        renderer.pulseStrafe(PASO_TECLA);
                        return true;
                }
            }
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_W:
            case KeyEvent.KEYCODE_DPAD_UP:
                renderer.pulseForward(-PASO_TECLA);
                return true;
            case KeyEvent.KEYCODE_S:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                renderer.pulseForward(PASO_TECLA);
                return true;
            case KeyEvent.KEYCODE_A:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                renderer.pulseStrafe(-PASO_TECLA);
                return true;
            case KeyEvent.KEYCODE_D:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                renderer.pulseStrafe(PASO_TECLA);
                return true;
            case KeyEvent.KEYCODE_Q:
                renderer.RotateY(-4f);
                return true;
            case KeyEvent.KEYCODE_E:
                renderer.RotateY(4f);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /** Soltar el botón de correr vuelve a la velocidad normal. */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_A:
            case KeyEvent.KEYCODE_BUTTON_L2:
            case KeyEvent.KEYCODE_BUTTON_R2:
            case KeyEvent.KEYCODE_SHIFT_LEFT:
            case KeyEvent.KEYCODE_SHIFT_RIGHT:
                renderer.setCorriendo(false);
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    // =========================================================================
    //  RESPALDO: arrastrar el dedo en zonas VACÍAS (sin botón).
    // =========================================================================
    private boolean manejarTouch(MotionEvent event) {
        // -------- MODO VR: mantén pulsado (en cualquier parte) para avanzar --------
        if (modoVR) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    // Toque doble = asomar los botones unos segundos. Es la salida
                    // de emergencia del modo VR cuando no traes mando: sin esto,
                    // al ocultar la barra te quedarías atrapado en VR.
                    long ahora = System.currentTimeMillis();
                    if (ahora - ultimoToqueMs < 350) mostrarBarraUnosSegundos();
                    ultimoToqueMs = ahora;
                    renderer.setInputForward(-VEL_CAMINAR);
                    return true;
                case MotionEvent.ACTION_POINTER_DOWN:
                    // Dos dedos = correr, sin soltar la marcha.
                    renderer.setCorriendo(true);
                    return true;
                case MotionEvent.ACTION_POINTER_UP:
                    if (event.getPointerCount() <= 2) renderer.setCorriendo(false);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    renderer.setInputForward(0f);
                    renderer.setCorriendo(false);
                    return true;
            }
            return false;
        }

        // -------- MODO MINECRAFT: arrastrar en zonas vacías --------
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchStartX = event.getX();
                touchStartY = event.getY();
                touchOnLeftSide = touchStartX < (glSurfaceView.getWidth() * 0.5f);
                lastTouchMoveMs = System.currentTimeMillis();
                return true;
            case MotionEvent.ACTION_MOVE:
                long now = System.currentTimeMillis();
                if (now - lastTouchMoveMs < 16) return true;
                lastTouchMoveMs = now;

                float dx = event.getX() - touchStartX;
                float dy = event.getY() - touchStartY;

                if (touchOnLeftSide) {
                    renderer.pulseForward(dy * SENS_ARRASTRE_MOVER);
                    renderer.pulseStrafe(dx * SENS_ARRASTRE_MOVER);
                } else {
                    renderer.RotateY(dx * SENS_ARRASTRE_MIRAR);
                    renderer.RotateX(-dy * SENS_ARRASTRE_MIRAR);
                }

                touchStartX = event.getX();
                touchStartY = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return true;
        }
        return false;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK
                || (event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD
                || Dpad.isDpadDevice(event)) {

            InputDevice device = event.getDevice();
            float leftX  = getCenteredAxis(event, device, MotionEvent.AXIS_X);
            float leftY  = getCenteredAxis(event, device, MotionEvent.AXIS_Y);
            float rightX = getCenteredAxis(event, device, MotionEvent.AXIS_Z);
            float rightY = getCenteredAxis(event, device, MotionEvent.AXIS_RZ);
            if (Math.abs(rightX) <= DEAD_ZONE && Math.abs(rightY) <= DEAD_ZONE) {
                rightX = getCenteredAxis(event, device, MotionEvent.AXIS_RX);
                rightY = getCenteredAxis(event, device, MotionEvent.AXIS_RY);
            }

            // Android sólo manda eventos cuando los ejes CAMBIAN: si se llamara al
            // renderer aquí, al mantener el stick quieto el personaje se detendría.
            // Por eso se guarda el estado y el GLThread lo aplica cada frame.
            renderer.setInputForward(Math.abs(leftY)  > DEAD_ZONE ? leftY  * VEL_CAMINAR : 0f);
            renderer.setInputStrafe (Math.abs(leftX)  > DEAD_ZONE ? leftX  * VEL_STRAFE  : 0f);
            renderer.setInputYaw    (Math.abs(rightX) > DEAD_ZONE ? rightX * VEL_GIRO    : 0f);
            renderer.setInputPitch  (Math.abs(rightY) > DEAD_ZONE ? -rightY * VEL_PITCH  : 0f);

            // Gatillos analógicos (muchos mandos no mandan L2/R2 como botón).
            float gatillo = Math.max(
                    event.getAxisValue(MotionEvent.AXIS_LTRIGGER),
                    event.getAxisValue(MotionEvent.AXIS_RTRIGGER));
            if (gatillo > 0.5f) renderer.setCorriendo(true);
            else if (gatillo < 0.2f) renderer.setCorriendo(false);

            // El D-pad se atiende DESPUÉS de los sticks. Antes iba primero y su
            // "return true" se comía el evento antes de leer los ejes analógicos.
            if (Math.abs(leftX) <= DEAD_ZONE && Math.abs(leftY) <= DEAD_ZONE) {
                int dpadDirection = dpad.getDirectionPressed(event);
                if (dpadDirection != -1) moverConDpad(dpadDirection);
            }
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    private void moverConDpad(int direction) {
        switch (direction) {
            case Dpad.LEFT:
                renderer.pulseStrafe(-PASO_TECLA);
                break;
            case Dpad.RIGHT:
                renderer.pulseStrafe(PASO_TECLA);
                break;
            case Dpad.UP:
                renderer.pulseForward(-PASO_TECLA);
                break;
            case Dpad.DOWN:
                renderer.pulseForward(PASO_TECLA);
                break;
        }
    }

    private static float getCenteredAxis(MotionEvent event, InputDevice device, int axis) {
        if (device == null) return 0f;
        InputDevice.MotionRange range = device.getMotionRange(axis, event.getSource());
        if (range == null) return 0f;
        float value = event.getAxisValue(axis);
        return Math.abs(value) > Math.max(range.getFlat(), DEAD_ZONE) ? value : 0f;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!modoVR) return;   // en modo Minecraft el giroscopio no manda
        if (event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) return;
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_X,
                SensorManager.AXIS_MINUS_Z,
                remappedRotationMatrix
        );
        SensorManager.getOrientation(remappedRotationMatrix, orientationAngles);
        renderer.setOrientation(orientationAngles);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se requiere para el recorrido.
    }
}
