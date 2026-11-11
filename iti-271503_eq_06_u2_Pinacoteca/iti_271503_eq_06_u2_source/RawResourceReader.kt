package upv.pm.may_ago_26.iti_271503.pgu2_eq06

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object RawResourceReader {
    fun readTextFileFromRawResource(context: Context, resourceId: Int): String {
        val inputStream = context.resources.openRawResource(resourceId)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val body = StringBuilder()
        return try {
            var nextLine = bufferedReader.readLine()
            while (nextLine != null) {
                body.append(nextLine).append('\n')
                nextLine = bufferedReader.readLine()
            }
            body.toString()
        } catch (e: IOException) {
            ""
        }
    }
}
