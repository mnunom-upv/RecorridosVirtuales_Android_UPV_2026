package upv.pm.may_ago_26.iti_271503.pgu2_eq05;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureHelper
{
	public static Context CXT;

	public static Bitmap convertDrawableResToBitmap(int drawableId, Integer width, Integer height) {

		Drawable d = null;

		if (d instanceof BitmapDrawable) {
			System.out.println("Öpcion1");
			return ((BitmapDrawable) d).getBitmap();
		}

		if (d instanceof GradientDrawable) {
			System.out.println("Öpcion2");
			GradientDrawable g = (GradientDrawable) d;

			int w = d.getIntrinsicWidth() > 0 ? d.getIntrinsicWidth() : width;
			int h = d.getIntrinsicHeight() > 0 ? d.getIntrinsicHeight() : height;

			Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			g.setBounds(0, 0, w, h);
			g.setStroke(1, Color.BLACK);
			g.setFilterBitmap(true);
			g.draw(canvas);
			return bitmap;
		}

		System.out.println("Öpcion3");
		Bitmap bit = BitmapFactory.decodeResource(CXT.getResources(), drawableId);
		return bit.copy(Bitmap.Config.ARGB_8888, true);
	}

	public static int loadTexture(final Context context, final int resourceId, final boolean repetir) {
		final int[] textureHandle = new int[1];
		CXT = context;
		
		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] == 0)
		{
			throw new RuntimeException("Error generating texture name.");
		}

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;	// No pre-scaling

		// Read in the resource
		//final Bitmap bitmapp = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
		//final Bitmap bitmapp =getBitmap(context,resourceId);
		//final Bitmap bitmapp = BitmapFactory.decodeResource(CXT.getApplicationContext().getResources(), resourceId, options);
		final Bitmap bitmapp =convertDrawableResToBitmap(resourceId, 25,25);
				//BitmapFactory.decodeResource(context.getResources(), resourceId, options);
		if (bitmapp==null)
			System.out.println ("Valior BArriga SEñor");

		Matrix matrix = new Matrix();

		matrix.postRotate(180);

		Bitmap bitmap = Bitmap.createBitmap(bitmapp, 0, 0, bitmapp.getWidth(), bitmapp.getHeight(), matrix, true);



		// Bind to the texture in OpenGL
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

		// Set filtering
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

		if (repetir) {
			// Para repetir textura como tile
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
		} else {
			// Para no repetir (comportamiento por defecto)
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		}

		// Load the bitmap into the bound texture.
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

		// Recycle the bitmap, since its data has been loaded into OpenGL.
		bitmap.recycle();

		return textureHandle[0];
	}

	public static int loadCubeMap(Context context, int[] cubeResources) {
		final int[] textureObjectIds = new int[1];
		glGenTextures(1, textureObjectIds, 0);

		if (textureObjectIds[0] == 0) {
            /*if (D) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.");
            }*/
			return 0;
		}

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		final Bitmap[] cubeBitmaps = new Bitmap[6];
		for (int i = 0; i < 6; i++) {
			cubeBitmaps[i] = BitmapFactory.decodeResource(context.getResources(),
					cubeResources[i], options);

			if (cubeBitmaps[i] == null) {
                /*if (D) {
                    Log.w(TAG, "Resources ID " + cubeResources[i]
                            + " could not be decoded.");
                }*/
				glGenTextures(1, textureObjectIds, 0);
				return 0;
			}
		}

		glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);

		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0);
		texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0);

		texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0);
		texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0);

		texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0);
		texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0);

		glBindTexture(GL_TEXTURE_2D, 0);

		for (Bitmap bitmap : cubeBitmaps) {
			bitmap.recycle();
		}

		return textureObjectIds[0];
	}


	private TextureHelper() {
	}
}
