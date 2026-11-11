package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.util.Log
import com.google.protobuf.nano.InvalidProtocolBufferNanoException
import com.google.protobuf.nano.MessageNano
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import kotlin.text.StringBuilder

//import com.google.vrtoolkit.cardboard.proto.*;

object PhoneParams {
    private val TAG: String
    private const val STREAM_SENTINEL = 779508118

    fun readFromInputStream(inputStream: InputStream?): Phone.PhoneParams? {
        if (inputStream == null) {
            return null
        }
        try {
            val header = ByteBuffer.allocate(8)
            if (inputStream.read(header.array(), 0, header.array().size) == -1) {
                Log.e(TAG, "Error parsing param record: end of stream.")
                return null
            }
            val sentinel = header.getInt()
            val length = header.getInt()
            if (sentinel != STREAM_SENTINEL) {
                Log.e(TAG, "Error parsing param record: incorrect sentinel.")
                return null
            }
            val protoBytes = ByteArray(length)
            if (inputStream.read(protoBytes, 0, protoBytes.size) == -1) {
                Log.e(TAG, "Error parsing param record: end of stream.")
                return null
            }
            return MessageNano.mergeFrom<MessageNano?>(
                Phone.PhoneParams() as MessageNano,
                protoBytes
            ) as Phone.PhoneParams?
        } catch (e: InvalidProtocolBufferNanoException) {
            val tag = TAG
            val s = "Error parsing protocol buffer: "
            val value = e.toString().toString()
            Log.w(tag, if (value.isNotEmpty()) (s + value) else s)
        } catch (e2: IOException) {
            val tag2 = TAG
            val s2 = "Error reading Cardboard parameters: "
            val value2 = e2.toString()
            Log.w(tag2, if (value2.isNotEmpty()) (s2 + value2) else s2)
        }
        return null
    }

    fun readFromExternalStorage(): Phone.PhoneParams? {
        try {
            var stream: InputStream? = null
            try {
                stream =
                    BufferedInputStream(FileInputStream(ConfigUtils.getConfigFile("phone_params")))
                return readFromInputStream(stream)
            } finally {
                if (stream != null) {
                    try {
                        stream.close()
                    } catch (ex: IOException) {
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            val tag = TAG
            val value = e.toString().toString()
            Log.d(
                tag,
                StringBuilder(43 + value.length).append("Cardboard phone parameters file not found: ")
                    .append(value).toString()
            )
            return null
        }
    }

    init {
        TAG = PhoneParams::class.java.getSimpleName()
    }
}
