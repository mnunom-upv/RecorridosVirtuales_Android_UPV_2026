package upv.pm.may_ago_26.iti_271503.pgu2_eq06

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.opengl.GLES20
import android.opengl.GLUtils

object TextureHelper {
    private var cxt: Context? = null

    fun convertDrawableResToBitmap(drawableId: Int, width: Int, height: Int): Bitmap {
        val drawable = cxt?.resources?.getDrawable(drawableId, cxt?.theme)
        if (drawable is BitmapDrawable) return drawable.bitmap
        if (drawable is GradientDrawable) {
            val w = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else width
            val h = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else height
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, w, h)
            drawable.setStroke(1, Color.BLACK)
            drawable.setFilterBitmap(true)
            drawable.draw(canvas)
            return bitmap
        }
        val bit = BitmapFactory.decodeResource(cxt!!.resources, drawableId)
        return bit.copy(Bitmap.Config.ARGB_8888, true)
    }

    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureHandle = IntArray(1)
        cxt = context
        GLES20.glGenTextures(1, textureHandle, 0)
        if (textureHandle[0] == 0) throw RuntimeException("Error generating texture name.")

        val bitmapp = convertDrawableResToBitmap(resourceId, 25, 25)
        val matrix = Matrix().apply { postRotate(180f) }
        val bitmap = Bitmap.createBitmap(bitmapp, 0, 0, bitmapp.width, bitmapp.height, matrix, true)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        return textureHandle[0]
    }

    fun loadCubeMap(context: Context, cubeResources: IntArray): Int {
        val textureObjectIds = IntArray(1)
        GLES20.glGenTextures(1, textureObjectIds, 0)
        if (textureObjectIds[0] == 0) return 0

        val options = BitmapFactory.Options().apply { inScaled = false }
        val cubeBitmaps = arrayOfNulls<Bitmap>(6)
        for (i in 0 until 6) {
            cubeBitmaps[i] = BitmapFactory.decodeResource(context.resources, cubeResources[i], options)
            if (cubeBitmaps[i] == null) {
                GLES20.glGenTextures(1, textureObjectIds, 0)
                return 0
            }
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureObjectIds[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        cubeBitmaps.forEach { it?.recycle() }
        return textureObjectIds[0]
    }
}
