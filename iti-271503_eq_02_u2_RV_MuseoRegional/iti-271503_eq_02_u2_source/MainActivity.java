package upv.pm.may_ago_26.iti_271503.pgu2_eq02;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;


public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {

    private static final String TAG = "MainActivity";

    // Viewing variables
    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 10000.0f;
    private static  float CAMERA_Z = 150.01f;
    private static  float CAMERA_X = 450.00f;
    private static  float CAMERA_Y = 4.0f;
    private boolean banderaReinicio = false;


    private float [] Position;
    private float[] RightVector;
    private float[] UpVector;
    private float[] ViewDir;
    private float[] camera;
    private float[] view;
    private float[] modelViewProjection;


    // Rendering variables
    /** This will be used to pass in the transformation matrix. */
    private int mMvpMatrixHandle;
    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;
    /** This will be used to pass in model position information. */
    private int mPositionHandle;
    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;
    int vertexShaderHandle;
    int fragmentShaderHandle;
    /** This is a handle to our cube shading program. */
    private int mProgramHandle;

    RenderDIbujos renderDIbujos;

    //Variables del movimiento hacia atras y adelante
    private float previousX;
    private float previousY;

    final static int UP       = 0;
    final static int LEFT     = 1;
    final static int RIGHT    = 2;
    final static int DOWN     = 3;

    int Joystick_Izquierdo_Derecho;

    // -------------------------------------------------------------------------
    // Navegación tipo Fortnite adaptada al Museo Regional
    // Joystick izquierdo: caminar adelante/atrás y lateral.
    // Joystick derecho: girar la vista horizontalmente y ajustar un poco la altura.
    // Cruceta: movimiento alterno por pasos.
    // Botón A: reiniciar al punto inicial del recorrido.
    // -------------------------------------------------------------------------
    private static final float START_X = 950.0f;
    private static final float START_Y = 30.0f;
    private static final float START_Z = 400.0f;

    private static final float MOVE_SPEED = 8.0f;
    private static final float DPAD_STEP = 14.0f;
    private static final float LOOK_SPEED = 3.2f;
    private static final float JOYSTICK_DEAD_ZONE = 0.20f;

    // Límites amplios para evitar que el jugador se pierda fuera del demo.
    private static final float MIN_X = -350.0f;
    private static final float MAX_X = 2300.0f;
    private static final float MIN_Y = 20.0f;
    private static final float MAX_Y = 220.0f;
    private static final float MIN_Z = -2200.0f;
    private static final float MAX_Z = 1200.0f;

    private float yawDegrees = 0.0f;
    private float pitchDegrees = 0.0f;

    // Estados del joystick para movimiento fluido
    private float joystickLeftX = 0.0f;
    private float joystickLeftY = 0.0f;

    float PI = 3.1415926535897932384626433832795f;
    float PIdiv180 = (PI/180.0f);

    Eye eye;

    @SuppressLint("ClickableViewAccessibility") // Medicina Necesaria
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);
        camera = new float[16];
        view = new float[16];
        modelViewProjection = new float[16];

        //Coordenadas para moverse sin el control
        Position = new float[3];                            // Punto inicial del recorrido
        Position[0] = START_X;
        Position[1] = START_Y;
        Position[2] = START_Z;

                                    /*
                                       Biblioteca   |Z
                                       90           |        1200
                                                    |
                               entrada -X________________________X
                                                    |
                                                    |           90
                                           nave 1   | Z    nave 2
                                          Puerta garnde  carrera 22 */

        RightVector =  new float[3];
        RightVector[0] = 1.0f;
        RightVector[1] = 0.0f;
        RightVector[2] = 0.0f;

        UpVector =  new float[3];
        UpVector[0] = 0.0f;
        UpVector[1] = 1.0f;
        UpVector[2] = 0.0f;

        ViewDir =  new float[3];
        ViewDir[0] = 0.0f;
        ViewDir[1] = 0.0f;
        ViewDir[2] = -1.0f;




    }

    @Override
    public void onNewFrame(HeadTransform p0) {
        // Build the camera matrix and apply it to the ModelView.
        //viewDir, ViewPoint, UpVector en recorrido UPV original

        if(banderaReinicio){

            resetPlayerPosition();

            float[] ViPoint = suma(Position, ViewDir);
            Matrix.setLookAtM(camera, 0, Position[0],  Position[1],  Position[2], ViPoint[0], ViPoint[1], ViPoint[2], UpVector[0], UpVector[1], UpVector[2]);
            banderaReinicio = false;
        }else{
            // --- MOVIMIENTO FLUIDO ---
            // Obtenemos la orientación actual de la cabeza para movernos hacia donde miramos
            float[] headView = new float[16];
            p0.getHeadView(headView, 0);

            // Calculamos el vector "Forward" horizontal basado en el giroscopio
            // El vector forward en espacio de cámara es (0,0,-1). En espacio de mundo es la 3ra fila de la matriz de vista (negada)
            float headForwardX = -headView[2];
            float headForwardZ = -headView[10];

            // Normalizamos el vector en el plano XZ
            float mag = (float)Math.sqrt(headForwardX * headForwardX + headForwardZ * headForwardZ);
            if (mag > 0.001f) {
                headForwardX /= mag;
                headForwardZ /= mag;
            } else {
                headForwardX = 0f;
                headForwardZ = -1f;
            }

            // Vector Right (perpendicular al Forward en el plano horizontal)
            float headRightX = headForwardZ;
            float headRightZ = -headForwardX;

            // Aplicamos movimiento
            if (joystickLeftY != 0.0f) {
                // Adelante/Atrás
                float dist = -joystickLeftY * MOVE_SPEED;
                Position[0] += headForwardX * dist;
                Position[2] += headForwardZ * dist;
            }
            if (joystickLeftX != 0.0f) {
                // Lateral (Strafe)
                float dist = joystickLeftX * MOVE_SPEED;
                Position[0] += headRightX * dist;
                Position[2] += headRightZ * dist;
            }
            limitPlayerPosition();

            // La cámara base para Cardboard solo necesita la posición, la rotación la da el giroscopio
            // Usamos un punto de mira fijo relativo a la posición (siempre "adelante" del cuerpo)
            //Matrix.setLookAtM(camera, 0, Position[0], Position[1], Position[2], Position[0], Position[1], Position[2] - 1.0f, 0, 1, 0);
            // Pero para mantener compatibilidad con el sistema de ViewDir si se desea:
            updateCameraVectors();
            float[] ViewPoint = suma(Position, ViewDir);
            Matrix.setLookAtM(camera, 0, Position[0],  Position[1],  Position[2], ViewPoint[0], ViewPoint[1], ViewPoint[2], UpVector[0], UpVector[1], UpVector[2]);
        }


    }

    @Override
    public void onDrawEye(Eye eye) {
        this.eye = eye;
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //Print eye perspective
        //Log.d("Eye Perspective", "X: " + eye.getPerspective(Z_NEAR, Z_FAR)[0] + " Y: " + eye.getPerspective(Z_NEAR, Z_FAR)[5] + " Z: " + eye.getPerspective(Z_NEAR, Z_FAR)[10]);


        // Get the direction of the eye
        float[] eyeView = eye.getEyeView();


        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        float[] bbb =  new float[16];

        Matrix.multiplyMM(bbb, 0, eye.getEyeView(), 0, camera, 0);

        Matrix.multiplyMM(bbb, 0, perspective, 0, bbb, 0);

        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        System.arraycopy(bbb,0, modelViewProjection,0,16);

        //Hasta este punto tengo la MVP matrix para cada vez que se dibuja

        //Matrix.translateM(modelViewProjection, 0, -500,700,700);

        //piso_o_techo(gll, u_MvpMatrix, 3000, 3000, imagen_cielo, textura_cielo);

        //AQUI SE DIBUJAN LAS ESTRUCTURAS
        renderDIbujos.cielo(modelViewProjection);
        renderDIbujos.cielo_enfrente(modelViewProjection);
        renderDIbujos.cielo_atras(modelViewProjection);
        renderDIbujos.cielo_derecha(modelViewProjection);
        renderDIbujos.cielo_izquierda(modelViewProjection);
        //edificios alrededor


        renderDIbujos.base_cesped(modelViewProjection);
        renderDIbujos.barda(modelViewProjection);
        renderDIbujos.banqueta(modelViewProjection);
        //Dentro del museo
        renderDIbujos.suelo_ladrillos_dentro(modelViewProjection);
        renderDIbujos.suelo_ladrillos_nave2_dentro(modelViewProjection);
        renderDIbujos.suelo_ladrillos2_nave2_dentro(modelViewProjection);
        renderDIbujos.suelo_jardin(modelViewProjection);
        renderDIbujos.taquilla(modelViewProjection);
        renderDIbujos.biblioteca(modelViewProjection);
        renderDIbujos.saladelectura(modelViewProjection);
        renderDIbujos.oficinas(modelViewProjection);
        renderDIbujos.nave1(modelViewProjection);
        renderDIbujos.pared_nave1(modelViewProjection);
        renderDIbujos.suelo_nave1(modelViewProjection);
        renderDIbujos.nave2(modelViewProjection);
        renderDIbujos.suelo_nave2(modelViewProjection);


    }

    public float[] dotProduct(float[] a, float[] b) {
        float[] c = new float[3];
        c[0] = a[1] * b[2] - a[2] * b[1];
        c[1] = a[2] * b[0] - a[0] * b[2];
        c[2] = a[0] * b[1] - a[1] * b[0];
        return c;
    }


    @Override
    public void onFinishFrame(Viewport p0) {

    }

    @Override
    public void onSurfaceChanged(int p0, int p1) {

    }

    @Override
    public void onSurfaceChanged() {

    }

    @Override
    public void onSurfaceCreated(EGLConfig p0) {

        //initializeScene()
        compileShader();
        prepareRendering();

    }


    public void compileShader(){
        vertexShaderHandle = loadShader(GLES20.GL_VERTEX_SHADER, R.raw.per_pixel_vertex_shader_tex_and_light);
        fragmentShaderHandle = loadShader(GLES20.GL_FRAGMENT_SHADER, R.raw.per_pixel_fragment_shader_tex_and_light);
    }

    public void prepareRendering(){
        mProgramHandle = GLES20.glCreateProgram();
        // add the vertex shader to program
        GLES20.glAttachShader(mProgramHandle, vertexShaderHandle);
        // add the fragment shader to program
        GLES20.glAttachShader(mProgramHandle, fragmentShaderHandle);
        String[] aa =  new String[] {"a_Position", "a_TexCoord",  "a_Color"};
        for (int i = 0; i < aa.length; i++)
        {
            GLES20.glBindAttribLocation(mProgramHandle, i, aa[i]);
        }
        // build OpenGL ES program executable
        GLES20.glLinkProgram(mProgramHandle);

        // Get shader params
        // get handle to vertex shader's a_Position member

        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MvpMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Sampler");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoord");


        // enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        renderDIbujos = new RenderDIbujos(mProgramHandle, mMvpMatrixHandle, mTextureUniformHandle, mPositionHandle, mTextureCoordinateHandle, this);


    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * @param type - Vertex or fragment shader type.
     * @param resId - int containing the resource ID of the shader
    code file.
     * @return - Returns an id for the shader.
     */
    private int loadShader(int type, int resId){
        String code = readRawTextFile(resId);
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Converts a raw text file into a string.
     *
     * @param resId The resource ID of the raw text file about to
    be turned into a shader.
     * @return The content of the text file, or null in case of
    error.
     */
    private String readRawTextFile(int resId) {
        InputStream inputStream =
                getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRendererShutdown() {

    }

    // ----- Para mando conectado (Botones A y B)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d("4","soy el cuertooooooooooooooooooooooooooo");

        boolean handled = false;
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            if (event.getRepeatCount() == 0) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BUTTON_Y:
                        //mRenderer.MoveUpward( 5f ); ;
                        Log.d("btn","soy el boto");
                        break;
                    case KeyEvent.KEYCODE_BUTTON_A:
                        //mRenderer.MoveUpward( -5f ); ;
                        //mRenderer.ReiniciarPosiciones();
                        System.out.println("LETRA AAAAAAAAAAAAAAAAAAAAAAAA");
                        banderaReinicio = true;
                        Log.d("btn","soy el botn");
                        break;

                    case KeyEvent.KEYCODE_BUTTON_X:
                        //mRenderer.MoveUpward( -5f );
                        Log.d("control","Abajo");
                        Log.d("btn","soy el soy el oton");
                        break;
                }
            }
            if (handled) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    // ----- Para mando conectado (Pad direccional o cruceta) ----------------------------
    Dpad dpad = new Dpad();

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {

        //Log.d("1","soy el primeroooooooooooooooooo");

        //DPAD
        if (Dpad.isDpadDevice(event)) {
            int press = dpad.getDirectionPressed(event);
            //System.out.println("DEFINIVITAVEMNTO ENTRO EL SWITCH");
            switch (press) {
                case LEFT:
                    StrafeRight(-DPAD_STEP);
                    return true;
                case RIGHT:
                    StrafeRight(DPAD_STEP);
                    return true;
                case UP:
                    MoveForward(DPAD_STEP);
                    return true;
                case DOWN:
                    MoveForward(-DPAD_STEP);
                    return true;
            }
        }

        // ----- Para mando conectado (Joystick) ----------------------------

        float x = event.getX();
        float y = event.getY();

        // Joystick izquierdo: actualizamos estados para el movimiento fluido en onNewFrame
        joystickLeftX = applyDeadZone(getCenteredAxis(event, event.getDevice(), MotionEvent.AXIS_X, -1));
        joystickLeftY = applyDeadZone(getCenteredAxis(event, event.getDevice(), MotionEvent.AXIS_Y, -1));

        // Rotación manual deshabilitada: la cámara solo gira con el giroscopio
        /*
        float rightX = applyDeadZone(getCenteredAxis(event, event.getDevice(), MotionEvent.AXIS_Z, -1));
        float rightY = applyDeadZone(getCenteredAxis(event, event.getDevice(), MotionEvent.AXIS_RZ, -1));

        if (rightX != 0.0f || rightY != 0.0f) {
            RotateCamera(rightX * LOOK_SPEED, rightY * LOOK_SPEED);
        }
        */

        previousX = x;
        previousY = y;

        // --------------------------------------------
        // --------------------------------------------
        // --------------------------------------------
        // --------------------------------------------

        if(event.getAction()==MotionEvent.AXIS_RTRIGGER){
            Log.d("control","Arriba");
        }
        return true;

    }

    // Version 2.0. Detecta cual de los dos Joysticks esta siendo utilizado, para
    // realizar movimientos diferenciados entre ambos

    private String processJoystickInput(MotionEvent event, int historyPos) {

        //Log.d("2","soy el segundooooooooooooooooooooooooo");

        InputDevice inputDevice = event.getDevice();
        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        float x = getCenteredAxis(event, inputDevice,MotionEvent.AXIS_X, historyPos);
        if (x == 0) {
            x = getCenteredAxis(event, inputDevice,MotionEvent.AXIS_Z, historyPos);
            if (x != 0) {
                //Log.d("control", "Derecho");
                Joystick_Izquierdo_Derecho=1;
            }
        } else {
            //Log.d("control","Izquierdo");
            Joystick_Izquierdo_Derecho=0;
        }
        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
        float y = getCenteredAxis(event, inputDevice,MotionEvent.AXIS_Y, historyPos);
        if (y == 0) {
            y = getCenteredAxis(event, inputDevice,MotionEvent.AXIS_RZ, historyPos);
            if (y != 0) {
                //Log.d("control", "Derecho");
                Joystick_Izquierdo_Derecho=1;
            }
        }
        else {
            Log.d("control","Izquierdo");
            //Joystick_Izquierdo_Derecho=0;

        }
        String coordenada = "("+(int)x+","+(int)y+")";
        return coordenada;
        // Update the ship object based on the new x and y values
    }


    private static float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {

        //Log.d("5","soy el terceroooooooooooooo");

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


    public void StrafeRight(float Distance )
    {
        updateCameraVectors();
        Position = suma(Position,multiplicacion(RightVector,Distance));
        limitPlayerPosition();
    }

    public void MoveForward(float Distance )
    {
        updateCameraVectors();
        Position = suma(Position,multiplicacion(ViewDir,Distance));
        limitPlayerPosition();
    }

    public void RotateCamera(float deltaYaw, float deltaPitch)
    {
        yawDegrees += deltaYaw;
        pitchDegrees -= deltaPitch;

        if (pitchDegrees > 35.0f) {
            pitchDegrees = 35.0f;
        } else if (pitchDegrees < -25.0f) {
            pitchDegrees = -25.0f;
        }

        updateCameraVectors();
    }

    private void resetPlayerPosition()
    {
        Position = new float[3];
        Position[0] = START_X;
        Position[1] = START_Y;
        Position[2] = START_Z;

        yawDegrees = 0.0f;
        pitchDegrees = 0.0f;

        joystickLeftX = 0.0f;
        joystickLeftY = 0.0f;

        UpVector =  new float[3];
        UpVector[0] = 0.0f;
        UpVector[1] = 1.0f;
        UpVector[2] = 0.0f;

        updateCameraVectors();
    }

    private void updateCameraVectors()
    {
        float yawRadians = yawDegrees * PIdiv180;
        float pitchRadians = pitchDegrees * PIdiv180;

        ViewDir[0] = (float)(Math.sin(yawRadians) * Math.cos(pitchRadians));
        ViewDir[1] = (float)(Math.sin(pitchRadians));
        ViewDir[2] = (float)(-Math.cos(yawRadians) * Math.cos(pitchRadians));
        ViewDir = Normalize(ViewDir);

        // Vector derecho horizontal para que el strafe no suba ni baje al mirar arriba/abajo.
        RightVector[0] = (float)Math.cos(yawRadians);
        RightVector[1] = 0.0f;
        RightVector[2] = (float)Math.sin(yawRadians);
        RightVector = Normalize(RightVector);
    }

    private void limitPlayerPosition()
    {
        Position[0] = clamp(Position[0], MIN_X, MAX_X);
        Position[1] = clamp(Position[1], MIN_Y, MAX_Y);
        Position[2] = clamp(Position[2], MIN_Z, MAX_Z);
    }

    private float applyDeadZone(float value)
    {
        return Math.abs(value) < JOYSTICK_DEAD_ZONE ? 0.0f : value;
    }

    private float clamp(float value, float min, float max)
    {
        return Math.max(min, Math.min(max, value));
    }

    private float[] multiplicacion(float[] v,float r){
        float[] res = new float[3];
        res[0] = v[0]*r;
        res[1] = v[1]*r;
        res[2] = v[2]*r;
        return res;
    }

    private float[] suma(float[] v,float [] u){
        float[] res = new float[3];
        res[0] = v[0]+u[0];
        res[1] = v[1]+u[1];
        res[2] = v[2]+u[2];
        return res;
    }

    private float[] Normalize( float[] v)
    {
        float[] res = new float[3];
        float l = GetF3dVectorLength(v);
        if (l == 0.0f){
            res[0] = 0.0f;
            res[1] = 0.0f;
            res[2] = 0.0f;
        }else {
            res[0] = v[0] / l;
            res[1] = v[1] / l;
            res[2] = v[2] / l;
        }
        return res;
    }

    float GetF3dVectorLength(float[] v)
    {
        return (float)(Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]));
    }

    private float[] CrossProduct (float[] u, float[] v)
    {
        float[] resVector = new float[3];
        resVector[0] = u[1]*v[2] - u[2]*v[1];
        resVector[1] = u[2]*v[0] - u[0]*v[2];
        resVector[2] = u[0]*v[1] - u[1]*v[0];

        return resVector;
    }



    private float[] resta(float[] v,float [] u){
        float[] res = new float[3];
        res[0] = v[0]-u[0];
        res[1] = v[1]-u[1];
        res[2] = v[2]-u[2];
        return res;
    }

}