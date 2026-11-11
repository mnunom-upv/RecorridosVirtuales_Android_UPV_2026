package upv.pm.may_ago_26.iti_271503.pgu2_eq05;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextTextureHelper {
    
    public static int createGoldPlaqueTexture(String text) {
        int width = 512;
        int height = 160;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Fondo Amarillo / Dorado
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.parseColor("#FFD700")); // Dorado vibrante
        bgPaint.setStyle(Paint.Style.FILL);

        // Borde exterior en dorado oscuro / marrón
        Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.parseColor("#8B6508"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(10f);

        // Borde interior en rojo para resaltar
        Paint innerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerBorderPaint.setColor(Color.parseColor("#B71C1C"));
        innerBorderPaint.setStyle(Paint.Style.STROKE);
        innerBorderPaint.setStrokeWidth(4f);

        RectF outerRect = new RectF(5, 5, width - 5, height - 5);
        RectF innerRect = new RectF(12, 12, width - 12, height - 12);

        // Dibujar placas
        canvas.drawRoundRect(outerRect, 16, 16, bgPaint);
        canvas.drawRoundRect(outerRect, 16, 16, borderPaint);
        canvas.drawRoundRect(innerRect, 12, 12, innerBorderPaint);

        // Configuración de texto rojo
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#CC0000")); // Rojo intenso
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Dividir líneas si el texto es largo
        String[] lines;
        if (text.contains(",")) {
            lines = text.split(",");
            for (int i = 0; i < lines.length; i++) {
                lines[i] = lines[i].trim();
            }
        } else if (text.length() > 22) {
            // Dividir en 2 líneas por espacio si es muy largo
            int middle = text.length() / 2;
            int spaceIndex = text.indexOf(' ', middle);
            if (spaceIndex == -1) spaceIndex = text.lastIndexOf(' ', middle);
            if (spaceIndex != -1) {
                lines = new String[]{
                        text.substring(0, spaceIndex).trim(),
                        text.substring(spaceIndex + 1).trim()
                };
            } else {
                lines = new String[]{text};
            }
        } else {
            lines = new String[]{text};
        }

        // Calcular tamaño de fuente dinámico
        float maxLineWidth = width - 40;
        float fontSize = 38f;
        if (lines.length > 2) {
            fontSize = 28f;
        } else if (lines.length == 2) {
            fontSize = 32f;
        }
        textPaint.setTextSize(fontSize);

        for (String line : lines) {
            while (textPaint.measureText(line) > maxLineWidth && fontSize > 16f) {
                fontSize -= 2f;
                textPaint.setTextSize(fontSize);
            }
        }

        // Dibujar cada línea centrada vertical y horizontalmente
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float lineHeight = fm.descent - fm.ascent;
        float totalHeight = lineHeight * lines.length;
        float startY = (height - totalHeight) / 2f - fm.ascent;

        for (int i = 0; i < lines.length; i++) {
            canvas.drawText(lines[i], width / 2f, startY + (i * lineHeight), textPaint);
        }

        // Invertir verticalmente para alineación con las coordenadas UV de OpenGL sin espejear horizontalmente
        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);
        matrix.postTranslate(0, height);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.recycle();

        // Subir textura a OpenGL ES 2.0
        int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, rotatedBitmap, 0);
            rotatedBitmap.recycle();
        }

        return textureHandle[0];
    }
}
