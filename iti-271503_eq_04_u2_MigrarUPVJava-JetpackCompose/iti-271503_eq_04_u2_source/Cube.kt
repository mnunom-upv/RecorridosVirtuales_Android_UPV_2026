package upv.pm.may_ago_26.iti_271503.pgu2_eq04

/**
 * Created by Schoen and Jonathan on 4/13/2016.
 */
object Cube {
    val CUBE_COORDS: FloatArray = floatArrayOf(
        // Front face
        -1.0f, 1.0f, 1.0f,
        -1.0f, -1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,  // Right face

        1.0f, 1.0f, 1.0f,
        1.0f, -1.0f, 1.0f,
        1.0f, 1.0f, -1.0f,
        1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, 1.0f, -1.0f,  // Back face

        1.0f, 1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f,  // Left face

        -1.0f, 1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f,
        -1.0f, 1.0f, 1.0f,
        -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, 1.0f,
        -1.0f, 1.0f, 1.0f,  // Top face

        -1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, -1.0f,  // Bottom face

        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, 1.0f,
        -1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, 1.0f,
        -1.0f, -1.0f, 1.0f,
        -1.0f, -1.0f, -1.0f,
    )

    val CUBE_COLORS_FACES: FloatArray = floatArrayOf(
        // Front, green
        0f, 0.53f, 0.27f, 1.0f,  // Right, blue
        0.0f, 0.34f, 0.90f, 1.0f,  // Back, also green
        0f, 0.53f, 0.27f, 1.0f,  // Left, also blue
        0.0f, 0.34f, 0.90f, 1.0f,  // Top, red
        0.84f, 0.18f, 0.13f, 1.0f,  // Bottom, also red
        0.84f, 0.18f, 0.13f, 1.0f,
    )

    val CUBE_NORMALS_FACES: FloatArray = floatArrayOf(
        // Front face
        0.0f, 0.0f, 1.0f,  // Right face
        1.0f, 0.0f, 0.0f,  // Back face
        0.0f, 0.0f, -1.0f,  // Left face
        -1.0f, 0.0f, 0.0f,  // Top face
        0.0f, 1.0f, 0.0f,  // Bottom face
        0.0f, -1.0f, 0.0f,
    )

    val CUBE_FOUND_COLORS_FACES: FloatArray = floatArrayOf(
        // Same yellow for front, right, back, left, top, bottom faces
        1.0f, 0.65f, 0.0f, 1.0f,
        1.0f, 0.65f, 0.0f, 1.0f,
        1.0f, 0.65f, 0.0f, 1.0f,
        1.0f, 0.65f, 0.0f, 1.0f,
        1.0f, 0.65f, 0.0f, 1.0f,
        1.0f, 0.65f, 0.0f, 1.0f,
    )

    /**
     * Utility method for generating float arrays for cube faces
     * 
     * @param model - float[] array of values per face.
     * @param coords_per_vertex - int number of coordinates per
     * vertex.
     * @return - Returns float array of coordinates for
     * triangulated cube faces.
     * 6 faces X 6 points X coords_per_vertex
     */
    fun cubeFacesToArray(model: FloatArray, coords_per_vertex: Int): FloatArray {
        val coords: FloatArray? = FloatArray(6 * 6 * coords_per_vertex)
        var index = 0
        for (iFace in 0..5) {
            for (iVertex in 0..5) {
                for (iCoord in 0..<coords_per_vertex) {
                    coords!![index] = model[iFace * coords_per_vertex + iCoord]
                    index++
                }
            }
        }
        return coords!!
    }
}
