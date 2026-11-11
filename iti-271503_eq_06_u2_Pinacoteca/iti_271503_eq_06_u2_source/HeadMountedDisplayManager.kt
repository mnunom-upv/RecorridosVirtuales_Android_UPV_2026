package upv.pm.may_ago_26.iti_271503.pgu2_eq06

import android.content.Context
import android.util.Log
import android.view.Display
import android.view.WindowManager
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class HeadMountedDisplayManager(private val mContext: Context) {

    val headMountedDisplay: HeadMountedDisplay

    init {
        headMountedDisplay = HeadMountedDisplay(
            createScreenParams(),
            createCardboardDeviceParams()
        )
    }

    fun onResume() {
        val deviceParams = createCardboardDeviceParamsFromExternalStorage()

        if (deviceParams != null && deviceParams != headMountedDisplay.cardboardDeviceParams) {
            headMountedDisplay.cardboardDeviceParams = deviceParams
            Log.i(
                TAG,
                "Successfully read updated device params from external storage"
            )
        }

        val screenParams = createScreenParamsFromExternalStorage(display)

        if (screenParams != null && screenParams != headMountedDisplay.screenParams) {
            headMountedDisplay.screenParams = screenParams
            Log.i(
                TAG,
                "Successfully read updated screen params from external storage"
            )
        }
    }

    fun onPause() {
        // No se requiere lógica por ahora.
    }

    fun updateCardboardDeviceParams(cardboardDeviceParams: CardboardDeviceParams?): Boolean {
        if (
            cardboardDeviceParams == null ||
            cardboardDeviceParams == headMountedDisplay.cardboardDeviceParams
        ) {
            return false
        }

        headMountedDisplay.cardboardDeviceParams = cardboardDeviceParams
        writeCardboardParamsToExternalStorage()
        return true
    }

    fun updateScreenParams(screenParams: ScreenParams?): Boolean {
        if (
            screenParams == null ||
            screenParams == headMountedDisplay.screenParams
        ) {
            return false
        }

        headMountedDisplay.screenParams = screenParams
        return true
    }

    private fun writeCardboardParamsToExternalStorage() {
        var success = false
        var stream: OutputStream? = null

        try {
            stream = BufferedOutputStream(
                FileOutputStream(
                    ConfigUtils.getConfigFile("current_device_params")
                )
            )

            success = headMountedDisplay.cardboardDeviceParams
                ?.writeToOutputStream(stream) ?: false

        } catch (e: FileNotFoundException) {
            Log.e(TAG, "Could not open Cardboard params file for writing: $e")
        } finally {
            try {
                stream?.close()
            } catch (ex: IOException) {
                Log.e(TAG, "Error closing Cardboard params output stream: $ex")
            }
        }

        if (!success) {
            Log.e(
                TAG,
                "Could not write Cardboard parameters to external storage."
            )
        } else {
            Log.i(
                TAG,
                "Successfully wrote Cardboard parameters to external storage."
            )
        }
    }

    private val display: Display
        get() {
            val windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            return windowManager.defaultDisplay
        }

    private fun createScreenParams(): ScreenParams {
        val currentDisplay = display
        val params = createScreenParamsFromExternalStorage(currentDisplay)

        if (params != null) {
            Log.i(
                TAG,
                "Successfully read screen params from external storage"
            )
            return params
        }

        return ScreenParams(currentDisplay)
    }

    private fun createCardboardDeviceParams(): CardboardDeviceParams {
        var params = createCardboardDeviceParamsFromExternalStorage()

        if (params != null) {
            Log.i(
                TAG,
                "Successfully read device params from external storage"
            )
            return params
        }

        params = createCardboardDeviceParamsFromAssetFolder()

        if (params != null) {
            Log.i(
                TAG,
                "Successfully read device params from asset folder"
            )
            writeCardboardParamsToExternalStorage()
            return params
        }

        return CardboardDeviceParams()
    }

    private fun createCardboardDeviceParamsFromAssetFolder(): CardboardDeviceParams? {
        try {
            var stream: InputStream? = null

            try {
                stream = BufferedInputStream(
                    ConfigUtils.openAssetConfigFile(
                        mContext.assets,
                        "current_device_params"
                    )
                )

                return CardboardDeviceParams.createFromInputStream(stream)

            } finally {
                try {
                    stream?.close()
                } catch (ex: IOException) {
                    Log.e(TAG, "Error closing asset stream: $ex")
                }
            }

        } catch (e: FileNotFoundException) {
            Log.d(
                TAG,
                "Bundled Cardboard device parameters not found: $e"
            )
        } catch (e: IOException) {
            Log.e(
                TAG,
                "Error reading config file in asset folder: $e"
            )
        }

        return null
    }

    private fun createCardboardDeviceParamsFromExternalStorage(): CardboardDeviceParams? {
        try {
            var stream: InputStream? = null

            try {
                stream = BufferedInputStream(
                    FileInputStream(
                        ConfigUtils.getConfigFile("current_device_params")
                    )
                )

                return CardboardDeviceParams.createFromInputStream(stream)

            } finally {
                try {
                    stream?.close()
                } catch (ex: IOException) {
                    Log.e(TAG, "Error closing device params stream: $ex")
                }
            }

        } catch (e: FileNotFoundException) {
            Log.d(
                TAG,
                "Cardboard device parameters file not found: $e"
            )
            return null
        }
    }

    private fun createScreenParamsFromExternalStorage(display: Display): ScreenParams? {
        try {
            var stream: InputStream? = null

            try {
                stream = BufferedInputStream(
                    FileInputStream(
                        ConfigUtils.getConfigFile("phone_params")
                    )
                )

                return ScreenParams.createFromInputStream(display, stream)

            } finally {
                try {
                    stream?.close()
                } catch (ex: IOException) {
                    Log.e(TAG, "Error closing screen params stream: $ex")
                }
            }

        } catch (e: FileNotFoundException) {
            Log.d(
                TAG,
                "Cardboard screen parameters file not found: $e"
            )
            return null
        }
    }

    companion object {
        private const val TAG = "HeadMountedDisplayManager"
    }
}