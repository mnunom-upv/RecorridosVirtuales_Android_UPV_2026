package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object RawResourceReader {
    fun readTextFileFromRawResource(
        context: Context,
        resourceId: Int
    ): String? {
        val inputStream = context.getResources().openRawResource(
            resourceId
        )
        val inputStreamReader = InputStreamReader(
            inputStream
        )
        val bufferedReader = BufferedReader(
            inputStreamReader
        )

        var nextLine: String?
        val body = StringBuilder()

        try {
            while ((bufferedReader.readLine().also { nextLine = it }) != null) {
                body.append(nextLine)
                body.append('\n')
            }
        } catch (e: IOException) {
            return null
        }

        return body.toString()
    }
}
