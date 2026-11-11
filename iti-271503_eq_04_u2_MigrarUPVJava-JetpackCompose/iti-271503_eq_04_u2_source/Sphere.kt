package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.cos
import kotlin.math.sin

class Sphere : RenderObject {

    constructor() : super() {
        allocateBuffers()
    }

    constructor(color: FloatArray?) : super() {
        allocateBuffers()
        createSolidColorLightingMaterial(color)
    }

    constructor(textureId: Int) : super() {
        allocateBuffers()
        createDiffuseMaterial(textureId)
    }

    constructor(textureId: Int, nightTextureId: Int) : super() {
        allocateBuffers()
        createDayNightMaterial(textureId, nightTextureId)
    }

    constructor(textureId: Int, lighting: Boolean) : super() {
        allocateBuffers()

        if (lighting) {
            createDiffuseMaterial(textureId)
        } else {
            createUnlitTexMaterial(textureId)
        }
    }

    fun createSolidColorLightingMaterial(color: FloatArray?): Sphere {
        val mat = SolidColorLightingMaterial(color)

        mat.setBuffers(
            vertexBuffer,
            normalBuffer,
            indexBuffer,
            numIndices
        )

        material = mat
        return this
    }

    fun createDiffuseMaterial(textureId: Int): Sphere {
        val mat = DiffuseLightingMaterial(textureId)

        mat.setBuffers(
            vertexBuffer,
            normalBuffer,
            texCoordBuffer,
            indexBuffer,
            numIndices
        )

        material = mat
        return this
    }

    fun createDayNightMaterial(
        textureId: Int,
        nightTextureId: Int
    ): Sphere {
        val mat = DayNightMaterial(
            textureId,
            nightTextureId
        )

        mat.setBuffers(
            vertexBuffer,
            normalBuffer,
            texCoordBuffer,
            indexBuffer,
            numIndices
        )

        material = mat
        return this
    }

    fun createUnlitTexMaterial(textureId: Int): Sphere {
        val mat = UnlitTexMaterial(textureId)

        mat.setBuffers(
            vertexBuffer,
            texCoordBuffer,
            indexBuffer,
            numIndices
        )

        material = mat
        return this
    }

    companion object {

        private const val TAG = "RenderBox.Sphere"

        var vertexBuffer: FloatBuffer? = null
        var normalBuffer: FloatBuffer? = null
        var texCoordBuffer: FloatBuffer? = null
        var indexBuffer: ShortBuffer? = null
        var numIndices: Int = 0

        fun allocateBuffers() {
            if (vertexBuffer != null) {
                return
            }

            val radius = 1f

            val nbLong = 24
            val nbLat = 16

            val vertices = arrayOfNulls<Vector3>(
                (nbLong + 1) * nbLat + nbLong * 2
            )

            val pi = MathUtils.PI
            val twoPi = MathUtils.PI2

            for (i in 0 until nbLong) {
                vertices[i] = Vector3(Vector3.up).multiply(radius)
                vertices[vertices.size - i - 1] = Vector3(Vector3.up).multiply(-radius)
            }

            for (lat in 0 until nbLat) {
                val a1 = pi * (lat + 1).toFloat() / (nbLat + 1)
                val sin1 = sin(a1.toDouble()).toFloat()
                val cos1 = cos(a1.toDouble()).toFloat()

                for (lon in 0..nbLong) {
                    val a2 = twoPi * (if (lon == nbLong) 0 else lon).toFloat() / nbLong
                    val sin2 = sin(a2.toDouble()).toFloat()
                    val cos2 = cos(a2.toDouble()).toFloat()

                    vertices[lon + lat * (nbLong + 1) + nbLong] =
                        Vector3(
                            sin1 * cos2,
                            cos1,
                            sin1 * sin2
                        ).multiply(radius)
                }
            }

            val normals = arrayOfNulls<Vector3>(vertices.size)

            for (n in vertices.indices) {
                normals[n] = Vector3(vertices[n]!!).normalize()
            }

            val uvs = arrayOfNulls<Vector2>(vertices.size)
            val uvStart = 1.0f / (nbLong * 2)
            val uvStride = 1.0f / nbLong

            for (i in 0 until nbLong) {
                uvs[i] = Vector2(
                    uvStart + i * uvStride,
                    1f
                )

                uvs[uvs.size - i - 1] = Vector2(
                    1f - (uvStart + i * uvStride),
                    0f
                )
            }

            for (lat in 0 until nbLat) {
                for (lon in 0..nbLong) {
                    uvs[lon + lat * (nbLong + 1) + nbLong] =
                        Vector2(
                            lon.toFloat() / nbLong,
                            1f - (lat + 1).toFloat() / (nbLat + 1)
                        )
                }
            }

            val nbFaces = (nbLong + 1) * nbLat + 2
            val nbTriangles = nbFaces * 2
            val nbIndexes = nbTriangles * 3

            numIndices = nbIndexes

            val triangles = ShortArray(nbIndexes)

            var i = 0

            for (lon in 0 until nbLong) {
                triangles[i++] = lon.toShort()
                triangles[i++] = (nbLong + lon + 1).toShort()
                triangles[i++] = (nbLong + lon).toShort()
            }

            for (lat in 0 until nbLat - 1) {
                for (lon in 0 until nbLong) {
                    val current = (lon + lat * (nbLong + 1) + nbLong).toShort()
                    val next = (current + nbLong + 1).toShort()

                    triangles[i++] = current
                    triangles[i++] = (current + 1).toShort()
                    triangles[i++] = (next + 1).toShort()

                    triangles[i++] = current
                    triangles[i++] = (next + 1).toShort()
                    triangles[i++] = next
                }
            }

            for (lon in 0 until nbLong) {
                triangles[i++] = (vertices.size - lon - 1).toShort()
                triangles[i++] = (vertices.size - nbLong - (lon + 1) - 1).toShort()
                triangles[i++] = (vertices.size - nbLong - lon - 1).toShort()
            }

            val vertexArray = FloatArray(vertices.size * 3)

            i = 0

            while (i < vertices.size) {
                val step = i * 3
                val vertex = vertices[i]!!

                vertexArray[step] = vertex.x
                vertexArray[step + 1] = vertex.y
                vertexArray[step + 2] = vertex.z

                i++
            }

            val normalArray = FloatArray(normals.size * 3)

            i = 0

            while (i < normals.size) {
                val step = i * 3
                val normal = normals[i]!!

                normalArray[step] = normal.x
                normalArray[step + 1] = normal.y
                normalArray[step + 2] = normal.z

                i++
            }

            val texCoordArray = FloatArray(uvs.size * 2)

            i = 0

            while (i < uvs.size) {
                val step = i * 2
                val uv = uvs[i]!!

                texCoordArray[step] = uv.x
                texCoordArray[step + 1] = uv.y

                i++
            }

            vertexBuffer = RenderObject.allocateFloatBuffer(vertexArray)
            normalBuffer = RenderObject.allocateFloatBuffer(normalArray)
            texCoordBuffer = RenderObject.allocateFloatBuffer(texCoordArray)
            indexBuffer = RenderObject.allocateShortBuffer(triangles)
        }
    }
}