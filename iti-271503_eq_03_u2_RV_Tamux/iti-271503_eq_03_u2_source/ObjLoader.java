package upv_pm.may_ago_26.iti_271503.pgu2_eq03;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Cargador de .obj para OpenGL ES 2.0 que SEPARA la geometría en "partes".
 *
 * Cada parte corresponde a un objeto ("o ...") y a un material ("usemtl ...").
 * Así puedes darle a cada pared / piso / escalón su propio color o imagen.
 *
 * - Convierte Z-up (Tinkercad) a Y-up (OpenGL):  (x, y, z) -> (x, z, -y)
 * - Genera UVs planares (proyección sobre el eje dominante de la cara).
 * - Lee el color original de Tinkercad desde el nombre del material
 *   ("color_16448250" -> #FAFAFA), que queda como color por defecto de la parte.
 */
public final class ObjLoader {

    /** Unidades del mundo por "mosaico" de textura (para imágenes). */
    private static final float TEX_SCALE = 40f;

    /** Una parte dibujable: geometría + a qué objeto/material pertenece. */
    public static final class Part {
        public final String objectName;   // ej. "obj_10"
        public final String material;     // ej. "color_2829873"
        public final FloatBuffer positions;
        public final FloatBuffer texCoords;
        public final int vertexCount;     // triángulos * 3
        public final int colorR, colorG, colorB; // color de Tinkercad (0..255)
        // Caja envolvente de la parte (para física: piso, escalones, paredes).
        // minY/maxY NO son finales: una parte se puede bajar al piso con desplazarY().
        public float minZ, maxZ;
        public float minX, maxX;
        public float minY, maxY;
        // Copia CPU de los vértices (x,y,z por vértice) para apoyo por triángulo.
        public final float[] tri;
        /** Grosor REAL medido sobre la normal de la pared (sirve para muros diagonales). */
        public final float grosorReal;

        Part(String objectName, String material,
             FloatBuffer positions, FloatBuffer texCoords, int vertexCount,
             int colorR, int colorG, int colorB,
             float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
             float[] tri, float grosorReal) {
            this.objectName = objectName;
            this.material = material;
            this.positions = positions;
            this.texCoords = texCoords;
            this.vertexCount = vertexCount;
            this.colorR = colorR;
            this.colorG = colorG;
            this.colorB = colorB;
            this.minX = minX; this.minY = minY; this.minZ = minZ;
            this.maxX = maxX; this.maxY = maxY; this.maxZ = maxZ;
            this.tri = tri;
            this.grosorReal = grosorReal;
        }

        /** Escala esta parte sólo en Z, dejando fija la coordenada "pivote". */
        public void escalarZ(float factor, float pivote) {
            if (factor == 1f) return;
            for (int i = 2; i < tri.length; i += 3) {
                tri[i] = pivote + (tri[i] - pivote) * factor;
            }
            for (int i = 2; i < vertexCount * 3; i += 3) {
                positions.put(i, pivote + (positions.get(i) - pivote) * factor);
            }
            positions.position(0);
            float a = pivote + (minZ - pivote) * factor;
            float b = pivote + (maxZ - pivote) * factor;
            minZ = Math.min(a, b);
            maxZ = Math.max(a, b);
        }

        /** Escala esta parte sólo en X, dejando fija la coordenada "pivote". */
        public void escalarX(float factor, float pivote) {
            if (factor == 1f) return;
            for (int i = 0; i < tri.length; i += 3) {
                tri[i] = pivote + (tri[i] - pivote) * factor;
            }
            for (int i = 0; i < vertexCount * 3; i += 3) {
                positions.put(i, pivote + (positions.get(i) - pivote) * factor);
            }
            positions.position(0);
            float a = pivote + (minX - pivote) * factor;
            float b = pivote + (maxX - pivote) * factor;
            minX = Math.min(a, b);
            maxX = Math.max(a, b);
        }

        /**
         * Escala esta parte sólo en vertical, dejando fija la altura "pivote".
         *
         * Se usa para bajarle el peralte a las plataformas de los planetas sin
         * tocar su planta ni moverlas de sitio: el pivote es su base, así que
         * se quedan apoyadas exactamente donde estaban.
         */
        public void escalarY(float factor, float pivote) {
            if (factor == 1f) return;
            for (int i = 1; i < tri.length; i += 3) {
                tri[i] = pivote + (tri[i] - pivote) * factor;
            }
            for (int i = 1; i < vertexCount * 3; i += 3) {
                positions.put(i, pivote + (positions.get(i) - pivote) * factor);
            }
            positions.position(0);
            minY = pivote + (minY - pivote) * factor;
            maxY = pivote + (maxY - pivote) * factor;
        }

        /** Mueve esta parte "dx" unidades en horizontal (eje X). */
        public void desplazarX(float dx) {
            if (dx == 0f) return;
            for (int i = 0; i < tri.length; i += 3) {
                tri[i] += dx;
            }
            for (int i = 0; i < vertexCount * 3; i += 3) {
                positions.put(i, positions.get(i) + dx);
            }
            positions.position(0);
            minX += dx;
            maxX += dx;
        }

        /**
         * Sube o baja esta parte "dy" unidades.
         *
         * Hay que tocar las TRES copias de la geometría o el modelo se rompe:
         * el buffer nativo (lo que dibuja OpenGL), el arreglo tri (lo que usa la
         * física para el piso y las paredes) y la caja envolvente.
         */
        public void desplazarY(float dy) {
            if (dy == 0f) return;
            for (int i = 1; i < tri.length; i += 3) {
                tri[i] += dy;
            }
            for (int i = 1; i < vertexCount * 3; i += 3) {
                positions.put(i, positions.get(i) + dy);
            }
            positions.position(0);
            minY += dy;
            maxY += dy;
        }
    }

    public final List<Part> parts;

    // Caja envolvente global (para spawn / referencias).
    public final float minX, minY, minZ, maxX, maxY, maxZ;
    public final float centerX, centerY, centerZ;

    private ObjLoader(List<Part> parts,
                      float minX, float minY, float minZ,
                      float maxX, float maxY, float maxZ) {
        this.parts = parts;
        this.minX = minX; this.minY = minY; this.minZ = minZ;
        this.maxX = maxX; this.maxY = maxY; this.maxZ = maxZ;
        this.centerX = (minX + maxX) * 0.5f;
        this.centerY = (minY + maxY) * 0.5f;
        this.centerZ = (minZ + maxZ) * 0.5f;
    }

    public static ObjLoader fromRaw(Context context, int rawResId) {
        try (java.io.InputStream is = context.getResources().openRawResource(rawResId);
             java.io.InputStreamReader isr = new java.io.InputStreamReader(is);
             java.io.BufferedReader br = new java.io.BufferedReader(isr)) {
            return parse(br);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar OBJ", e);
        }
    }

    public static ObjLoader parse(java.io.BufferedReader reader) throws java.io.IOException {
        List<float[]> v = new ArrayList<>();
        List<Part> parts = new ArrayList<>();

        // Estado del parte "en construcción".
        String curObj = "obj_0";
        String curMat = "color_12632256"; // gris por defecto (#C0C0C0)
        List<int[]> pending = new ArrayList<>();

        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;

        String rawLine;
        while ((rawLine = reader.readLine()) != null) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.charAt(0) == '#') continue;

            String[] tok = line.split("\\s+");
            if (tok.length == 0) continue;

            switch (tok[0]) {
                case "v":
                    if (tok.length >= 4) {
                        float x = Float.parseFloat(tok[1]);
                        float y = Float.parseFloat(tok[2]);
                        float z = Float.parseFloat(tok[3]);
                        // Z-up (Tinkercad) -> Y-up (OpenGL)
                        float nx = x, ny = z, nz = -y;

                        v.add(new float[]{nx, ny, nz});
                        if (nx < minX) minX = nx; if (nx > maxX) maxX = nx;
                        if (ny < minY) minY = ny; if (ny > maxY) maxY = ny;
                        if (nz < minZ) minZ = nz; if (nz > maxZ) maxZ = nz;
                    }
                    break;

                case "o":
                    flush(parts, curObj, curMat, pending, v);
                    pending = new ArrayList<>();
                    curObj = tok.length > 1 ? tok[1] : curObj;
                    break;

                case "usemtl":
                    // Nuevo material dentro (o entre) objetos = nueva parte.
                    flush(parts, curObj, curMat, pending, v);
                    pending = new ArrayList<>();
                    curMat = tok.length > 1 ? tok[1] : curMat;
                    break;

                case "f":
                    if (tok.length >= 4) {
                        int n = tok.length - 1;
                        int[] idx = new int[n];
                        for (int i = 0; i < n; i++) {
                            int slashIdx = tok[i + 1].indexOf('/');
                            String idStr = slashIdx != -1 ? tok[i + 1].substring(0, slashIdx) : tok[i + 1];
                            int id = Integer.parseInt(idStr);
                            if (id < 0) id = v.size() + 1 + id;
                            idx[i] = id - 1;
                        }
                        for (int i = 1; i < n - 1; i++) {
                            int a = idx[0], b = idx[i], c = idx[i + 1];
                            // Descarta triángulos degenerados (área cero).
                            if (a == b || b == c || a == c) continue;
                            pending.add(new int[]{a, b, c});
                        }
                    }
                    break;

                default:
                    break;
            }
        }
        flush(parts, curObj, curMat, pending, v);

        return new ObjLoader(parts, minX, minY, minZ, maxX, maxY, maxZ);
    }

    /** Convierte los triángulos acumulados en una Part con sus buffers. */
    private static void flush(List<Part> parts, String objName, String material,
                              List<int[]> tris, List<float[]> v) {
        if (tris.isEmpty()) return;

        int triCount = tris.size();
        float[] pos = new float[triCount * 9];
        float[] uv = new float[triCount * 6];
        int pi = 0, ui = 0;

        float pminX = Float.MAX_VALUE, pminY = Float.MAX_VALUE, pminZ = Float.MAX_VALUE;
        float pmaxX = -Float.MAX_VALUE, pmaxY = -Float.MAX_VALUE, pmaxZ = -Float.MAX_VALUE;

        for (int[] tri : tris) {
            float[] a = v.get(tri[0]);
            float[] b = v.get(tri[1]);
            float[] c = v.get(tri[2]);

            float abx = b[0]-a[0], aby = b[1]-a[1], abz = b[2]-a[2];
            float acx = c[0]-a[0], acy = c[1]-a[1], acz = c[2]-a[2];
            float nx = aby*acz - abz*acy;
            float ny = abz*acx - abx*acz;
            float nz = abx*acy - aby*acx;
            float axn = Math.abs(nx), ayn = Math.abs(ny), azn = Math.abs(nz);

            float[][] tres = {a, b, c};
            for (float[] p : tres) {
                pos[pi++] = p[0]; pos[pi++] = p[1]; pos[pi++] = p[2];
                if (p[0] < pminX) pminX = p[0]; if (p[0] > pmaxX) pmaxX = p[0];
                if (p[1] < pminY) pminY = p[1]; if (p[1] > pmaxY) pmaxY = p[1];
                if (p[2] < pminZ) pminZ = p[2]; if (p[2] > pmaxZ) pmaxZ = p[2];
                float u, w;
                if (axn >= ayn && axn >= azn) { u = p[2]; w = p[1]; }
                else if (ayn >= axn && ayn >= azn) { u = p[0]; w = p[2]; }
                else { u = p[0]; w = p[1]; }
                uv[ui++] = u / TEX_SCALE;
                uv[ui++] = w / TEX_SCALE;
            }
        }

        FloatBuffer pb = ByteBuffer.allocateDirect(pos.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        pb.put(pos).position(0);
        FloatBuffer tb = ByteBuffer.allocateDirect(uv.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        tb.put(uv).position(0);

        int[] rgb = colorDeMaterial(material);
        float grosor = grosorReal(pos, pminX, pminZ, pmaxX, pmaxZ);
        parts.add(new Part(objName, material, pb, tb, triCount * 3,
                rgb[0], rgb[1], rgb[2],
                pminX, pminY, pminZ, pmaxX, pmaxY, pmaxZ,
                pos, grosor));
    }

    /**
     * Grosor REAL de la pieza medido perpendicular a su cara vertical dominante.
     *
     * La caja envolvente miente con las paredes DIAGONALES: una pared delgada girada
     * 45 grados tiene una caja ancha en X y en Z, así una pared de 5 unidades
     * de grosor puede parecer de 400. Aquí buscamos la cara vertical más grande,
     * tomamos su normal horizontal y proyectamos todos los vértices sobre ella.
     */
    private static float grosorReal(float[] pos,
                                    float pminX, float pminZ,
                                    float pmaxX, float pmaxZ) {
        float bestArea = 0f, nx0 = 0f, nz0 = 0f;

        for (int i = 0; i + 8 < pos.length; i += 9) {
            float ax = pos[i],     ay = pos[i + 1], az = pos[i + 2];
            float bx = pos[i + 3], by = pos[i + 4], bz = pos[i + 5];
            float cx = pos[i + 6], cy = pos[i + 7], cz = pos[i + 8];

            float ux = bx - ax, uy = by - ay, uz = bz - az;
            float vx = cx - ax, vy = cy - ay, vz = cz - az;

            float nx = uy * vz - uz * vy;
            float ny = uz * vx - ux * vz;
            float nz = ux * vy - uy * vx;

            float horiz = nx * nx + nz * nz;
            if (horiz < 1e-6f) continue;                    // normal vertical = piso/techo
            if (ny * ny > horiz) continue;                  // más horizontal que vertical

            float area = nx * nx + ny * ny + nz * nz;
            if (area > bestArea) {
                bestArea = area;
                float len = (float) Math.sqrt(horiz);
                nx0 = nx / len;
                nz0 = nz / len;
            }
        }

        // Sin caras verticales claras: nos quedamos con la caja (comportamiento anterior).
        if (bestArea <= 0f) return Math.min(pmaxX - pminX, pmaxZ - pminZ);

        float dmin = Float.MAX_VALUE, dmax = -Float.MAX_VALUE;
        for (int i = 0; i + 2 < pos.length; i += 3) {
            float d = pos[i] * nx0 + pos[i + 2] * nz0;
            if (d < dmin) dmin = d;
            if (d > dmax) dmax = d;
        }
        return dmax - dmin;
    }

    /** "color_16448250" -> {250,250,250}. Si falla, gris medio. */
    private static int[] colorDeMaterial(String material) {
        try {
            int us = material.indexOf('_');
            int num = Integer.parseInt(material.substring(us + 1));
            return new int[]{ (num >> 16) & 0xFF, (num >> 8) & 0xFF, num & 0xFF };
        } catch (Exception e) {
            return new int[]{ 192, 192, 192 };
        }
    }
}