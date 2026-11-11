package upv_pm.may_ago_26.iti_271503.pgu2_eq03;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Renderer del recorrido TAMUX.
 *
 * Carga la geometría desde un .obj SEPARADA en partes (por objeto y material),
 * de modo que puedes darle a cada pared / piso / escalón su propio COLOR o su
 * propia IMAGEN. Por defecto cada parte usa el color original de Tinkercad.
 *
 * Dónde personalizar: método definirEstilos() (abajo).
 *
 * Conserva toda la navegación: MoveForward / StrafeRight / MoveUpward /
 * RotateY / RotateX / MoveUpward / ReiniciarPosiciones / setModoVR / setOrientation.
 *
 * REQUISITO: copia tu tinker.obj en res/raw/  (queda como R.raw.tinker).
 */
public class CubeRenderer implements GLSurfaceView.Renderer {

    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] mvpMatrix = new float[16];

    private int programHandle;
    private int mvpMatrixHandle;
    private int textureUniformHandle;
    private int positionHandle;
    private int textureCoordinateHandle;

    private static final int POSITION_DATA_SIZE = 3;
    private static final int TEXTURE_COORDINATE_DATA_SIZE = 2;

    public static float screenWidth;
    public static float screenHeight;

    // =====================================================================
    //  MODO VR (dos ojos) — AJUSTES DE CÁMARA
    //
    //  ESCALA DEL MODELO: la puerta del lobby mide 40 unidades de alto y una
    //  puerta real mide ~2.10 m, así que 1 unidad ≈ 5.25 cm.
    //
    //  SEPARACION_OJOS es la MITAD de la distancia entre pupilas. La distancia
    //  real entre los ojos de una persona es de ~6.4 cm, o sea 1.2 unidades,
    //  y cada ojo se desplaza la mitad: 0.6.
    //
    //  Antes valía 22 (¡2.3 metros entre ojo y ojo!). Por eso cada ojo veía una
    //  imagen distinta imposible de fusionar y, al acercarte a un muro, un ojo
    //  quedaba del OTRO LADO de la pared y veía lo de atrás.
    // =====================================================================
    private volatile boolean modoVR = true;
    private static final float SEPARACION_OJOS = 0.6f;

    /** Campo de visión vertical (grados). En VR cada ojo ocupa media pantalla. */
    private static final float FOV_VR         = 78f;
    private static final float FOV_NORMAL     = 65f;
    /** Plano cercano: más lejos = menos parpadeo de profundidad (z-fighting). */
    private static final float PLANO_CERCANO  = 2.5f;
    private static final float PLANO_LEJANO   = 8000f;

    /** El minimapa se puede mostrar/ocultar (botón del mando o de pantalla). */
    private volatile boolean mapaVisible = true;

    // --- Cámara estilo FPS ---
    private final float[] position = new float[3];
    private float manualYawDeg = 0f;
    private float manualPitchDeg = 0f;

    private float startX, startY, startZ;

    private int logFrame = 0;

    /** Pon true si quieres volver a ver en el Logcat las coordenadas del jugador. */
    private static final boolean LOG_POSICION = false;

    // =====================================================================
    //  FÍSICA DE CAMINATA (aplica en modo Minecraft y en VR)
    // =====================================================================
    /** Altura de los ojos sobre el piso (te paras a esta altura). */
    private static final float EYE_HEIGHT = 35f;// 25F
    /** Escalón máximo que puedes subir caminando. */
    private static final float STEP_UP = 22f;
    /** Qué tan rápido bajas/caes (unidades por SEGUNDO). */
    private static final float FALL_SPEED = 700f;
    /**
     * Radio del cuerpo para chocar con paredes.
     * Se subió de 6 a 7.5 para no quedar con la nariz pegada al muro (en VR eso
     * hacía que el plano cercano cortara la pared). No se puede subir mucho más:
     * el hueco que comunica el lobby con el pasillo mide 20 unidades de ancho.
     */
    private static final float BODY_RADIUS = 7.5f;
    private static final float BODY_RADIUS_SQ = BODY_RADIUS * BODY_RADIUS;

    // --- Sensor (solo modo VR) ---
    private boolean usarSensor = false;
    private float sensorYawDeg = 0f;
    private float sensorPitchDeg = 0f;

    // --- Modelo cargado del OBJ ---
    private ObjLoader model;
    private int[] partTexture;   // textura GL final por cada parte del modelo

    // =====================================================================
    //  LIBRO DE ESTILOS (se llena en definirEstilos()).
    //  Clave = nombre del objeto del OBJ ("obj_0", "obj_10", ...).
    //  Prioridad al dibujar:  imagen  >  color  >  color original de Tinkercad.
    // =====================================================================
    //  IMPORTANTE: la clave es objeto + material ("obj_85|color_40919"), NO solo el
    //  nombre del objeto. Un mismo obj_N puede tener VARIOS materiales y cada uno es
    //  una parte distinta; si se indexara solo por nombre, texturizar una parte
    //  borraría el color de todas sus hermanas.
    private final Map<String, Integer> imagenPorParte = new HashMap<>(); // -> R.drawable.xxx
    private final Map<String, Integer> colorPorParte  = new HashMap<>(); // -> 0xRRGGBB

    // Cachés para no duplicar texturas iguales.
    private final Map<Integer, Integer> cacheColor   = new HashMap<>(); // 0xRRGGBB -> texGL
    private final Map<Integer, Integer> cacheImagen  = new HashMap<>(); // drawable -> texGL

    // =====================================================================
    //  MURALES: fotos pegadas sobre una pared del modelo.
    //
    //  Un mural es un simple rectángulo (2 triángulos) que se dibuja unos
    //  milímetros DELANTE de la pared, con sus propias coordenadas de textura
    //  0..1, de modo que la foto se ve UNA sola vez y con su proporción real
    //  (las partes normales del OBJ repiten la textura cada 40 unidades, lo
    //  cual sirve para pisos y muros, pero deformaría una fotografía).
    //
    //  No toca la geometría del OBJ ni la física: el jugador sigue chocando
    //  con la pared original exactamente igual que antes.
    // =====================================================================

    /** Separación del mural respecto a la pared (evita parpadeo por z-fighting). */
    private static final float SEPARACION_MURAL = 1.5f;

    /** Un mural ya listo para dibujar. */
    private static final class Mural {
        final FloatBuffer positions;
        final FloatBuffer texCoords;
        final int vertexCount;
        final int texture;

        Mural(FloatBuffer positions, FloatBuffer texCoords, int vertexCount, int texture) {
            this.positions = positions;
            this.texCoords = texCoords;
            this.vertexCount = vertexCount;
            this.texture = texture;
        }
    }

    private final List<Mural> murales = new ArrayList<>();
    private final Map<Integer, Integer> cacheMural = new HashMap<>(); // drawable -> texGL


    // =====================================================================
    //  SPAWN dentro del cuarto de las saliditas (coords Y-up).
    // =====================================================================
    private static final float SPAWN_X   = 422;
    private static final float SPAWN_Y   = 136;
    private static final float SPAWN_Z   = 136;
    private static final float SPAWN_YAW = -0;

    public CubeRenderer(Context context) {
        this.context = context;
    }

    public void setModoVR(boolean activado) { this.modoVR = activado; }

    // =====================================================================
    //  ESTADO DE ENTRADA (lo escribe el hilo de UI, lo consume el GLThread)
    //
    //  Antes MainActivity llamaba MoveForward/StrafeRight directamente desde el
    //  hilo principal. Eso recorría TODA la geometría dentro de puedeEstar() en
    //  el hilo que debe responder a los eventos -> ANR ("Input dispatching timed
    //  out"). Ahora la UI solo deja marcada la intención y el movimiento se
    //  aplica una vez por frame en onDrawFrame().
    // =====================================================================
    //  IMPORTANTE: estos valores ya NO son "avance por frame" sino VELOCIDADES
    //  en unidades (o grados) por SEGUNDO. Antes se sumaban tal cual una vez por
    //  frame, así que en un teléfono a 90 fps caminabas un 50% más rápido que en
    //  uno a 60 fps. Ahora se multiplican por el tiempo real del frame y la
    //  velocidad se siente igual en cualquier dispositivo.
    private volatile float inputForward = 0f;
    private volatile float inputStrafe  = 0f;
    private volatile float inputYaw     = 0f;
    private volatile float inputPitch   = 0f;
    private volatile boolean corriendo  = false;

    public void setInputForward(float v) { inputForward = v; }
    public void setInputStrafe (float v) { inputStrafe  = v; }
    public void setInputYaw    (float v) { inputYaw     = v; }
    public void setInputPitch  (float v) { inputPitch   = v; }
    public void setCorriendo(boolean v)  { corriendo    = v; }

    /** Multiplicador al mantener el botón de correr. */
    private static final float MULT_CORRER = 2.0f;

    /** Empujón puntual (gamepad, teclado): se acumula y se aplica en el próximo frame. */
    private volatile float pulseForward = 0f;
    private volatile float pulseStrafe  = 0f;
    public void pulseForward(float v) { pulseForward += v; }
    public void pulseStrafe (float v) { pulseStrafe  += v; }

    // --- Reloj del frame (para que la velocidad no dependa de los fps) ---
    private long ultimoFrameNs = 0L;
    private float dt = 1f / 60f;

    private void actualizarReloj() {
        long ahora = System.nanoTime();
        if (ultimoFrameNs == 0L) { ultimoFrameNs = ahora; dt = 1f / 60f; return; }
        dt = (ahora - ultimoFrameNs) / 1_000_000_000f;
        ultimoFrameNs = ahora;
        // Si la app se congela un momento (carga, cambio de app), no queremos
        // teletransportarnos: se limita el salto máximo de un frame.
        if (dt > 0.1f) dt = 0.1f;
        if (dt < 0.001f) dt = 0.001f;
    }

    /** Consume el estado de entrada. Sólo se llama desde el GLThread. */
    private void aplicarEntrada() {
        float mult = (corriendo ? MULT_CORRER : 1f) * dt;

        if (inputForward != 0f) MoveForward(inputForward * mult);
        if (inputStrafe  != 0f) StrafeRight(inputStrafe  * mult);
        if (inputYaw     != 0f) RotateY(inputYaw   * dt);
        if (inputPitch   != 0f) RotateX(inputPitch * dt);

        float pf = pulseForward, ps = pulseStrafe;
        if (pf != 0f) { pulseForward = 0f; MoveForward(pf); }
        if (ps != 0f) { pulseStrafe  = 0f; StrafeRight(ps); }
    }

    // =====================================================================
    //  MAPA (minimapa) — se enciende/apaga desde el mando o la pantalla
    // =====================================================================
    public void setMapaVisible(boolean v) { mapaVisible = v; }
    public boolean isMapaVisible()        { return mapaVisible; }
    /** Alterna el mapa y devuelve el estado nuevo. */
    public boolean alternarMapa()         { mapaVisible = !mapaVisible; return mapaVisible; }

    // =====================================================================
    //  PERSONALIZACIÓN: aquí decides el color/imagen de cada parte.
    //  Puedes editar libremente. Ejemplos incluidos abajo.
    // =====================================================================
    private void definirEstilos() {
        // --- IMÁGENES (textura) por objeto ---
        // Los pisos del cuarto con textura de piso:
        texturizarObjeto("obj_10", R.drawable.piso_lobby);
        texturizarObjeto("obj_11", R.drawable.piso_lobby);
        texturizarObjeto("obj_14", R.drawable.piso_lobby);

        // --- ESCALERA PRINCIPAL: madera ---
        // obj_13 tenía la textura del piso del lobby, y por eso la escalera se
        // veía blanca de loseta mientras el tramo de arriba (obj_12) quedaba
        // verde olivo: dos tramos de la MISMA escalera con distinto acabado.
        // Ahora los dos llevan madera, como los peldaños reales del TAMUX.
        texturizarObjeto("obj_12", R.drawable.madera_escalera);
        texturizarObjeto("obj_13", R.drawable.madera_escalera);

        // Texturizar automáticamente (Sala 1 y Paredes Naranjas)
        if (model != null) {
            for (ObjLoader.Part p : model.parts) {
                // El color original gris oscuro/negro en Tinkercad es RGB(43, 46, 49)
                // Muros claros del ala este: llevaban color plano mientras los
                // oscuros sí tenían textura, y esa mezcla de acabados se nota
                // al pasar de una sala a otra. Ahora los dos llevan material.
                if (p.colorR == 221 && p.colorG == 226 && p.colorB == 228
                        && (p.maxY - p.minY) >= 15f) {
                    texturizarParte(p, R.drawable.pared_clara);
                }
                if (p.colorR == 43 && p.colorG == 46 && p.colorB == 49) {
                    float dy = p.maxY - p.minY;
                    // Si es plano (piso/techo), le ponemos textura de piso. Si es alto, de pared/fondo.
                    if (dy < 15f) {
                        texturizarParte(p, R.drawable.piso_sala);
                    } else if ((p.minX + p.maxX) * 0.5f < LIMITE_SALA_1) {
                        // Muro de la sala del universo: mismo negro que el resto
                        // del museo pero con estrellas tenues.
                        //
                        // La regla es POR ZONA, no por lista de nombres. Antes
                        // iban con estrellas cinco muros elegidos a mano y sus
                        // vecinos quedaban en negro liso, así que se notaba
                        // dónde terminaba uno y empezaba el otro. Así todos los
                        // muros negros de esa sala llevan el mismo acabado y las
                        // juntas dejan de saltar a la vista.
                        texturizarParte(p, R.drawable.pared_estrellas);
                    } else {
                        texturizarParte(p, R.drawable.negro_suave);
                    }
                }

                // El nuevo color naranja en Tinkercad es RGB(227, 91, 34)
                if (p.colorR == 227 && p.colorG == 91 && p.colorB == 34) {
                    // El usuario pidió cambiar las paredes naranjas por el efecto de negro
                    texturizarParte(p, R.drawable.negro_suave);
                }
            }
        }

        // --- Piso café SÓLO en la nave del mural ---
        // Es el piso que se ve al pie de la escalera en la foto: concreto
        // pulido color tierra. Va DESPUÉS del ciclo automático para ganarle la
        // asignación de piso_sala que le tocaría por su color.
        //
        // obj_16 NO lleva café a propósito: es el piso que sigue pasando la
        // puerta, ya rumbo a la sala 1, y ahí el museo es negro. Así el cambio
        // de material coincide con el umbral, igual que en el edificio real.
        texturizarObjeto("obj_15", R.drawable.piso_cafe);

        // --- Plataformas de los planetas de la sala 1 ---
        // En Tinkercad quedaron café. En el museo son bases oscuras de concreto
        // pulido, así que se les pone un gris carbón mate. Sin más adornos: la
        // pieza que se tiene que ver es el planeta, no la base.
        for (String[] grupo : GRUPOS_PLANETAS) {
            texturizarObjeto(grupo[0], R.drawable.pedestal_oscuro);
        }

        // --- COLORES sólidos por objeto (0xRRGGBB) ---
        // (Las escaleras antiguas obj_32..obj_36 ya no necesitan ser pintadas de naranja
        // ya que el nuevo modelo tiene sus propios colores y el usuario quiere negro)

        // Ejemplo de una pared pintada a mano:
        // pintarObjeto("obj_0", 0x3366CC);

        // Todo lo que NO menciones aquí conserva su color original de Tinkercad.
    }

    /** Clave única de una parte: objeto + material. */
    private static String claveDe(ObjLoader.Part p) {
        return p.objectName + "|" + p.material;
    }

    /** Asigna una imagen (drawable) a UNA parte concreta (objeto + material). */
    public void texturizarParte(ObjLoader.Part p, int drawableResId) {
        String k = claveDe(p);
        imagenPorParte.put(k, drawableResId);
        colorPorParte.remove(k); // la imagen gana
    }

    /** Asigna una imagen a TODAS las partes de un objeto (todos sus materiales). */
    public void texturizarObjeto(String objectName, int drawableResId) {
        if (model == null) return;
        for (ObjLoader.Part p : model.parts) {
            if (p.objectName.equals(objectName)) texturizarParte(p, drawableResId);
        }
    }

    /** Asigna un color sólido (0xRRGGBB) a UNA parte concreta. */
    public void pintarParte(ObjLoader.Part p, int rgb) {
        String k = claveDe(p);
        colorPorParte.put(k, rgb);
        imagenPorParte.remove(k);
    }

    /** Asigna un color sólido a TODAS las partes de un objeto. */
    public void pintarObjeto(String objectName, int rgb) {
        if (model == null) return;
        for (ObjLoader.Part p : model.parts) {
            if (p.objectName.equals(objectName)) pintarParte(p, rgb);
        }
    }

    // =====================================================================
    //  PERSONALIZACIÓN: aquí decides qué fotos van pegadas a qué pared.
    // =====================================================================
    private void definirMurales() {
        // Si Android recrea la superficie GL, esto se vuelve a ejecutar:
        // limpiamos para no acumular murales duplicados ni texturas muertas.
        murales.clear();
        cacheMural.clear();

        // --- Pared del fondo, al pie de las escaleras principales ---
        // Es la parte "obj_23" del modelo: la pared clara que tienes de frente
        // en cuanto bajas las primeras escaleras (las que están después del
        // lobby). Se le pasan sus medidas EXACTAS para que la foto tape el muro
        // completo, de esquina a esquina, y no parezca un cuadro colgado.
        //
        //      X : 255.3 .. 674.3     (ancho total del muro)
        //      Y :  -4.0 .. 184.0     (alto total; el piso de esa sala está en
        //                              Y = 7 y el techo en Y = 173)
        //      Z : -438.0 .. -435.0   (la cara que TÚ ves es la de Z = -435)
        cubrirParedZ(
                R.drawable.mural_museo, // foto del museo (UNIVERSO / VIDA / ...)
                255.3f, 674.3f,         // de un extremo a otro del muro (X)
                -4f, 184f,              // del suelo al remate del muro (Y)
                -435f,                  // Z de la cara visible de la pared
                85f / 64f);             // proporción real de la foto (2040x1536)

        // Para tapar otra pared, repite la llamada con sus medidas. Si la pared
        // mira hacia -Z (la ves por el otro lado), añade "false" al final.

        // -----------------------------------------------------------------
        //  TECHO DEL EDIFICIO COMPLETO
        //
        //  El modelo de Tinkercad no trae techo en ningún lado: los muros del
        //  lobby rematan en Y = 141 y de ahí para arriba estaba abierto al
        //  vacío. Aquí se construye la cubierta entera, como murales pero
        //  horizontales y con la textura repetida.
        //
        //  Va en cinco rectángulos que embaldosan TODA la huella del modelo
        //  (X -712..674.3, Z -552..416) sin dejar hueco y sin encimarse: dos
        //  planos a la misma altura ocupando el mismo sitio se pelean por la
        //  profundidad y el techo parpadea.
        //
        //  Cuatro van a 182 y sólo el del lobby a 184, que es un poco más alto.
        //  Ese escalón de 2 unidades lo tapan los faldones de aquí abajo.
        //
        //  Comprobado: NINGUNA pieza del modelo queda por encima de 182, así
        //  que la cubierta no esconde nada. Todo lo que la atraviesa son muros,
        //  y que asomen es justo lo que evita rendijas.
        // -----------------------------------------------------------------
        cubrirTechoY(R.drawable.techo_vigas,          // lobby (el más alto)
                LOBBY_X_MIN, LOBBY_X_MAX, LOBBY_Z_MIN, LOBBY_Z_MAX,
                TECHO_LOBBY_Y, MOSAICO_TECHO);

        cubrirTechoY(R.drawable.techo_vigas,          // todo el poniente
                HUELLA_X_MIN, LOBBY_X_MIN, HUELLA_Z_MIN, HUELLA_Z_MAX,
                TECHO_GENERAL_Y, MOSAICO_TECHO);

        cubrirTechoY(R.drawable.techo_vigas,          // pasillo y sala del mural
                LOBBY_X_MIN, HUELLA_X_MAX, HUELLA_Z_MIN, LOBBY_Z_MIN,
                TECHO_GENERAL_Y, MOSAICO_TECHO);

        cubrirTechoY(R.drawable.techo_vigas,          // lo que queda al norte
                LOBBY_X_MIN, HUELLA_X_MAX, LOBBY_Z_MAX, HUELLA_Z_MAX,
                TECHO_GENERAL_Y, MOSAICO_TECHO);

        cubrirTechoY(R.drawable.techo_vigas,          // franja al oriente del lobby
                LOBBY_X_MAX, HUELLA_X_MAX, LOBBY_Z_MIN, LOBBY_Z_MAX,
                TECHO_GENERAL_Y, MOSAICO_TECHO);

        // -----------------------------------------------------------------
        //  FALDÓN DEL LOBBY (del remate de los muros al techo)
        //
        //  Los muros del lobby sólo llegan a Y = 141, así que entre ellos y el
        //  techo quedaba una franja abierta por la que se veía el vacío. Se
        //  cierra con la MISMA lámina pero con las rendijas verticales, que es
        //  como se ve el muro en el TAMUX real. El hueco que comunica con el
        //  pasillo (X = 411..431) llega sólo hasta 141, así que no lo tapa.
        // -----------------------------------------------------------------
        cubrirMuroZ(R.drawable.techo_lamina, LOBBY_X_MIN, LOBBY_X_MAX,
                REMATE_MUROS, TECHO_LOBBY_Y, LOBBY_Z_MAX, 16f); // lado de la entrada
        cubrirMuroZ(R.drawable.techo_lamina, LOBBY_X_MIN, 665.3f,
                REMATE_MUROS, TECHO_LOBBY_Y, LOBBY_Z_MIN, 16f); // muro hacia el pasillo
        cubrirMuroX(R.drawable.techo_lamina, LOBBY_Z_MIN, LOBBY_Z_MAX,
                REMATE_MUROS, TECHO_LOBBY_Y, LOBBY_X_MIN, 16f); // muro oeste
        cubrirMuroX(R.drawable.techo_lamina, LOBBY_Z_MIN, LOBBY_Z_MAX,
                REMATE_MUROS, TECHO_LOBBY_Y, LOBBY_X_MAX, 16f); // muro este

        // -----------------------------------------------------------------
        //  PARED DEL PATIO (foto real) — la que queda JUSTO DETRÁS de ti
        //  al aparecer en el cuarto inicial.
        //
        //  Son los dos paños macizos que flanquean las puertas de cristal.
        //  Se deja libre el centro (X 401..441) para no tapar las puertas.
        //  El "false" indica que la cara visible mira hacia -Z; sin él la foto
        //  saldría espejeada.
        // -----------------------------------------------------------------
        final float ASPECTO_PATIO = 960f / 723f;   // proporción real de la foto
        cubrirParedZ(R.drawable.pared_patio, 341.3f, 401.3f, 101f, 141f, 161f,
                ASPECTO_PATIO, false);
        cubrirParedZ(R.drawable.pared_patio, 441.3f, 501.3f, 101f, 141f, 161f,
                ASPECTO_PATIO, false);

        // -----------------------------------------------------------------
        //  ZÓCALO DE LOS PEDESTALES DE LA SALA 1
        //
        //  Medido sobre el OBJ: cada plataforma es una PIRÁMIDE INVERTIDA.
        //  Mide 93 unidades de ancho en la tapa y CERO en la base: toca el
        //  piso en un solo punto. Por eso el planeta se veía flotando sobre
        //  una placa; así no hay forma de que se lea apoyado.
        //
        //  En vez de deformar tu geometría, se le mete por dentro un tambor
        //  que sí apoya en el suelo con todo su ancho. El tambor sube justo
        //  hasta donde la pirámide alcanza ese mismo radio, así que una sale
        //  de la otra sin escalón. La pieza original ni se toca ni se oculta:
        //  sólo se le agrega la base que le faltaba, como el bloque macizo de
        //  la foto del museo.
        // -----------------------------------------------------------------
        for (String[] grupo : GRUPOS_PLANETAS) {
            construirZocalo(grupo[0]);
        }

        // -----------------------------------------------------------------
        //  CANTO ENTRE LOS DOS NIVELES DE LA SALA 1
        //
        //  La sala tiene dos pisos a distinta altura: obj_96 en Y = -145 y
        //  obj_27/obj_72 en Y = -174, con 29 unidades (1.5 m) de diferencia.
        //  Pero obj_96 es una losa de UNA unidad de espesor, así que su borde
        //  no tenía cara vertical: se veía el canto y debajo el vacío.
        //
        //  Se cierra con concreto en X = -182.7 (el borde de la losa alta), en
        //  el tramo Z = -529..-101, que es justo donde hay piso abajo y no lo
        //  tapa ya un muro. Se corta en Z = -101 para no estorbar la rampa
        //  obj_83, que es por donde se baja entre niveles.
        // -----------------------------------------------------------------
        cubrirMuroX(R.drawable.pedestal_oscuro,
                -529f, -101f,      // tramo con piso abajo y canto descubierto
                -174f, -145f,      // del piso bajo al alto
                -182.7f, 40f);

        // -----------------------------------------------------------------
        //  PASAMANOS DE LA ESCALERA PRINCIPAL
        //
        //  Como en la foto: uno pegado a cada muro y otro partiendo la
        //  escalera por en medio. Van sobre las dos secciones (obj_12 arriba y
        //  obj_13 abajo), siguiendo la pendiente ya corregida de cada una.
        //
        //  Son decorado, no geometría del modelo: NO entran en el cálculo de
        //  colisiones, así que se ven pero no te frenan al subir ni te empujan
        //  contra el muro. La escalera se sigue caminando igual que antes.
        // -----------------------------------------------------------------
        for (float x : new float[]{ESC_X_IZQ, ESC_X_CENTRO, ESC_X_DER}) {
            construirBarandal(x, -20f, 87f, -152f, 50f, ALTURA_PASAMANOS, 34f);
            construirBarandal(x, -153f, 50f, -261f,  7f, ALTURA_PASAMANOS, 34f);
        }

        // -----------------------------------------------------------------
        //  SALA 1: ATRILES INFORMATIVOS Y BARANDAL DEL DESNIVEL
        //
        //  Un atril junto a cada planeta, como los del museo. La posición de
        //  cada uno se eligió midiendo dónde hay hueco de verdad: los cuatro
        //  van a un costado donde no choca con muros, rampas ni vitrinas.
        //  La tapa sube hacia el planeta, así que la lees de pie desde afuera.
        // -----------------------------------------------------------------
        construirConsola(-382.7f, -108f, -145f,  0f, -1f);   // planeta azul
        construirConsola(-503.7f,  114f, -145f,  0f,  1f);   // sol
        construirConsola(-285.7f,   53f, -145f,  0f,  1f);   // luna
        construirConsola(  -0.7f, -321f, -174f,  0f, -1f);   // planeta azul del fondo

        // El desnivel de 29 unidades (1.5 m) entre los dos pisos de la sala
        // pedía protección, igual que los barandales que rodean el foso en el
        // museo. Va sobre el piso alto, retirado del borde.
        construirBarandal(-188f, -520f, -145f, -110f, -145f, ALTURA_PASAMANOS, 42f);

        // -----------------------------------------------------------------
        //  TIRAS DE LUZ AZUL (sala 1)
        //
        //  En el pasillo de EL UNIVERSO la línea azul rasante sobre el murete
        //  es lo que le da carácter a la sala. Aquí se pone lo mismo: una
        //  banda delgada corriendo por el canto del desnivel.
        //
        //  Como el proyecto no tiene iluminación, no ilumina de verdad; es una
        //  textura azul intensa, que a esta escala se lee igual que la tira LED
        //  de la foto. Va como decorado, así que no estorba al caminar.
        // -----------------------------------------------------------------
        cubrirBarraInclinada(R.drawable.luz_azul, -181.9f,
                -529f, -149f, -101f, -149f, 3.5f);

        // -----------------------------------------------------------------
        //  PUERTAS CORREDIZAS DEL VANO DEL LOBBY
        //
        //  El vano ya quedó del ancho de la entrada, pero era un agujero pelón
        //  en un muro blanco. Aquí se le arma el herraje para que se lea como
        //  unas corredizas ABIERTAS, con las hojas recogidas a los lados:
        //
        //     - jambas: dos montantes metálicos en los cantos del vano
        //     - dintel: la viga que lo remata arriba
        //     - riel:   la guía por donde correrían las hojas
        //     - hojas:  dos paños de cristal estacionados contra el muro
        //
        //  Todo va como decorado, así que NO estorba el paso: cruzas el vano
        //  completo igual que antes.
        // -----------------------------------------------------------------
        // Las hojas van del lado del LOBBY (Z = 100.3, justo delante de la cara
        // del muro en 99). Puestas del otro lado quedaban tapadas por el propio
        // muro y no se veían al acercarte, que es cuando importan.
        final float Z_HERRAJE = 100.3f;
        final float Y_PISO = 101f, Y_DINTEL = 141f;

        // Jambas, metidas en el espesor del muro para verse de los dos lados.
        cubrirPrismaVertical(R.drawable.metal_blanco, VANO_X_MIN, 96.5f, 2.6f,
                Y_PISO, Y_DINTEL, 8, 40f);
        cubrirPrismaVertical(R.drawable.metal_blanco, VANO_X_MAX, 96.5f, 2.6f,
                Y_PISO, Y_DINTEL, 8, 40f);

        // Dintel y riel de corredera.
        cubrirMuroZ(R.drawable.metal_blanco, VANO_X_MIN - 20f, VANO_X_MAX + 20f,
                Y_DINTEL - 4f, Y_DINTEL, Z_HERRAJE, 40f);
        cubrirBarraInclinada(R.drawable.metal_blanco, Z_HERRAJE + 0.6f,
                VANO_X_MIN - 20f, Y_DINTEL - 6f, VANO_X_MAX + 20f, Y_DINTEL - 6f, 2.2f);

        // Hojas de cristal recogidas: cada una junto a su lado del vano.
        cubrirMuroZ(R.drawable.cristal, VANO_X_MIN - 20f, VANO_X_MIN,
                Y_PISO + 1f, Y_DINTEL - 7f, Z_HERRAJE, 40f);
        cubrirMuroZ(R.drawable.cristal, VANO_X_MAX, VANO_X_MAX + 20f,
                Y_PISO + 1f, Y_DINTEL - 7f, Z_HERRAJE, 40f);

        // -----------------------------------------------------------------
        //  ENVOLVENTE PERIMETRAL (relleno de fondo)
        //
        //  El modelo de Tinkercad no cierra por completo: los muros de fachada
        //  no llegan a tocarse en las esquinas y quedan rendijas por las que se
        //  veía el color de fondo, o sea un agujero negro plano.
        //
        //  Se auditó lanzando rayos desde 88 puntos de piso realmente
        //  transitables en 120 direcciones cada uno: el 14.4% se escapaba al
        //  vacío. Todos por los costados; por arriba ya no, gracias al techo.
        //
        //  En vez de perseguir hueco por hueco, se cierra la caja completa:
        //  cuatro paños desde debajo del piso más profundo hasta el techo,
        //  justo por fuera de todo. Sólo se asoman por donde hoy hay agujero,
        //  y ahí se ven como pared de fondo en vez de como vacío.
        //
        //  El acabado sigue la misma regla que el resto del museo: estrellas
        //  del lado de la sala 1, negro liso del lado del ala este.
        // -----------------------------------------------------------------
        cubrirMuroX(R.drawable.pared_estrellas, HUELLA_Z_MIN, HUELLA_Z_MAX,
                HUELLA_Y_MIN, TECHO_GENERAL_Y, HUELLA_X_MIN, 40f);
        cubrirMuroX(R.drawable.negro_suave, HUELLA_Z_MIN, HUELLA_Z_MAX,
                HUELLA_Y_MIN, TECHO_GENERAL_Y, HUELLA_X_MAX, 40f);

        for (float z : new float[]{HUELLA_Z_MIN, HUELLA_Z_MAX}) {
            cubrirMuroZ(R.drawable.pared_estrellas, HUELLA_X_MIN, LIMITE_SALA_1,
                    HUELLA_Y_MIN, TECHO_GENERAL_Y, z, 40f);
            cubrirMuroZ(R.drawable.negro_suave, LIMITE_SALA_1, HUELLA_X_MAX,
                    HUELLA_Y_MIN, TECHO_GENERAL_Y, z, 40f);
        }

        // Marco de cada hoja, para que no parezcan vidrios sueltos.
        for (float[] hoja : new float[][]{
                {VANO_X_MIN - 20f, VANO_X_MIN}, {VANO_X_MAX, VANO_X_MAX + 20f}}) {
            cubrirPrismaVertical(R.drawable.metal_blanco, hoja[0], Z_HERRAJE, 1.5f,
                    Y_PISO + 1f, Y_DINTEL - 7f, 6, 40f);
            cubrirPrismaVertical(R.drawable.metal_blanco, hoja[1], Z_HERRAJE, 1.5f,
                    Y_PISO + 1f, Y_DINTEL - 7f, 6, 40f);
        }

        // -----------------------------------------------------------------
        //  LETRERO "UNIVERSO" JUNTO A LA PUERTA
        //
        //  La puerta buena resultó ser un grupo que no había identificado:
        //  obj_39..obj_42 son las hojas y obj_43..obj_45 las manijas (X ≈ 429),
        //  montadas en el muro obj_36, que corre de X = 258 a 431 en Z = -306.
        //  O sea que el acceso está EN la nave del mural, no al fondo del
        //  pasillo donde lo tenía.
        //
        //  El rótulo va sobre la cara norte de ese mismo muro, a la izquierda
        //  del hueco: ocupa de X = 302 a 390 y la puerta arranca en 411, así
        //  que queda al lado sin taparla, y se lee de frente al acercarte.
        // -----------------------------------------------------------------
        final float ALTO_LETRERO = 36f;
        final float ANCHO_LETRERO = ALTO_LETRERO * (537f / 221f);  // proporción real
        final float X_LETRERO = 346f;      // centrado en el paño libre del muro
        cubrirParedZ(R.drawable.titulo_universo,
                X_LETRERO - ANCHO_LETRERO * 0.5f, X_LETRERO + ANCHO_LETRERO * 0.5f,
                46f, 46f + ALTO_LETRERO,
                -306f, 537f / 221f, true);

        // -----------------------------------------------------------------
        //  CÉDULAS EN LOS MUROS NEGROS DE LA SALA 1
        //
        //  Los paños de obj_132 son enormes y quedaban completamente vacíos.
        //  Se cuelgan tres cuadros a la altura de la vista (el piso está en
        //  -174 y los ojos en -139), separados entre sí, más una tira de luz
        //  rasante al pie del muro como la del pasillo real. Nada más: son
        //  paredes de fondo y recargarlas se vería peor que dejarlas lisas.
        // -----------------------------------------------------------------
        final float Z_MURO_SUR = -480.9f;     // cara norte de obj_132
        for (float x : new float[]{-430f, -230f, -30f}) {
            cubrirParedZ(R.drawable.panel_astro, x - 42f, x + 42f,
                    -132f, -48f, Z_MURO_SUR, 1f, true);
        }
        cubrirMuroZ(R.drawable.luz_azul, -600f, 120f, -170f, -166f, Z_MURO_SUR, 40f);
    }

    // ---- Escalera principal: dónde van los tres pasamanos ----
    private static final float ESC_X_IZQ    = 519f;
    private static final float ESC_X_CENTRO = 590f;
    private static final float ESC_X_DER    = 661f;
    /** Altura del pasamanos sobre el escalón (≈0.9 m). */
    private static final float ALTURA_PASAMANOS = 17f;

    /**
     * Radio del tambor que se le mete por dentro a cada plataforma.
     *
     * La tapa de la pirámide mide 46.5 de radio. Con 34 el tambor ocupa casi
     * todo ese ancho, así que la plataforma se lee como un bloque macizo con
     * un remate ligeramente volado arriba, igual que la base del sol en la
     * foto del museo. Con valores mucho menores el vuelo de la pirámide tapa
     * el tambor y vuelve a parecer un plato sobre una punta.
     */
    private static final float RADIO_ZOCALO = 34f;

    private void construirZocalo(String pedestal) {
        if (model == null) return;
        float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        boolean hay = false;
        for (ObjLoader.Part p : model.parts) {
            if (!p.objectName.equals(pedestal)) continue;
            hay = true;
            minX = Math.min(minX, p.minX); maxX = Math.max(maxX, p.maxX);
            minZ = Math.min(minZ, p.minZ); maxZ = Math.max(maxZ, p.maxZ);
            minY = Math.min(minY, p.minY); maxY = Math.max(maxY, p.maxY);
        }
        if (!hay) return;

        float radioTapa = (maxX - minX) * 0.5f;
        float alto = maxY - minY;
        if (radioTapa <= RADIO_ZOCALO || alto <= 1f) return;

        // Altura a la que la pirámide ya alcanzó el radio del tambor: ahí
        // termina el tambor, para que una salga de la otra sin escalón.
        float yTope = minY + alto * (RADIO_ZOCALO / radioTapa);

        cubrirPrismaVertical(R.drawable.pedestal_oscuro,
                (minX + maxX) * 0.5f, (minZ + maxZ) * 0.5f,
                RADIO_ZOCALO, minY, yTope, 12, 40f);
    }

    // ---- Medidas del lobby (las del modelo, medidas sobre el propio OBJ) ----
    private static final float LOBBY_X_MIN   = 341.3f;
    private static final float LOBBY_X_MAX   = 501.3f;
    private static final float LOBBY_Z_MIN   =  94.0f;
    private static final float LOBBY_Z_MAX   = 166.0f;
    /** Altura a la que rematan los muros del lobby en el modelo de Tinkercad. */
    private static final float REMATE_MUROS  = 141.0f;
    /** Altura del techo de lámina (≈ 4.4 m sobre el piso del lobby). */
    private static final float TECHO_LOBBY_Y = 184.0f;

    // ---- Huella completa del modelo (medida sobre el propio OBJ) ----
    //      La cubierta se apoya en estos límites para no dejar ningún hueco.
    // Van 2 unidades por FUERA de la pieza más extrema del modelo (X -712..674.3,
    // Z -552..416). Ese margen evita que la envolvente quede a ras de un muro
    // existente: dos caras en el mismo plano se pelean por la profundidad y
    // parpadean.
    private static final float HUELLA_X_MIN = -714.0f;
    private static final float HUELLA_X_MAX =  676.3f;
    private static final float HUELLA_Z_MIN = -554.0f;
    private static final float HUELLA_Z_MAX =  418.0f;
    /** Hasta dónde baja la envolvente: por debajo del piso más profundo. */
    private static final float HUELLA_Y_MIN = -380.0f;

    /** Altura del techo en todo el edificio salvo el lobby, que va 2 más alto. */
    private static final float TECHO_GENERAL_Y = 182.0f;

    /**
     * Unidades de mundo que ocupa un mosaico de techo_vigas.png.
     *
     * Ese mosaico NO es sólo lámina: trae dibujada una armadura completa
     * (cuerdas, diagonales y el riel de lámparas), así que el valor manda dos
     * cosas a la vez y hay que elegirlo con cuidado:
     *
     *   - separación entre trabes = este número        -> 90 u ≈ 4.7 m
     *   - paso de la rendija       = este número / 24  -> 3.75 u ≈ 20 cm
     *
     * Los dos caen justo en lo que se usa de verdad en una nave así. Si lo
     * subes, las trabes se separan pero las rendijas también se ensanchan.
     */
    private static final float MOSAICO_TECHO = 90f;



    /** Unidades de mundo que ocupa un mosaico de textura por omisión. */
    private static final float UNIDADES_POR_MOSAICO = 16f;

    /**
     * Construye un TECHO horizontal (o un piso) con la textura REPETIDA.
     *
     * A diferencia de un mural, aquí la imagen no se estira para caber: se
     * repite en mosaico cada "unidadesPorMosaico" unidades, que es justo lo que
     * hace falta para una lámina acanalada. Las rendijas quedan paralelas al
     * eje Z (la textura se repite a lo largo de X).
     *
     * No añade geometría al modelo, así que NO cambia la física: sigues
     * caminando y chocando exactamente igual que antes.
     *
     * @param xMin, xMax  extremos en X de la superficie
     * @param zMin, zMax  extremos en Z de la superficie
     * @param y           altura a la que se coloca
     * @param unidadesPorMosaico tamaño de un mosaico de la textura
     */
    /**
     * Origen común de las coordenadas de textura de TODOS los techos.
     *
     * Sin esto, cada rectángulo empezaría a contar el mosaico desde su propia
     * esquina y las armaduras de un tramo no continuarían las del de al lado:
     * se verían cortadas en las juntas. Midiendo todos desde el mismo punto,
     * las vigas forman una sola retícula de punta a punta del edificio.
     */
    private static final float ORIGEN_TECHO_X = 341.3f;
    private static final float ORIGEN_TECHO_Z = 94.0f;

    private void cubrirTechoY(int drawableResId,
                              float xMin, float xMax,
                              float zMin, float zMax,
                              float y, float unidadesPorMosaico) {
        int tex = cargarTexturaRepetida(drawableResId);
        if (tex == 0) return;

        float x0 = Math.min(xMin, xMax), x1 = Math.max(xMin, xMax);
        float z0 = Math.min(zMin, zMax), z1 = Math.max(zMin, zMax);
        float paso = unidadesPorMosaico > 0f ? unidadesPorMosaico : UNIDADES_POR_MOSAICO;

        // Coordenadas de textura medidas desde el origen común, no desde la
        // esquina de este rectángulo.
        float u0 = (x0 - ORIGEN_TECHO_X) / paso, u1 = (x1 - ORIGEN_TECHO_X) / paso;
        float v0 = (z0 - ORIGEN_TECHO_Z) / paso, v1 = (z1 - ORIGEN_TECHO_Z) / paso;

        float[] pos = {
                x0, y, z0,   x0, y, z1,   x1, y, z1,
                x0, y, z0,   x1, y, z1,   x1, y, z0
        };
        float[] uv = {
                u0, v0,      u0, v1,      u1, v1,
                u0, v0,      u1, v1,      u1, v0
        };

        murales.add(new Mural(buffer(pos), buffer(uv), 6, tex));
    }

    /**
     * Muro vertical perpendicular al eje Z con la textura REPETIDA.
     * Las rendijas quedan verticales, como el muro de lámina del TAMUX.
     */
    private void cubrirMuroZ(int drawableResId,
                             float xMin, float xMax,
                             float yAbajo, float yArriba,
                             float z, float unidadesPorMosaico) {
        int tex = cargarTexturaRepetida(drawableResId);
        if (tex == 0) return;
        float x0 = Math.min(xMin, xMax), x1 = Math.max(xMin, xMax);
        float y0 = Math.min(yAbajo, yArriba), y1 = Math.max(yAbajo, yArriba);
        float paso = unidadesPorMosaico > 0f ? unidadesPorMosaico : UNIDADES_POR_MOSAICO;
        float uRep = (x1 - x0) / paso, vRep = (y1 - y0) / paso;

        float[] pos = {
                x0, y1, z,   x0, y0, z,   x1, y0, z,
                x0, y1, z,   x1, y0, z,   x1, y1, z
        };
        float[] uv = {
                0f, 0f,      0f, vRep,    uRep, vRep,
                0f, 0f,      uRep, vRep,  uRep, 0f
        };
        murales.add(new Mural(buffer(pos), buffer(uv), 6, tex));
    }

    /** Muro vertical perpendicular al eje X con la textura REPETIDA. */
    private void cubrirMuroX(int drawableResId,
                             float zMin, float zMax,
                             float yAbajo, float yArriba,
                             float x, float unidadesPorMosaico) {
        int tex = cargarTexturaRepetida(drawableResId);
        if (tex == 0) return;
        float z0 = Math.min(zMin, zMax), z1 = Math.max(zMin, zMax);
        float y0 = Math.min(yAbajo, yArriba), y1 = Math.max(yAbajo, yArriba);
        float paso = unidadesPorMosaico > 0f ? unidadesPorMosaico : UNIDADES_POR_MOSAICO;
        float uRep = (z1 - z0) / paso, vRep = (y1 - y0) / paso;

        float[] pos = {
                x, y1, z0,   x, y0, z0,   x, y0, z1,
                x, y1, z0,   x, y0, z1,   x, y1, z1
        };
        float[] uv = {
                0f, 0f,      0f, vRep,    uRep, vRep,
                0f, 0f,      uRep, vRep,  uRep, 0f
        };
        murales.add(new Mural(buffer(pos), buffer(uv), 6, tex));
    }

    /**
     * Construye un tambor vertical (prisma de N lados) con la textura repetida.
     * Sólo los costados: la tapa no hace falta porque siempre queda tapada por
     * lo que se apoya encima.
     */
    private void cubrirPrismaVertical(int drawableResId,
                                      float cx, float cz, float radio,
                                      float yBase, float yTope,
                                      int lados, float unidadesPorMosaico) {
        int tex = cargarTexturaRepetida(drawableResId);
        if (tex == 0 || lados < 3 || yTope <= yBase) return;
        float paso = unidadesPorMosaico > 0f ? unidadesPorMosaico : UNIDADES_POR_MOSAICO;
        float vRep = (yTope - yBase) / paso;
        float perimetro = (float) (2 * Math.PI * radio);

        float[] pos = new float[lados * 18];
        float[] uv  = new float[lados * 12];
        for (int i = 0; i < lados; i++) {
            double a0 = 2 * Math.PI * i / lados;
            double a1 = 2 * Math.PI * (i + 1) / lados;
            float x0 = cx + radio * (float) Math.cos(a0), z0 = cz + radio * (float) Math.sin(a0);
            float x1 = cx + radio * (float) Math.cos(a1), z1 = cz + radio * (float) Math.sin(a1);
            float u0 = perimetro * i / lados / paso, u1 = perimetro * (i + 1) / lados / paso;

            int p = i * 18, q = i * 12;
            pos[p]    = x0; pos[p+1]  = yTope; pos[p+2]  = z0;
            pos[p+3]  = x0; pos[p+4]  = yBase; pos[p+5]  = z0;
            pos[p+6]  = x1; pos[p+7]  = yBase; pos[p+8]  = z1;
            pos[p+9]  = x0; pos[p+10] = yTope; pos[p+11] = z0;
            pos[p+12] = x1; pos[p+13] = yBase; pos[p+14] = z1;
            pos[p+15] = x1; pos[p+16] = yTope; pos[p+17] = z1;

            uv[q]    = u0; uv[q+1]  = 0f;
            uv[q+2]  = u0; uv[q+3]  = vRep;
            uv[q+4]  = u1; uv[q+5]  = vRep;
            uv[q+6]  = u0; uv[q+7]  = 0f;
            uv[q+8]  = u1; uv[q+9]  = vRep;
            uv[q+10] = u1; uv[q+11] = 0f;
        }
        murales.add(new Mural(buffer(pos), buffer(uv), lados * 6, tex));
    }

    /**
     * Barra recta de sección cuadrada entre dos puntos del plano (Z,Y), a X fija.
     * Se usa para los pasamanos: siguen la pendiente de la escalera.
     */
    private void cubrirBarraInclinada(int drawableResId, float x,
                                      float z0, float y0, float z1, float y1,
                                      float grosor) {
        int tex = cargarTexturaRepetida(drawableResId);
        if (tex == 0) return;
        float dz = z1 - z0, dy = y1 - y0;
        float largo = (float) Math.sqrt(dz * dz + dy * dy);
        if (largo < 0.01f) return;
        // Perpendicular a la barra dentro del plano (Z,Y).
        float pz = -dy / largo * grosor * 0.5f, py = dz / largo * grosor * 0.5f;
        float hx = grosor * 0.5f;

        // Cuatro esquinas en cada extremo: ±X y ±perpendicular.
        float[][] a = {
                {x - hx, y0 + py, z0 + pz}, {x + hx, y0 + py, z0 + pz},
                {x + hx, y0 - py, z0 - pz}, {x - hx, y0 - py, z0 - pz}};
        float[][] b = {
                {x - hx, y1 + py, z1 + pz}, {x + hx, y1 + py, z1 + pz},
                {x + hx, y1 - py, z1 - pz}, {x - hx, y1 - py, z1 - pz}};

        float[] pos = new float[4 * 18];
        float[] uv  = new float[4 * 12];
        float vRep = largo / 40f;
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4, p = i * 18, q = i * 12;
            float[] a0 = a[i], a1 = a[j], b0 = b[i], b1 = b[j];
            System.arraycopy(new float[]{
                    a0[0], a0[1], a0[2],  b0[0], b0[1], b0[2],  b1[0], b1[1], b1[2],
                    a0[0], a0[1], a0[2],  b1[0], b1[1], b1[2],  a1[0], a1[1], a1[2]
            }, 0, pos, p, 18);
            System.arraycopy(new float[]{
                    0f, 0f,  vRep, 0f,  vRep, 1f,
                    0f, 0f,  vRep, 1f,  0f, 1f
            }, 0, uv, q, 12);
        }
        murales.add(new Mural(buffer(pos), buffer(uv), 24, tex));
    }

    /**
     * Pasamanos completo: la barra más sus postes.
     *
     * Se construye como decorado (va en "murales"), NO como parte del modelo,
     * así que no entra en el cálculo de colisiones: el barandal se ve pero no
     * te frena ni te empuja al caminar por la escalera.
     */
    private void construirBarandal(float x, float z0, float y0, float z1, float y1,
                                   float alturaRail, float pasoPostes) {
        final float GROSOR = 2.6f;
        cubrirBarraInclinada(R.drawable.barandal_blanco, x,
                z0, y0 + alturaRail, z1, y1 + alturaRail, GROSOR);

        float largoZ = Math.abs(z1 - z0);
        int n = Math.max(2, Math.round(largoZ / pasoPostes));
        for (int i = 0; i <= n; i++) {
            float t = i / (float) n;
            float z = z0 + (z1 - z0) * t;
            float y = y0 + (y1 - y0) * t;
            cubrirPrismaVertical(R.drawable.barandal_blanco,
                    x, z, GROSOR * 0.6f, y, y + alturaRail, 6, 40f);
        }
    }

    /**
     * Atril informativo, como los del museo: un cuerpo bajo con la tapa
     * inclinada y la ficha impresa encima.
     *
     * (dirX,dirZ) apunta hacia la pieza que explica: la tapa sube hacia allá,
     * de modo que la lees de pie desde el lado contrario.
     */
    private void construirConsola(float cx, float cz, float yPiso,
                                  float dirX, float dirZ) {
        int texCuerpo = cargarTexturaRepetida(R.drawable.negro_suave);
        int texFicha  = cargarTexturaMural(R.drawable.panel_info);
        if (texCuerpo == 0 || texFicha == 0) return;

        final float ANCHO = 74f, FONDO = 34f, CUERPO = 15f, PANEL = 9f;
        // Ejes locales: "f" hacia la pieza, "l" a lo largo del atril.
        float largo = (float) Math.sqrt(dirX * dirX + dirZ * dirZ);
        if (largo < 0.001f) return;
        float fx = dirX / largo, fz = dirZ / largo;
        float lx = -fz, lz = fx;

        float hf = FONDO * 0.5f, hl = ANCHO * 0.5f;
        // Esquinas de la planta: atrás (lado del visitante) y adelante.
        float[][] planta = {
                {cx - fx*hf - lx*hl, cz - fz*hf - lz*hl},
                {cx - fx*hf + lx*hl, cz - fz*hf + lz*hl},
                {cx + fx*hf + lx*hl, cz + fz*hf + lz*hl},
                {cx + fx*hf - lx*hl, cz + fz*hf - lz*hl}};

        // Cuerpo: cuatro caras verticales.
        float yTop = yPiso + CUERPO;
        float[] pos = new float[4 * 18];
        float[] uv  = new float[4 * 12];
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4, p = i * 18, q = i * 12;
            float[] A = planta[i], B = planta[j];
            System.arraycopy(new float[]{
                    A[0], yTop,  A[1],   A[0], yPiso, A[1],   B[0], yPiso, B[1],
                    A[0], yTop,  A[1],   B[0], yPiso, B[1],   B[0], yTop,  B[1]
            }, 0, pos, p, 18);
            System.arraycopy(new float[]{
                    0f, 0f,  0f, 0.4f,  1f, 0.4f,
                    0f, 0f,  1f, 0.4f,  1f, 0f
            }, 0, uv, q, 12);
        }
        murales.add(new Mural(buffer(pos), buffer(uv), 24, texCuerpo));

        // Tapa inclinada con la ficha: sube del lado de la pieza.
        float yAlto = yTop + PANEL;
        float[] tapa = {
                planta[0][0], yTop,  planta[0][1],
                planta[1][0], yTop,  planta[1][1],
                planta[2][0], yAlto, planta[2][1],

                planta[0][0], yTop,  planta[0][1],
                planta[2][0], yAlto, planta[2][1],
                planta[3][0], yAlto, planta[3][1]
        };
        float[] uvTapa = {
                0f, 1f,   1f, 1f,   1f, 0f,
                0f, 1f,   1f, 0f,   0f, 0f
        };
        murales.add(new Mural(buffer(tapa), buffer(uvTapa), 6, texFicha));
    }

    /**
     * Letrero o cuadro sobre un muro perpendicular al eje X.
     *
     * A diferencia de cubrirMuroX, aquí la imagen NO se repite: se estira una
     * sola vez sobre el rectángulo indicado. Es para carteles y cédulas, donde
     * repetir el dibujo se vería mal.
     *
     * @param haciaXPositivo true si la cara que se mira está del lado +X
     */
    private void cubrirLetreroX(int drawableResId,
                                float zMin, float zMax,
                                float yMin, float yMax,
                                float x, boolean haciaXPositivo) {
        int tex = cargarTexturaMural(drawableResId);
        if (tex == 0) return;
        float z0 = Math.min(zMin, zMax), z1 = Math.max(zMin, zMax);
        float y0 = Math.min(yMin, yMax), y1 = Math.max(yMin, yMax);
        float xs = x + (haciaXPositivo ? SEPARACION_MURAL : -SEPARACION_MURAL);

        // Visto desde +X, el eje Z crece hacia la izquierda; desde -X, al revés.
        float zIzq = haciaXPositivo ? z1 : z0;
        float zDer = haciaXPositivo ? z0 : z1;

        float[] pos = {
                xs, y1, zIzq,   xs, y0, zIzq,   xs, y0, zDer,
                xs, y1, zIzq,   xs, y0, zDer,   xs, y1, zDer
        };
        float[] uv = {
                0f, 0f,   0f, 1f,   1f, 1f,
                0f, 0f,   1f, 1f,   1f, 0f
        };
        murales.add(new Mural(buffer(pos), buffer(uv), 6, tex));
    }

    /** Empaqueta un arreglo de floats en un buffer nativo para OpenGL. */
    private static FloatBuffer buffer(float[] datos) {
        FloatBuffer fb = ByteBuffer.allocateDirect(datos.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(datos).position(0);
        return fb;
    }

    /**
     * Carga una textura pensada para REPETIRSE (GL_REPEAT) y verse bien de lejos.
     *
     * Se usa GL_LINEAR con mipmaps: sin mipmaps, un techo largo visto en
     * perspectiva produce un hormigueo muy molesto en las rendijas, y en VR ese
     * hormigueo es distinto en cada ojo y marea.
     *
     * OJO: GL_REPEAT en OpenGL ES 2.0 exige que la imagen sea potencia de dos.
     * techo_lamina.png es de 256x256, así que cumple.
     */
    private int cargarTexturaRepetida(int drawableResId) {
        Integer cached = cacheMural.get(drawableResId);
        if (cached != null) return cached;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId, options);
        if (bitmap == null) {
            android.util.Log.w("Techo", "No se pudo decodificar el drawable " + drawableResId);
            return 0;
        }

        int[] h = new int[1];
        GLES20.glGenTextures(1, h, 0);
        if (h[0] == 0) { bitmap.recycle(); return 0; }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, h[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        bitmap.recycle();
        cacheMural.put(drawableResId, h[0]);
        return h[0];
    }

    /**
     * Encuadre vertical del recorte, de 0 (se queda la parte de ARRIBA de la
     * foto) a 1 (se queda la de ABAJO). 0.5 = centrado.
     *
     * Solo tiene efecto cuando la pared es más apaisada que la foto y sobra
     * alto, que es justo lo que pasa en la pared del fondo.
     */
    private static final float ENCUADRE_VERTICAL = 0.5f;

    /** Cubre por completo una pared perpendicular al eje Z, mirando hacia +Z. */
    private void cubrirParedZ(int drawableResId,
                              float xIzq, float xDer, float yAbajo, float yArriba,
                              float zPared, float aspectoFoto) {
        cubrirParedZ(drawableResId, xIzq, xDer, yAbajo, yArriba, zPared, aspectoFoto, true);
    }

    /**
     * Cubre por completo una pared perpendicular al eje Z.
     *
     * La foto se escala hasta tapar TODO el rectángulo indicado y lo que sobra
     * se recorta, en vez de estirarse. Es el mismo criterio que un fondo de
     * pantalla "rellenar": el muro queda cubierto de esquina a esquina y la
     * fotografía conserva su proporción real, sin quedar achatada.
     *
     * @param drawableResId foto a mostrar (R.drawable.xxx)
     * @param xIzq, xDer    extremos horizontales de la pared
     * @param yAbajo, yArriba extremos verticales de la pared
     * @param zPared        Z de la CARA de la pared sobre la que se pega
     * @param aspectoFoto   ancho/alto de la foto (para no deformarla)
     * @param haciaZPositivo true si la cara visible mira hacia +Z
     */
    private void cubrirParedZ(int drawableResId,
                              float xIzq, float xDer, float yAbajo, float yArriba,
                              float zPared, float aspectoFoto,
                              boolean haciaZPositivo) {
        int tex = cargarTexturaMural(drawableResId);
        if (tex == 0) return;

        float ancho = Math.abs(xDer - xIzq);
        float alto  = Math.abs(yArriba - yAbajo);
        if (ancho <= 0f || alto <= 0f || aspectoFoto <= 0f) return;

        // --- Recorte tipo "rellenar": se usa solo la parte de la foto que cabe
        //     manteniendo la proporción. ---
        float aspectoPared = ancho / alto;
        float u0 = 0f, u1 = 1f, v0 = 0f, v1 = 1f;
        if (aspectoPared > aspectoFoto) {
            // La pared es más apaisada que la foto: se usa todo el ancho y se
            // recorta arriba y abajo.
            float usado = aspectoFoto / aspectoPared;
            v0 = (1f - usado) * ENCUADRE_VERTICAL;
            v1 = v0 + usado;
        } else {
            // La pared es más alta que la foto: se usa todo el alto y se
            // recorta a los lados (centrado).
            float usado = aspectoPared / aspectoFoto;
            u0 = (1f - usado) * 0.5f;
            u1 = u0 + usado;
        }

        float z = haciaZPositivo ? zPared + SEPARACION_MURAL : zPared - SEPARACION_MURAL;

        // Izquierda/derecha vistas DESDE el jugador (se invierten si mira a -Z).
        float menorX = Math.min(xIzq, xDer), mayorX = Math.max(xIzq, xDer);
        float izq = haciaZPositivo ? menorX : mayorX;
        float der = haciaZPositivo ? mayorX : menorX;
        float arriba = Math.max(yAbajo, yArriba);
        float abajo  = Math.min(yAbajo, yArriba);

        // Dos triángulos: (sup-izq, inf-izq, inf-der) y (sup-izq, inf-der, sup-der).
        float[] pos = {
                izq, arriba, z,   izq, abajo, z,   der, abajo, z,
                izq, arriba, z,   der, abajo, z,   der, arriba, z
        };
        // En OpenGL la fila de arriba del bitmap corresponde a v = 0.
        float[] uv = {
                u0, v0,   u0, v1,   u1, v1,
                u0, v0,   u1, v1,   u1, v0
        };

        FloatBuffer pb = ByteBuffer.allocateDirect(pos.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        pb.put(pos).position(0);
        FloatBuffer tb = ByteBuffer.allocateDirect(uv.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        tb.put(uv).position(0);

        murales.add(new Mural(pb, tb, 6, tex));
    }

    /**
     * Carga la foto de un mural.
     *
     * Se hace aparte de TextureHelper a propósito: las texturas del edificio se
     * repiten (GL_REPEAT) y usan filtro GL_NEAREST, que en una fotografía grande
     * se ve pixelada. Aquí usamos GL_LINEAR y GL_CLAMP_TO_EDGE para que la foto
     * se vea suave y sin repetirse. Esto NO afecta al resto de texturas: estos
     * ajustes son propios de cada textura.
     */
    private int cargarTexturaMural(int drawableResId) {
        Integer cached = cacheMural.get(drawableResId);
        if (cached != null) return cached;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId, options);
        if (bitmap == null) {
            android.util.Log.w("Mural", "No se pudo decodificar el drawable " + drawableResId);
            return 0;
        }

        int[] h = new int[1];
        GLES20.glGenTextures(1, h, 0);
        if (h[0] == 0) { bitmap.recycle(); return 0; }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, h[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        bitmap.recycle();
        cacheMural.put(drawableResId, h[0]);
        return h[0];
    }

    // =====================================================================
    //  API DE NAVEGACIÓN (la que llama MainActivity)
    // =====================================================================
    public void MoveForward(float amount) {
        double y = Math.toRadians(yawTotal());
        float dx = -amount * (float) Math.sin(y);
        float dz =  amount * (float) Math.cos(y);
        intentarMover(dx, dz);
    }

    public void StrafeRight(float amount) {
        double y = Math.toRadians(yawTotal());
        float dx = amount * (float) Math.cos(y);
        float dz = amount * (float) Math.sin(y);
        intentarMover(dx, dz);
    }

    /** En caminata la altura la controla la gravedad; dejamos esto sin efecto. */
    public void MoveUpward(float amount) { /* no-op en modo caminata */ }

    public void RotateY(float angle) { manualYawDeg += angle; }

    public void RotateX(float angle) {
        manualPitchDeg += angle;
        if (manualPitchDeg > 89f) manualPitchDeg = 89f;
        if (manualPitchDeg < -89f) manualPitchDeg = -89f;
    }

    public void setOrientation(float[] ang) {
        sensorYawDeg = (float) Math.toDegrees(-ang[0]);
        sensorPitchDeg = (float) Math.toDegrees(-ang[1]);
        usarSensor = true;
    }

    /**
     * Apaga el giroscopio al salir del modo VR.
     *
     * Lo que llevaba aportado el sensor se SUMA al ángulo manual antes de
     * apagarlo. Si no se hiciera, al cambiar de modo la vista pegaría un
     * brinco hacia otra dirección y perderías la orientación en el edificio.
     */
    public void desactivarSensor() {
        if (usarSensor) {
            manualYawDeg += sensorYawDeg;
            manualPitchDeg += sensorPitchDeg;
            if (manualPitchDeg > 89f) manualPitchDeg = 89f;
            if (manualPitchDeg < -89f) manualPitchDeg = -89f;
        }
        usarSensor = false;
        sensorYawDeg = 0f;
        sensorPitchDeg = 0f;
    }

    /** Al entrar en VR la cabeza manda: se limpia la inclinación manual. */
    public void prepararParaVR() {
        manualPitchDeg = 0f;
    }

    /** Se llama al reanudar la app para que el primer frame no tenga un dt enorme. */
    public void reiniciarReloj() { ultimoFrameNs = 0L; }

    public void ReiniciarPosiciones() {
        position[0] = startX;
        position[1] = startY;
        position[2] = startZ;
        manualYawDeg = SPAWN_YAW;
        manualPitchDeg = 0f;
        sensorYawDeg = 0f;
        sensorPitchDeg = 0f;
    }

    private float yawTotal()   { return manualYawDeg   + (usarSensor ? sensorYawDeg   : 0f); }
    private float pitchTotal() { return manualPitchDeg + (usarSensor ? sensorPitchDeg : 0f); }

    // =====================================================================
    //  MOTOR DE CAMINATA
    // =====================================================================

    /** Grosor máximo (horizontal) para considerar algo "pared" y que te choque. */
    private static final float WALL_THIN = 40f;

    /** Una parte es pared (te frena) si es delgada y alta; las rampas/pisos anchos no. */
    private boolean esPared(ObjLoader.Part p) {
        float dy = p.maxY - p.minY;
        // Se usa el grosor REAL (perpendicular a la cara), no el de la caja
        // envolvente: si no, las paredes DIAGONALES no se detectan y se atraviesan.
        return dy > STEP_UP && p.grosorReal < WALL_THIN;
    }

    /**
     * Distancia máxima de un solo paso de colisión.
     *
     * Si en un frame te movieras 30 unidades de golpe, podrías "saltarte" una
     * pared delgada: la prueba se hace en el punto de salida y en el de llegada,
     * y la pared queda en medio. Por eso el movimiento se parte en trozos.
     */
    private static final float PASO_COLISION_MAX = 3f;

    /** Intenta desplazarse (dx,dz) partiendo el recorrido en trozos pequeños. */
    private void intentarMover(float dx, float dz) {
        float dist = (float) Math.sqrt(dx * dx + dz * dz);
        int trozos = (int) Math.ceil(dist / PASO_COLISION_MAX);
        if (trozos <= 1) { intentarMoverPaso(dx, dz); return; }
        if (trozos > 12) trozos = 12;      // tope de seguridad
        float px = dx / trozos, pz = dz / trozos;
        for (int i = 0; i < trozos; i++) intentarMoverPaso(px, pz);
    }

    /** Un paso corto, resolviendo el choque por eje (permite deslizar por el muro). */
    private void intentarMoverPaso(float dx, float dz) {
        float feet = position[1] - EYE_HEIGHT;
        float eye  = position[1];

        // Si ya estás incrustado en algo, permite salir sin bloqueo (anti-atasco).
        boolean atascado = !puedeEstar(position[0], position[2], feet, eye);

        float nx = position[0] + dx;
        if (atascado || puedeEstar(nx, position[2], feet, eye)) position[0] = nx;

        float nz = position[2] + dz;
        if (atascado || puedeEstar(position[0], nz, feet, eye)) position[2] = nz;
    }

    /** ¿La posición (x,z) está libre de paredes para un cuerpo entre feet y eye? */
    private boolean puedeEstar(float x, float z, float feet, float eye) {
        if (model == null) return true;
        for (ObjLoader.Part p : model.parts) {
            if (!esPared(p)) continue;                 // solo las paredes frenan
            if (p.maxY <= feet + STEP_UP) continue;    // suficientemente baja para pisarla
            if (p.minY >= eye) continue;               // por encima de tu cabeza
            if (chocaConParte(p, x, z, feet, eye)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Comprueba si el jugador choca con la geometría real de una parte pared,
     * en lugar de solo su caja envolvente (evita paredes invisibles en diagonales/arcos).
     */
    private boolean chocaConParte(ObjLoader.Part p, float x, float z, float feet, float eye) {
        // Descarte rápido por caja envolvente
        if (x < p.minX - BODY_RADIUS || x > p.maxX + BODY_RADIUS ||
            z < p.minZ - BODY_RADIUS || z > p.maxZ + BODY_RADIUS) {
            return false;
        }
        float[] t = p.tri;
        for (int i = 0; i + 8 < t.length; i += 9) {
            float ay = t[i + 1], by = t[i + 4], cy = t[i + 7];
            float tminY = Math.min(ay, Math.min(by, cy));
            float tmaxY = Math.max(ay, Math.max(by, cy));
            if (tmaxY <= feet + STEP_UP || tminY >= eye) {
                continue;
            }
            float ax = t[i],     az = t[i + 2];
            float bx = t[i + 3], bz = t[i + 5];
            float cx = t[i + 6], cz = t[i + 8];

            if (pointInTriangle2D(x, z, ax, az, bx, bz, cx, cz)) {
                return true;
            }
            if (distSqToSegment(x, z, ax, az, bx, bz) < BODY_RADIUS_SQ) {
                return true;
            }
            if (distSqToSegment(x, z, bx, bz, cx, cz) < BODY_RADIUS_SQ) {
                return true;
            }
            if (distSqToSegment(x, z, cx, cz, ax, az) < BODY_RADIUS_SQ) {
                return true;
            }
        }
        return false;
    }

    private static float distSqToSegment(float px, float pz, float ax, float az, float bx, float bz) {
        float abx = bx - ax;
        float abz = bz - az;
        float apx = px - ax;
        float apz = pz - az;
        float abLenSq = abx * abx + abz * abz;
        if (abLenSq < 1e-6f) {
            return apx * apx + apz * apz;
        }
        float t = (apx * abx + apz * abz) / abLenSq;
        if (t <= 0f) {
            return apx * apx + apz * apz;
        } else if (t >= 1f) {
            float bpx = px - bx;
            float bpz = pz - bz;
            return bpx * bpx + bpz * bpz;
        }
        float projX = ax + t * abx;
        float projZ = az + t * abz;
        float dx = px - projX;
        float dz = pz - projZ;
        return dx * dx + dz * dz;
    }

    private static boolean pointInTriangle2D(float px, float pz,
                                            float ax, float az,
                                            float bx, float bz,
                                            float cx, float cz) {
        float d = (bz - cz) * (ax - cx) + (cx - bx) * (az - cz);
        if (Math.abs(d) < 1e-6f) return false;
        float w1 = ((bz - cz) * (px - cx) + (cx - bx) * (pz - cz)) / d;
        float w2 = ((cz - az) * (px - cx) + (ax - cx) * (pz - cz)) / d;
        float w3 = 1f - w1 - w2;
        return w1 >= 0f && w2 >= 0f && w3 >= 0f;
    }

    /**
     * Altura del piso justo debajo de (x,z) usando los TRIÁNGULOS reales
     * (así funcionan rampas y escaleras). Devuelve NaN si no hay piso.
     */
    private float alturaDeSoporte(float x, float z, float maxTop) {
        if (model == null) return Float.NaN;
        float best = Float.NaN;
        for (ObjLoader.Part p : model.parts) {
            if (x < p.minX || x > p.maxX || z < p.minZ || z > p.maxZ) continue;
            float[] t = p.tri;
            for (int i = 0; i + 8 < t.length; i += 9) {
                float y = alturaEnTriangulo(x, z,
                        t[i],   t[i+1], t[i+2],
                        t[i+3], t[i+4], t[i+5],
                        t[i+6], t[i+7], t[i+8]);
                if (!Float.isNaN(y) && y <= maxTop + 0.5f) {
                    if (Float.isNaN(best) || y > best) best = y;
                }
            }
        }
        return best;
    }

    /** Interpola la altura Y del triángulo en (x,z). NaN si (x,z) cae fuera del triángulo. */
    private static float alturaEnTriangulo(float x, float z,
                                           float ax, float ay, float az,
                                           float bx, float by, float bz,
                                           float cx, float cy, float cz) {
        float d = (bz - cz) * (ax - cx) + (cx - bx) * (az - cz);
        if (Math.abs(d) < 1e-6f) return Float.NaN; // triángulo vertical: no es piso
        float w1 = ((bz - cz) * (x - cx) + (cx - bx) * (z - cz)) / d;
        float w2 = ((cz - az) * (x - cx) + (ax - cx) * (z - cz)) / d;
        float w3 = 1f - w1 - w2;
        float e = -0.06f; // tolerancia para uniones entre baldosas de piso
        if (w1 < e || w2 < e || w3 < e) return Float.NaN;
        return w1 * ay + w2 * by + w3 * cy;
    }

    /** Gravedad + apoyo en el piso + subir/bajar escalones. Se llama una vez por frame. */
    private void actualizarFisica() {
        float feet = position[1] - EYE_HEIGHT;
        float support = alturaDeSoporte(position[0], position[2], feet + STEP_UP);

        if (Float.isNaN(support)) {
            // No hay datos de piso aquí: NO caemos al vacío, nos quedamos donde estamos.
            return;
        }
        if (support >= feet) {
            feet = support;                                   // parado o subiendo un escalón
        } else {
            feet = Math.max(support, feet - FALL_SPEED * dt);  // bajando/cayendo suave
        }
        position[1] = feet + EYE_HEIGHT;

        // --- LOG DE POSICIÓN (útil para elegir el SPAWN; ponlo en true si lo necesitas) ---
        if (!LOG_POSICION) return;
        if (++logFrame >= 30) {   // ~2 veces por segundo a 60 fps
            logFrame = 0;
            android.util.Log.d("POS", String.format(
                    "SPAWN_X=%.0f  SPAWN_Y=%.0f  SPAWN_Z=%.0f   yaw=%.0f",
                    position[0], position[1], position[2], yawTotal()));
        }
    }

    // =====================================================================
    //  CICLO DE VIDA OPENGL
    // =====================================================================
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        String vertexShader = RawResourceReader.readTextFileFromRawResource(context, R.raw.texture_vertex);
        String fragmentShader = RawResourceReader.readTextFileFromRawResource(context, R.raw.texture_fragment);
        programHandle = ShaderHelper.createAndLinkProgram(
                ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader),
                ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader),
                new String[]{"a_Position", "a_TexCoord"});

        GLES20.glUseProgram(programHandle);
        mvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MvpMatrix");
        textureUniformHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture");
        positionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        textureCoordinateHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoord");

        cargarRecursos();

        if (context instanceof MainActivity) {
            ((MainActivity) context).ocultarPantallaCarga();
        }
    }

    private void cargarRecursos() {
        // 1) Cargar el modelo separado en partes.
        model = ObjLoader.fromRaw(context, R.raw.tinker);

        // 1.5) Sala 1: quitar los esferoides sobrantes y asentar cada planeta
        //      sobre su plataforma. Va ANTES de todo lo demás porque descarta
        //      partes y cambia la geometría.
        asentarPlanetas();

        // 1.6) Enlazar rampa y escaleras, y cerrar las ranuras contra el muro.
        ajustarCirculacion();

        // 2) Definir qué color/imagen quieres en cada parte.
        definirEstilos();

        // 3) Construir la textura final de cada parte.
        int n = model.parts.size();
        partTexture = new int[n];
        for (int i = 0; i < n; i++) {
            ObjLoader.Part p = model.parts.get(i);
            partTexture[i] = resolverTextura(p);
        }

        // 3.5) Murales (fotos pegadas a las paredes).
        definirMurales();

        // 4) Punto de aparición.
        //    Si SPAWN_* no cae sobre ningún piso (típico tras mover el modelo en
        //    Tinkercad), se usa el centro del modelo para no aparecer dentro de
        //    una pared ni flotando en el vacío.
        startX = SPAWN_X; startY = SPAWN_Y; startZ = SPAWN_Z;
        float apoyo = alturaDeSoporte(SPAWN_X, SPAWN_Z, SPAWN_Y);
        if (Float.isNaN(apoyo)) {
            startX = model.centerX;
            startZ = model.centerZ;
            startY = model.maxY;              // caemos desde arriba
        } else {
            startY = apoyo + EYE_HEIGHT;      // de pie sobre el piso encontrado
        }
        ReiniciarPosiciones();
    }

    // =====================================================================
    //  SALA 1 (EL UNIVERSO)
    //
    //  Criterio: tocar lo MENOS posible. Todo lo de esta sala es del diseño
    //  original; aquí sólo se corrige lo que estaba mal apoyado y se quitan las
    //  dos plataformas que quedaron vacías. Ni las paredes, ni los planetas
    //  colgantes, ni su acomodo se mueven de sitio.
    //
    //  1) obj_115 y obj_124 son plataformas SIN planeta encima. Estorbaban a
    //     media sala sin exhibir nada, así que no se dibujan. Es lo único que
    //     se elimina; nada más de la sala se borra.
    //
    //  2) obj_125..obj_129 son los planetas que cuelgan. Se quedan EXACTAMENTE
    //     donde los pusiste. Antes los pegaba a la cara del techo, y eso
    //     arruinaba tu composición: obj_129 cuelga 22 unidades más abajo que
    //     los otros cuatro, y esa diferencia de alturas es a propósito. Con el
    //     techo en 182 y sus tapas en 179.4, ya quedan colgando justo debajo
    //     sin necesidad de moverlos.
    //
    //  3) Cada planeta de piso va con su plataforma. La plataforma baja hasta
    //     apoyarse en el piso que tenga debajo (la de obj_122 flotaba a 31
    //     unidades porque ahí el suelo baja de -145 a -174) y al rebajarle el
    //     peralte, el planeta la acompaña con el MISMO desplazamiento. Así se
    //     conserva tal cual el hundimiento original de 15 unidades de la esfera
    //     dentro de la base, que es lo que la hace emerger del pedestal.
    // =====================================================================

    /** Lo único que se retira de la sala: las dos plataformas sin planeta. */
    private static final String[] OBJETOS_ELIMINADOS = { "obj_115", "obj_124" };

    /** Cada grupo es { plataforma, planeta }. Las vacías ya no aparecen aquí. */
    private static final String[][] GRUPOS_PLANETAS = {
            {"obj_116", "obj_119"},   // azul
            {"obj_117", "obj_121"},   // rojo  (sol)
            {"obj_118", "obj_120"},   // rosado (luna)
            {"obj_123", "obj_122"},   // azul  — es el que flotaba
    };

    /** Quita del modelo los objetos que ya no se quieren ver. */
    private void ocultarObjetos(String[] nombres) {
        if (model == null) return;
        for (String nombre : nombres) {
            for (int i = model.parts.size() - 1; i >= 0; i--) {
                if (model.parts.get(i).objectName.equals(nombre)) model.parts.remove(i);
            }
        }
    }

    /**
     * Altura de las plataformas de los planetas.
     *
     * Venían de 35 unidades, que con esta escala son 1.84 m: exactamente la
     * altura de tus ojos (EYE_HEIGHT = 35), y por eso las veías de canto, como
     * losas. 24 unidades son ≈1.26 m: te llegan al pecho, se leen como una base
     * maciza y siguen bien por debajo de la vista. Se encoge sólo en vertical
     * tomando la base como pivote, así que la planta no cambia y la plataforma
     * queda pegada al piso.
     */
    private static final float ALTURA_PEDESTAL = 24f;

    private void asentarPlanetas() {
        ocultarObjetos(OBJETOS_ELIMINADOS);

        for (String[] grupo : GRUPOS_PLANETAS) {
            // 1. La plataforma y su planeta bajan JUNTOS hasta el piso: al
            //    moverse lo mismo, la relación entre ambos no cambia.
            apoyarGrupoEnPiso(grupo);

            // 2. Al rebajarle el peralte a la plataforma, su cara superior baja
            //    cierta cantidad. El planeta baja EXACTAMENTE esa misma
            //    cantidad, así que sigue hundido en la base las mismas 15
            //    unidades que le diste en Tinkercad.
            float bajoLaTapa = rebajarPedestal(grupo[0]);
            if (grupo.length >= 2 && bajoLaTapa != 0f) {
                for (ObjLoader.Part p : model.parts) {
                    if (p.objectName.equals(grupo[1])) p.desplazarY(bajoLaTapa);
                }
            }
        }
        // Los planetas colgantes NO se tocan: conservan la altura original.
    }

    /**
     * Deja la plataforma con ALTURA_PEDESTAL de alto, apoyada donde estaba.
     *
     * Devuelve cuánto bajó su cara superior, para que el planeta pueda
     * acompañarla con el mismo desplazamiento y no se altere el hundimiento
     * original de la esfera dentro de la base.
     */
    private float rebajarPedestal(String objectName) {
        if (model == null) return 0f;
        float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (ObjLoader.Part p : model.parts) {
            if (!p.objectName.equals(objectName)) continue;
            if (p.minY < minY) minY = p.minY;
            if (p.maxY > maxY) maxY = p.maxY;
        }
        float alto = maxY - minY;
        if (alto <= 0.5f || Math.abs(alto - ALTURA_PEDESTAL) < 0.5f) return 0f;

        float factor = ALTURA_PEDESTAL / alto;
        for (ObjLoader.Part p : model.parts) {
            if (p.objectName.equals(objectName)) p.escalarY(factor, minY);
        }
        return (minY + ALTURA_PEDESTAL) - maxY;   // negativo: la tapa bajó
    }

    /** Baja un grupo completo hasta que su base descanse en el piso. */
    private void apoyarGrupoEnPiso(String[] objetos) {
        if (model == null) return;

        float baseY = Float.MAX_VALUE, cx = 0f, cz = 0f;
        int n = 0;
        for (ObjLoader.Part p : model.parts) {
            if (!perteneceA(p, objetos)) continue;
            if (p.minY < baseY) baseY = p.minY;
            cx += (p.minX + p.maxX) * 0.5f;
            cz += (p.minZ + p.maxZ) * 0.5f;
            n++;
        }
        if (n == 0) return;
        cx /= n; cz /= n;

        // Al pedir apoyo sólo hasta baseY-1 se descarta la geometría del propio
        // grupo, para que no se "apoye sobre sí mismo".
        float apoyo = alturaDeSoporte(cx, cz, baseY - 1f);
        if (Float.isNaN(apoyo)) return;   // sin piso debajo: mejor no moverlo

        float dy = apoyo - baseY;
        if (Math.abs(dy) < 0.5f) return;
        for (ObjLoader.Part p : model.parts) {
            if (perteneceA(p, objetos)) p.desplazarY(dy);
        }
    }

    private static boolean perteneceA(ObjLoader.Part p, String[] objetos) {
        for (String o : objetos) if (p.objectName.equals(o)) return true;
        return false;
    }

    // =====================================================================
    //  CIRCULACIÓN: rampa y escaleras que bajan del lobby a la sala del mural
    //
    //  Medido sobre el propio OBJ, recorriendo el piso a lo largo de Z (x=590):
    //
    //     z =  36 .. 14   rampa obj_55, baja de 101 a 87
    //     z =  14 .. -19  piso obj_11, plano en 87
    //     z = -19         ¡SALTO DE +2! la escalera obj_12 arranca en 89
    //     z = -19 ..-153  escalera obj_12, baja de 89 a 52
    //     z = -153        ESCALÓN DE 5 al pasar a obj_13, que ahí va en 47
    //     z =-153 ..-262  escalera obj_13, baja de 51 a 14
    //     z = -262        CAÍDA DE 7 (37 cm) al piso obj_15, que está en 7
    //
    //  O sea: tropiezo al entrar a la escalera, un escalón doble a la mitad y
    //  un brinco al final. Aquí se enlazan los tres tramos para que el recorrido
    //  quede continuo, de 87 a 7 sin saltos.
    //
    //  Y de lado quedaban dos ranuras abiertas contra el muro obj_14 (cara
    //  interior en X = 514.3): 2 unidades (10 cm) en obj_12 y 1 (5 cm) en
    //  obj_13. Por ahí se veía el vacío de abajo. Se corren en X hasta topar
    //  con el muro; del lado este siguen metiéndose en obj_70, así que no se
    //  abre nada del otro lado.
    // =====================================================================
    private void ajustarCirculacion() {
        if (model == null) return;

        // --- Alturas: que cada tramo entregue exactamente donde empieza el siguiente ---
        ajustarRampa("obj_12", 590f, -152f, 50f, -20f, 87f);   // entrega al piso obj_11 (87)
        ajustarRampa("obj_13", 590f, -261f,  7f, -153f, 50f);  // entrega al piso obj_15 (7)

        // --- Ranuras laterales contra el muro obj_14 ---
        moverEnX("obj_12", -2f);
        moverEnX("obj_13", -1f);

        // -----------------------------------------------------------------
        //  VANO DEL LOBBY AL PASILLO: más ancho y alineado con la entrada
        //
        //  Medido sobre el OBJ: las puertas de cristal de la entrada ocupan
        //  X = 401.3 .. 441.3 (obj_7 y obj_8). El vano interior, en cambio,
        //  iba de 411.3 a 431.3: mismo centro, pero la MITAD de ancho. Por eso
        //  se veía como una rendija y no como una entrada.
        //
        //  Se le corre el borde a cada muro hasta que el vano coincida
        //  EXACTAMENTE con las puertas: entras y sigues derecho por un hueco
        //  del mismo ancho, sin tener que buscarlo. De paso pasas de 5 a 25
        //  unidades de holgura a cada lado con el radio del cuerpo (7.5), así
        //  que ya no te atoras al cruzar.
        // -----------------------------------------------------------------
        moverBordeX("obj_4",  341.3f, 411.3f, VANO_X_MIN);   // muro oeste
        moverBordeX("obj_54", 664.3f, 431.3f, VANO_X_MAX);   // muro este

        // Los dos muros que flanquean el vano NO estaban a paño: obj_4 tiene
        // 5 unidades de espesor (cara del lobby en Z = 99) y obj_54 tenía 12
        // (cara en Z = 106). Ese resalto de 7 unidades era lo que hacía que la
        // entrada se viera chueca. Se le adelgaza a obj_54 hasta igualarlo, de
        // paso deja de montarse sobre el borde del piso del lobby.
        for (ObjLoader.Part p : model.parts) {
            if (p.objectName.equals("obj_54")) p.escalarZ(5f / 12f, 94f);
        }
    }

    /**
     * Frontera entre la sala 1 (el universo) y el resto del museo.
     * Todo muro oscuro cuyo centro cae al oeste de aquí recibe el acabado
     * estrellado; al este se queda en negro liso.
     */
    private static final float LIMITE_SALA_1 = -150f;

    /** El vano interior copia el ancho de las puertas de entrada (obj_7 + obj_8). */
    private static final float VANO_X_MIN = 401.3f;
    private static final float VANO_X_MAX = 441.3f;

    /**
     * Corre uno de los bordes de un muro en X, dejando el otro extremo clavado.
     *
     * @param pivote       extremo que NO se mueve
     * @param bordeActual  dónde está hoy el borde que sí se mueve
     * @param bordeNuevo   dónde debe quedar
     */
    private void moverBordeX(String objectName, float pivote,
                             float bordeActual, float bordeNuevo) {
        float largo = bordeActual - pivote;
        if (Math.abs(largo) < 0.01f) return;
        float factor = (bordeNuevo - pivote) / largo;
        for (ObjLoader.Part p : model.parts) {
            if (p.objectName.equals(objectName)) p.escalarX(factor, pivote);
        }
    }

    private void moverEnX(String objectName, float dx) {
        for (ObjLoader.Part p : model.parts) {
            if (p.objectName.equals(objectName)) p.desplazarX(dx);
        }
    }

    /**
     * Recoloca una rampa o escalera para que su cara superior pase por dos
     * alturas dadas en dos posiciones de Z.
     *
     * No se toman las alturas de un solo punto: en una escalera eso caería sobre
     * un peralte o sobre una huella según la suerte y el resultado bailaría. Se
     * muestrea el tramo completo y se ajusta una recta por mínimos cuadrados,
     * que es la pendiente media real. Después:
     *   - si la pendiente que hace falta es la misma, basta con subir o bajar;
     *   - si no, se estira en vertical alrededor del pivote que deja el extremo
     *     bajo justo donde se pidió.
     */
    private void ajustarRampa(String nombre, float x,
                              float zBajo, float yBajoDeseado,
                              float zAlto, float yAltoDeseado) {
        final int MUESTRAS = 41;
        double sz = 0, sy = 0, szz = 0, szy = 0;
        int n = 0;
        for (int i = 0; i < MUESTRAS; i++) {
            float z = zBajo + (zAlto - zBajo) * i / (MUESTRAS - 1f);
            float y = alturaDeObjeto(nombre, x, z);
            if (Float.isNaN(y)) continue;
            sz += z; sy += y; szz += (double) z * z; szy += (double) z * y;
            n++;
        }
        if (n < 4) return;

        double den = n * szz - sz * sz;
        if (Math.abs(den) < 1e-9) return;
        double m = (n * szy - sz * sy) / den;
        double b = (sy - m * sz) / n;

        float yBajo0 = (float) (m * zBajo + b);
        float yAlto0 = (float) (m * zAlto + b);
        float desnivel0 = yAlto0 - yBajo0;
        if (Math.abs(desnivel0) < 1f) return;

        float f = (yAltoDeseado - yBajoDeseado) / desnivel0;

        if (Math.abs(f - 1f) < 0.005f) {              // misma pendiente: sólo mover
            float dy = yBajoDeseado - yBajo0;
            if (Math.abs(dy) < 0.2f) return;
            for (ObjLoader.Part p : model.parts) {
                if (p.objectName.equals(nombre)) p.desplazarY(dy);
            }
        } else {                                       // hay que cambiar la pendiente
            float pivote = (yBajoDeseado - f * yBajo0) / (1f - f);
            for (ObjLoader.Part p : model.parts) {
                if (p.objectName.equals(nombre)) p.escalarY(f, pivote);
            }
        }
    }

    /** Altura de la cara superior de UN objeto concreto en (x,z). NaN si no hay. */
    private float alturaDeObjeto(String objectName, float x, float z) {
        float best = Float.NaN;
        for (ObjLoader.Part p : model.parts) {
            if (!p.objectName.equals(objectName)) continue;
            if (x < p.minX || x > p.maxX || z < p.minZ || z > p.maxZ) continue;
            float[] t = p.tri;
            for (int i = 0; i + 8 < t.length; i += 9) {
                float y = alturaEnTriangulo(x, z,
                        t[i],   t[i+1], t[i+2],
                        t[i+3], t[i+4], t[i+5],
                        t[i+6], t[i+7], t[i+8]);
                if (!Float.isNaN(y) && (Float.isNaN(best) || y > best)) best = y;
            }
        }
        return best;
    }

    /** Decide la textura GL de una parte: imagen > color asignado > color de Tinkercad. */
    private int resolverTextura(ObjLoader.Part p) {
        String k = claveDe(p);
        Integer drawable = imagenPorParte.get(k);
        if (drawable != null) {
            Integer cached = cacheImagen.get(drawable);
            if (cached != null) return cached;
            int t = TextureHelper.loadTexture(context, drawable);
            cacheImagen.put(drawable, t);
            return t;
        }
        Integer rgb = colorPorParte.get(k);
        int r, g, b;
        if (rgb != null) {
            r = (rgb >> 16) & 0xFF; g = (rgb >> 8) & 0xFF; b = rgb & 0xFF;
        } else {
            r = p.colorR; g = p.colorG; b = p.colorB; // color original de Tinkercad
        }
        int key = (r << 16) | (g << 8) | b;
        Integer cached = cacheColor.get(key);
        if (cached != null) return cached;
        int t = crearTexturaColor(r, g, b);
        cacheColor.put(key, t);
        return t;
    }

    /** Crea una textura de 1x1 px de color sólido (para pintar sin tocar los shaders). */
    private int crearTexturaColor(int r, int g, int b) {
        int[] h = new int[1];
        GLES20.glGenTextures(1, h, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, h[0]);
        ByteBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder());
        buf.put((byte) r).put((byte) g).put((byte) b).put((byte) 255).position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 1, 1, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return h[0];
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        actualizarReloj();  // duración real del frame: la velocidad ya no depende de los fps
        aplicarEntrada();   // movimiento pedido por la UI (fuera del hilo principal)
        actualizarFisica(); // gravedad / piso / escalones (una sola vez por frame)
        int w = (int) screenWidth, h = (int) screenHeight;
        if (modoVR) {
            // Las dos mitades deben ser EXACTAMENTE iguales. Con un ancho impar,
            // "w/2" dejaba una columna de píxeles suelta y el ojo derecho salía
            // un pelo desplazado, que en VR se nota como desalineación.
            int half = w / 2;
            dibujarOjo(0,        0, half, h, -SEPARACION_OJOS);
            dibujarOjo(w - half, 0, half, h,  SEPARACION_OJOS);

            if (mapaVisible) {
                int mapSize = Math.min(240, (int) (half * 0.38f));
                int margin = 30;
                int vy = h - mapSize - margin;
                // Cada mapa va pegado al borde INTERIOR de su ojo: así los dos
                // caen cerca del centro de la vista y se fusionan al mirarlos.
                dibujarMinimapa(half - mapSize - margin, vy, mapSize);
                dibujarMinimapa(w - half + margin,       vy, mapSize);
            }
        } else {
            dibujarOjo(0, 0, w, h, 0f);
            if (mapaVisible) {
                int mapSize = Math.min(312, (int) (h * 0.42f));
                int margin = 40;
                dibujarMinimapa(margin, h - mapSize - margin, mapSize);
            }
        }
    }

    private void dibujarMinimapa(int vx, int vy, int mapSize) {
        // En OpenGL el origen (0,0) es abajo a la izquierda.
        GLES20.glViewport(vx, vy, mapSize, mapSize);

        // Limpiamos solo el Depth Buffer para dibujar sobre la escena sin cruces
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);

        // Proyección ortográfica para el mapa (sin perspectiva)
        float range = 250f; // Vista aérea
        // El plano "near" se ajusta para que todo lo que esté "detrás" de la cámara no se dibuje (techo)
        Matrix.orthoM(projectionMatrix, 0, -range, range, -range, range, 0f, 500f);

        float ex = position[0];
        // Ponemos la cámara del minimapa EXACTAMENTE a la altura de los ojos del jugador
        float ey = position[1];
        float ez = position[2];

        float yaw = (float) Math.toRadians(yawTotal());
        // El vector up define cómo gira la cámara. Para que rote, usamos el vector forward del jugador.
        float upX = (float) (-Math.sin(yaw));
        float upZ = (float) (Math.cos(yaw));

        // Miramos hacia abajo desde los ojos del jugador.
        // Como near=0f, cualquier geometría (techo) que esté por encima de los ojos NO se dibuja!
        Matrix.setLookAtM(viewMatrix, 0, ex, ey, ez, ex, ey - 100f, ez, upX, 0f, upZ);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0);

        dibujarModelo();

        // Dibujar un pequeño punto rojo en el centro del minimapa usando Scissor Test
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        int dotSize = 10;
        GLES20.glScissor(vx + mapSize / 2 - dotSize / 2, vy + mapSize / 2 - dotSize / 2, dotSize, dotSize);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1f); // Restaurar color de fondo
    }

    private void dibujarOjo(int vx, int vy, int vw, int vh, float eyeShift) {
        if (vh == 0) return;
        GLES20.glViewport(vx, vy, vw, vh);

        // Campo de visión REAL en grados, en vez del 90° fijo de antes (que en
        // pantalla completa deformaba mucho los bordes y en VR daba mareo).
        float fov = modoVR ? FOV_VR : FOV_NORMAL;
        float top = (float) (Math.tan(Math.toRadians(fov * 0.5f)) * PLANO_CERCANO);
        float right = top * ((float) vw / vh);
        Matrix.frustumM(projectionMatrix, 0, -right, right, -top, top,
                PLANO_CERCANO, PLANO_LEJANO);

        float yaw = (float) Math.toRadians(yawTotal());
        float pitch = (float) Math.toRadians(pitchTotal());
        float fx = (float) (Math.sin(yaw) * Math.cos(pitch));
        float fy = (float) (Math.sin(pitch));
        float fz = (float) (-Math.cos(yaw) * Math.cos(pitch));
        // Vector "derecha" de la cabeza. El desplazamiento del ojo va SOLO por
        // aquí (nunca en Y): así los dos ojos quedan siempre a la misma altura
        // aunque mires al techo, que es lo que permite fusionar la imagen.
        float rx = (float) Math.cos(yaw);
        float rz = (float) Math.sin(yaw);

        float ex = position[0] + rx * eyeShift;
        float ey = position[1];
        float ez = position[2] + rz * eyeShift;

        Matrix.setLookAtM(viewMatrix, 0, ex, ey, ez, ex + fx, ey + fy, ez + fz, 0f, 1f, 0f);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0);

        dibujarModelo();
    }

    private void dibujarModelo() {
        if (model == null) return;
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(textureUniformHandle, 0);

        for (int i = 0; i < model.parts.size(); i++) {
            ObjLoader.Part p = model.parts.get(i);

            p.positions.position(0);
            GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE,
                    GLES20.GL_FLOAT, false, 0, p.positions);
            GLES20.glEnableVertexAttribArray(positionHandle);

            p.texCoords.position(0);
            GLES20.glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE,
                    GLES20.GL_FLOAT, false, 0, p.texCoords);
            GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, partTexture[i]);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, p.vertexCount);
        }

        dibujarMurales();
    }

    /** Dibuja las fotos pegadas a las paredes (se llama dentro de dibujarModelo). */
    private void dibujarMurales() {
        for (int i = 0; i < murales.size(); i++) {
            Mural m = murales.get(i);

            m.positions.position(0);
            GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE,
                    GLES20.GL_FLOAT, false, 0, m.positions);
            GLES20.glEnableVertexAttribArray(positionHandle);

            m.texCoords.position(0);
            GLES20.glVertexAttribPointer(textureCoordinateHandle, TEXTURE_COORDINATE_DATA_SIZE,
                    GLES20.GL_FLOAT, false, 0, m.texCoords);
            GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m.texture);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, m.vertexCount);
        }
    }

}