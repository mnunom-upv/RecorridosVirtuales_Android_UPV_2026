package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.net.Uri
import android.nfc.NdefMessage
import android.util.Base64
import android.util.Log
import com.google.protobuf.nano.InvalidProtocolBufferNanoException
import com.google.protobuf.nano.MessageNano
import upv.pm.may_ago_26.iti_271503.pgu2_eq04.CardboardDevice.DeviceParams
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

//import com.google.vrtoolkit.cardboard.proto.*;
class CardboardDeviceParams {
    private var mVendor: String? = null
    private var mModel: String? = null
    var interLensDistance: Float = 0f
    var verticalDistanceToLensCenter: Float = 0f
    var screenToLensDistance: Float = 0f
    var leftEyeMaxFov: FieldOfView? = null
        private set
    var hasMagnet: Boolean = false
    private var mDistortion: Distortion? = null

    constructor() : super() {
        this.setDefaultValues()
    }

    constructor(params: CardboardDeviceParams) : super() {
        this.copyFrom(params)
    }

    constructor(params: DeviceParams?) : super() {
        this.setDefaultValues()
        if (params == null) {
            return
        }
        this.mVendor = params.vendor
        this.mModel = params.model
        this.interLensDistance = params.interLensDistance
        this.verticalDistanceToLensCenter = params.trayBottomToLensHeight
        this.screenToLensDistance = params.screenToLensDistance
        this.leftEyeMaxFov =
            FieldOfView.parseFromProtobuf(params.leftEyeFieldOfViewAngles!!)
        if (this.leftEyeMaxFov == null) {
            this.leftEyeMaxFov = FieldOfView()
        }
        this.mDistortion = Distortion.parseFromProtobuf(params.distortionCoefficients)
        if (this.mDistortion == null) {
            this.mDistortion = Distortion()
        }
        this.hasMagnet = params.hasMagnet
    }

    fun writeToOutputStream(outputStream: OutputStream): Boolean {
        try {
            val paramBytes = this.toByteArray()
            val header = ByteBuffer.allocate(8)
            header.putInt(STREAM_SENTINEL)
            header.putInt(paramBytes.size)
            outputStream.write(header.array())
            outputStream.write(paramBytes)
            return true
        } catch (e: IOException) {
            val s = "CardboardDeviceParams"
            val s2 = "Error writing Cardboard parameters: "
            val value = e.toString().toString()
            Log.w(s, if (value.length != 0) (s2 + value) else java.lang.String(s2) as String)
            return false
        }
    }

    private fun toByteArray(): ByteArray {
        val params = DeviceParams()
        params.setVendor(this.mVendor!!)
        params.setModel(this.mModel!!)
        params.setInterLensDistance(this.interLensDistance)
        params.setTrayBottomToLensHeight(this.verticalDistanceToLensCenter)
        params.setScreenToLensDistance(this.screenToLensDistance)
        params.leftEyeFieldOfViewAngles = this.leftEyeMaxFov!!.toProtobuf()
        params.distortionCoefficients = this.mDistortion!!.toProtobuf()
        if (this.hasMagnet) {
            params.setHasMagnet(this.hasMagnet)
        }
        return MessageNano.toByteArray(params as MessageNano)
    }

    fun toUri(): Uri {
        val paramsData = this.toByteArray()
        val paramsSize = paramsData.size
        return Uri.Builder().scheme("http").authority("google.com")
            .appendEncodedPath("cardboard/cfg")
            .appendQueryParameter("p", Base64.encodeToString(paramsData, 0, paramsSize, 11)).build()
    }

    var vendor: String?
        get() = this.mVendor
        set(vendor) {
            this.mVendor = (if (vendor != null) vendor else "")
        }

    var model: String?
        get() = this.mModel
        set(model) {
            this.mModel = (if (model != null) model else "")
        }

    val distortion: Distortion
        get() = this.mDistortion!!

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other === this) {
            return true
        }
        if (other !is CardboardDeviceParams) {
            return false
        }
        val o = other
        return this.mVendor == o.mVendor && this.mModel == o.mModel && this.interLensDistance == o.interLensDistance && this.verticalDistanceToLensCenter == o.verticalDistanceToLensCenter && this.screenToLensDistance == o.screenToLensDistance && this.leftEyeMaxFov == o.leftEyeMaxFov && this.mDistortion == o.mDistortion && this.hasMagnet == o.hasMagnet
    }

    override fun toString(): String {
        val append = StringBuilder().append("{\n")
        val value = this.mVendor.toString().toString()
        val append2 = append.append(
            StringBuilder(12 + value.length).append("  vendor: ").append(value).append(",\n")
                .toString()
        )
        val value2 = this.mModel.toString().toString()
        val append3 = append2.append(
            StringBuilder(11 + value2.length).append("  model: ").append(value2).append(",\n")
                .toString()
        ).append(
            StringBuilder(40).append("  inter_lens_distance: ").append(this.interLensDistance)
                .append(",\n").toString()
        ).append(
            StringBuilder(53).append("  vertical_distance_to_lens_center: ")
                .append(this.verticalDistanceToLensCenter).append(",\n").toString()
        ).append(
            StringBuilder(44).append("  screen_to_lens_distance: ")
                .append(this.screenToLensDistance).append(",\n").toString()
        )
        val value3 = this.leftEyeMaxFov.toString().replace("\n", "\n  ").toString().toString()
        val append4 = append3.append(
            StringBuilder(22 + value3.length).append("  left_eye_max_fov: ").append(value3)
                .append(",\n").toString()
        )
        val value4 = this.mDistortion.toString().replace("\n", "\n  ").toString().toString()
        return append4.append(
            StringBuilder(16 + value4.length).append("  distortion: ").append(value4).append(",\n")
                .toString()
        ).append(
            StringBuilder(17).append("  magnet: ").append(this.hasMagnet).append(",\n").toString()
        ).append("}\n").toString()
    }

    private fun setDefaultValues() {
        this.mVendor = DEFAULT_VENDOR
        this.mModel = DEFAULT_MODEL
        this.interLensDistance = DEFAULT_INTER_LENS_DISTANCE
        this.verticalDistanceToLensCenter = DEFAULT_VERTICAL_DISTANCE_TO_LENS_CENTER
        this.screenToLensDistance = DEFAULT_SCREEN_TO_LENS_DISTANCE
        this.leftEyeMaxFov = FieldOfView()
        this.hasMagnet = true
        this.mDistortion = Distortion()
    }

    private fun copyFrom(params: CardboardDeviceParams) {
        this.mVendor = params.mVendor
        this.mModel = params.mModel
        this.interLensDistance = params.interLensDistance
        this.verticalDistanceToLensCenter = params.verticalDistanceToLensCenter
        this.screenToLensDistance = params.screenToLensDistance
        this.leftEyeMaxFov = if (params.leftEyeMaxFov != null) FieldOfView(params.leftEyeMaxFov!!) else null
        this.hasMagnet = params.hasMagnet
        this.mDistortion = if (params.mDistortion != null) Distortion(params.mDistortion!!) else null
    }

    companion object {
        private const val TAG = "CardboardDeviceParams"
        private const val HTTP_SCHEME = "http"
        private const val URI_HOST_GOOGLE_SHORT = "g.co"
        private const val URI_HOST_GOOGLE = "google.com"
        private const val URI_PATH_CARDBOARD_HOME = "cardboard"
        private const val URI_PATH_CARDBOARD_CONFIG = "cardboard/cfg"
        private const val URI_SCHEME_LEGACY_CARDBOARD = "cardboard"
        private const val URI_HOST_LEGACY_CARDBOARD = "v1.0.0"
        private val URI_ORIGINAL_CARDBOARD_NFC: Uri
        private val URI_ORIGINAL_CARDBOARD_QR_CODE: Uri
        private const val URI_KEY_PARAMS = "p"
        private const val STREAM_SENTINEL = 894990891
        private const val DEFAULT_VENDOR = "Google, Inc."
        private const val DEFAULT_MODEL = "Cardboard v1"
        private const val DEFAULT_INTER_LENS_DISTANCE = 0.06f
        private const val DEFAULT_VERTICAL_DISTANCE_TO_LENS_CENTER = 0.035f
        private const val DEFAULT_SCREEN_TO_LENS_DISTANCE = 0.042f
        fun isOriginalCardboardDeviceUri(uri: Uri): Boolean {
            return URI_ORIGINAL_CARDBOARD_QR_CODE == uri as Any? || (URI_ORIGINAL_CARDBOARD_NFC.getScheme() == uri.getScheme() && URI_ORIGINAL_CARDBOARD_NFC.getAuthority() == uri.getAuthority())
        }

        private fun isCardboardDeviceUri(uri: Uri): Boolean {
            return HTTP_SCHEME == uri.getScheme() && URI_HOST_GOOGLE == uri.getAuthority() && URI_PATH_CARDBOARD_CONFIG == uri.getPath()
        }

        fun isCardboardUri(uri: Uri): Boolean {
            return isOriginalCardboardDeviceUri(uri) || isCardboardDeviceUri(uri)
        }

        fun createFromUri(uri: Uri?): CardboardDeviceParams? {
            if (uri == null) {
                return null
            }
            if (isOriginalCardboardDeviceUri(uri)) {
                Log.d("CardboardDeviceParams", "URI recognized as original cardboard device.")
                val deviceParams = CardboardDeviceParams()
                deviceParams.setDefaultValues()
                return deviceParams
            }
            if (!isCardboardDeviceUri(uri)) {
                Log.w(
                    "CardboardDeviceParams",
                    String.format("URI \"%s\" not recognized as cardboard device.", uri)
                )
                return null
            }
            var params: DeviceParams? = null
            val paramsEncoded = uri.getQueryParameter(URI_KEY_PARAMS)
            if (paramsEncoded != null) {
                try {
                    val bytes = Base64.decode(paramsEncoded, 11)
                    params = MessageNano.mergeFrom<MessageNano?>(
                        DeviceParams() as MessageNano,
                        bytes
                    ) as DeviceParams?
                    Log.d("CardboardDeviceParams", "Read cardboard params from URI.")
                } catch (e: Exception) {
                    val s = "CardboardDeviceParams"
                    val s2 = "Parsing cardboard parameters from URI failed: "
                    val value = e.toString().toString()
                    Log.w(
                        s,
                        if (value.length != 0) (s2 + value) else java.lang.String(s2) as String
                    )
                }
            } else {
                Log.w("CardboardDeviceParams", "No cardboard parameters in URI.")
            }
            return CardboardDeviceParams(params)
        }

        fun createFromInputStream(inputStream: InputStream?): CardboardDeviceParams? {
            if (inputStream == null) {
                return null
            }
            try {
                val header = ByteBuffer.allocate(8)
                if (inputStream.read(header.array(), 0, header.array().size) == -1) {
                    Log.e("CardboardDeviceParams", "Error parsing param record: end of stream.")
                    return null
                }
                val sentinel = header.getInt()
                val length = header.getInt()
                if (sentinel != STREAM_SENTINEL) {
                    Log.e(
                        "CardboardDeviceParams",
                        "Error parsing param record: incorrect sentinel."
                    )
                    return null
                }
                val protoBytes = ByteArray(length)
                if (inputStream.read(protoBytes, 0, protoBytes.size) == -1) {
                    Log.e("CardboardDeviceParams", "Error parsing param record: end of stream.")
                    return null
                }
                return CardboardDeviceParams(
                    MessageNano.mergeFrom<MessageNano?>(
                        DeviceParams() as MessageNano,
                        protoBytes
                    ) as DeviceParams?
                )
            } catch (e: InvalidProtocolBufferNanoException) {
                val s = "CardboardDeviceParams"
                val s2 = "Error parsing protocol buffer: "
                val value = e.toString().toString()
                Log.w(s, if (value.length != 0) (s2 + value) else java.lang.String(s2) as String)
            } catch (e2: IOException) {
                val s3 = "CardboardDeviceParams"
                val s4 = "Error reading Cardboard parameters: "
                val value2 = e2.toString().toString()
                Log.w(s3, if (value2.length != 0) (s4 + value2) else java.lang.String(s4) as String)
            }
            return null
        }

        fun createFromNfcContents(tagContents: NdefMessage?): CardboardDeviceParams? {
            if (tagContents == null) {
                Log.w("CardboardDeviceParams", "Could not get contents from NFC tag.")
                return null
            }
            for (record in tagContents.getRecords()) {
                val params: CardboardDeviceParams? = createFromUri(record.toUri())
                if (params != null) {
                    return params
                }
            }
            return null
        }

        init {
            URI_ORIGINAL_CARDBOARD_NFC = Uri.Builder().scheme(URI_SCHEME_LEGACY_CARDBOARD)
                .authority(URI_HOST_LEGACY_CARDBOARD).build()
            URI_ORIGINAL_CARDBOARD_QR_CODE =
                Uri.Builder().scheme(HTTP_SCHEME).authority(URI_HOST_GOOGLE_SHORT)
                    .appendEncodedPath(URI_PATH_CARDBOARD_HOME).build()
        }
    }
}
