package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.opengl.GLES20
import android.opengl.GLUtils

object TextureHelper {

    var CXT: Context? = null

    fun convertDrawableResToBitmap(drawableId: Int, width: Int, height: Int): Bitmap? {
        val context = CXT ?: return null

        val drawable: Drawable? = try {
            context.resources.getDrawable(drawableId, context.theme)
        } catch (e: Exception) {
            null
        }

        if (drawable is BitmapDrawable) {
            val originalBitmap = drawable.bitmap

            if (originalBitmap != null && !originalBitmap.isRecycled) {
                return originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
            }
        }

        if (drawable is GradientDrawable) {
            val w = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else width
            val h = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else height

            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            drawable.setBounds(0, 0, w, h)
            drawable.setStroke(1, Color.BLACK)
            drawable.isFilterBitmap = true
            drawable.draw(canvas)

            return bitmap
        }

        val decodedBitmap = BitmapFactory.decodeResource(context.resources, drawableId)

        return if (decodedBitmap != null && !decodedBitmap.isRecycled) {
            decodedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            null
        }
    }

    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureHandle = IntArray(1)
        CXT = context

        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] == 0) {
            throw RuntimeException("Error generating texture name.")
        }

        val sourceBitmap = convertDrawableResToBitmap(resourceId, 25, 25)
            ?: throw RuntimeException("Error decoding texture resource: $resourceId")

        if (sourceBitmap.isRecycled) {
            throw RuntimeException("Texture source bitmap is already recycled: $resourceId")
        }

        val matrix = Matrix()
        matrix.postRotate(180f)

        val rotatedBitmap = Bitmap.createBitmap(
            sourceBitmap,
            0,
            0,
            sourceBitmap.width,
            sourceBitmap.height,
            matrix,
            true
        )

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_NEAREST
        )

        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_NEAREST
        )

        GLUtils.texImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            rotatedBitmap,
            0
        )

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        rotatedBitmap.recycle()
        sourceBitmap.recycle()

        return textureHandle[0]
    }

    fun loadCubeMap(context: Context, cubeResources: IntArray): Int {
        val textureObjectIds = IntArray(1)

        GLES20.glGenTextures(
            1,
            textureObjectIds,
            0
        )

        if (textureObjectIds[0] == 0) {
            return 0
        }

        val options = BitmapFactory.Options()
        options.inScaled = false

        val cubeBitmaps: Array<Bitmap> = Array(6) { index ->
            BitmapFactory.decodeResource(
                context.resources,
                cubeResources[index],
                options
            ) ?: run {
                GLES20.glDeleteTextures(
                    1,
                    textureObjectIds,
                    0
                )

                throw RuntimeException(
                    "Resource ID ${cubeResources[index]} could not be decoded."
                )
            }
        }

        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_CUBE_MAP,
            textureObjectIds[0]
        )

        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_CUBE_MAP,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )

        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_CUBE_MAP,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )

        GLUtils.texImage2D(
            GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
            0,
            cubeBitmaps[0],
            0
        )

        GLUtils.texImage2D(
            GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
            0,
            cubeBitmaps[1],
            0
        )

        GLUtils.texImage2D(
            GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
            0,
            cubeBitmaps[2],
            0
        )

        GLUtils.texImage2D(
            GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
            0,
            cubeBitmaps[3],
            0
        )

        GLUtils.texImage2D(
            GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z,
            0,
            cubeBitmaps[4],
            0
        )

        GLUtils.texImage2D(
            GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
            0,
            cubeBitmaps[5],
            0
        )

        GLES20.glBindTexture(
            GLES20.GL_TEXTURE_CUBE_MAP,
            0
        )

        for (bitmap in cubeBitmaps) {
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }

        return textureObjectIds[0]
    }
}