package upv.pm.may_ago_26.iti_271503.pgu2_eq04

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.TagLostException
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Build
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import java.io.IOException
import java.lang.String
import java.util.Timer
import java.util.TimerTask
import kotlin.text.StringBuilder

//import com.google.vrtoolkit.cardboard.*;
class NfcSensor private constructor(context: Context) {
    private val mContext: Context
    private val mNfcAdapter: NfcAdapter?
    private val mTagLock: Any
    private val mListeners: MutableList<ListenerHelper>
    private val mNfcIntentFilters: Array<IntentFilter?>
    private var mCurrentNdef: Ndef? = null
    private var mCurrentTag: Tag? = null
    private var mCurrentTagIsCardboard = false
    private var mNfcDisconnectTimer: Timer? = null
    private var mTagConnectionFailures = 0

    init {
        this.mContext = context.getApplicationContext()
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this.mContext)
        this.mListeners = ArrayList<ListenerHelper>()
        this.mTagLock = Any()
        if (this.mNfcAdapter != null) {
            val ndefIntentFilter = IntentFilter("android.nfc.action.NDEF_DISCOVERED")
            ndefIntentFilter.addAction("android.nfc.action.TECH_DISCOVERED")
            ndefIntentFilter.addAction("android.nfc.action.TAG_DISCOVERED")
            this.mNfcIntentFilters = arrayOf<IntentFilter?>(ndefIntentFilter)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.mContext.registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        this@NfcSensor.onNfcIntent(intent)
                    }
                } as BroadcastReceiver, ndefIntentFilter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                this.mContext.registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        this@NfcSensor.onNfcIntent(intent)
                    }
                } as BroadcastReceiver, ndefIntentFilter)
            }
        } else {
            this.mNfcIntentFilters = arrayOfNulls<IntentFilter>(0)
        }
    }

    fun addOnCardboardNfcListener(listener: OnCardboardNfcListener?) {
        if (listener == null) {
            return
        }
        synchronized(this.mListeners) {
            for (helper in this.mListeners) {
                if (helper.listener === listener) {
                    return
                }
            }
            this.mListeners.add(ListenerHelper(listener, Handler()))
        }
    }

    fun removeOnCardboardNfcListener(listener: OnCardboardNfcListener?) {
        if (listener == null) {
            return
        }
        synchronized(this.mListeners) {
            for (helper in this.mListeners) {
                if (helper.listener === listener) {
                    this.mListeners.remove(helper)
                }
            }
        }
    }

    val isNfcSupported: Boolean
        get() = this.mNfcAdapter != null

    val isNfcEnabled: Boolean
        get() = this.isNfcSupported && this.mNfcAdapter!!.isEnabled()

    val isDeviceInCardboard: Boolean
        get() {
            synchronized(this.mTagLock) {
                return this.mCurrentTagIsCardboard
            }
        }

    val tagContents: NdefMessage?
        get() {
            synchronized(this.mTagLock) {
                return if (this.mCurrentNdef != null) this.mCurrentNdef!!.getCachedNdefMessage() else null
            }
        }

    @get:Throws(TagLostException::class, IOException::class, FormatException::class)
    val currentTagContents: NdefMessage?
        get() {
            synchronized(this.mTagLock) {
                return if (this.mCurrentNdef != null) this.mCurrentNdef!!.getNdefMessage() else null
            }
        }

    val tagCapacity: Int
        get() {
            synchronized(this.mTagLock) {
                checkNotNull(this.mCurrentNdef) { "No NFC tag" }
                return this.mCurrentNdef!!.getMaxSize()
            }
        }

    @Throws(TagLostException::class, IOException::class, IllegalArgumentException::class)
    fun writeUri(uri: Uri?) {
        synchronized(this.mTagLock) {
            checkNotNull(this.mCurrentTag) { "No NFC tag found" }
            var currentMessage: NdefMessage? = null
            var newMessage: NdefMessage? = null
            val newRecord = NdefRecord.createUri(uri)
            try {
                currentMessage = this.currentTagContents
            } catch (e3: Exception) {
                currentMessage = this.tagContents
            }
            if (currentMessage != null) {
                val newRecords = ArrayList<NdefRecord?>()
                var recordFound = false
                for (record in currentMessage.getRecords()) {
                    if (this.isCardboardNdefRecord(record)) {
                        if (!recordFound) {
                            newRecords.add(newRecord)
                            recordFound = true
                        }
                    } else {
                        newRecords.add(record)
                    }
                }
                newMessage = NdefMessage(newRecords.toTypedArray<NdefRecord?>())
            }
            if (newMessage == null) {
                newMessage = NdefMessage(arrayOf<NdefRecord?>(newRecord))
            }
            Label_0432@{
                if (this.mCurrentNdef != null) {
                    if (!this.mCurrentNdef!!.isConnected()) {
                        this.mCurrentNdef!!.connect()
                    }
                    require(this.mCurrentNdef!!.getMaxSize() >= newMessage.getByteArrayLength()) {
                        StringBuilder(
                            82
                        ).append("Not enough capacity in NFC tag. Capacity: ")
                            .append(this.mCurrentNdef!!.getMaxSize()).append(" bytes, ")
                            .append(newMessage.getByteArrayLength()).append(" required.").toString()
                    }
                    try {
                        this.mCurrentNdef!!.writeNdefMessage(newMessage)
                    } catch (e: FormatException) {
                        val s = "Internal error when writing to NFC tag: "
                        val value = e.toString().toString()
                        throw RuntimeException(if (value.length != 0) (s + value) else String(s) as kotlin.String)
                    }
                } else {
                    val ndef = NdefFormatable.get(this.mCurrentTag)
                    if (ndef == null) {
                        throw IOException("Could not find a writable technology for the NFC tag")
                    }
                    Log.w(
                        "NfcSensor",
                        "Ndef technology not available. Falling back to NdefFormattable."
                    )
                    try {
                        ndef.connect()
                        ndef.format(newMessage)
                        ndef.close()
                    } catch (e2: FormatException) {
                        val s2 = "Internal error when writing to NFC tag: "
                        val value2 = e2.toString().toString()
                        throw RuntimeException(if (value2.length != 0) (s2 + value2) else String(s2) as kotlin.String)
                    }
                }
            }
            this.onNewNfcTag(this.mCurrentTag)
        }
    }

    fun onResume(activity: Activity) {
        if (!this.isNfcEnabled) {
            return
        }
        val intent = Intent("android.nfc.action.NDEF_DISCOVERED")
        intent.setPackage(activity.getPackageName())
        // Example fix in NfcSensor.onResume() (line 196)
        val pendingIntent = PendingIntent.getBroadcast(
            mContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        ) // or FLAG_MUTABLE
        this.mNfcAdapter!!.enableForegroundDispatch(
            activity,
            pendingIntent,
            this.mNfcIntentFilters,
            null as Array<Array<kotlin.String?>?>?
        )
    }

    fun onPause(activity: Activity?) {
        if (!this.isNfcEnabled) {
            return
        }
        this.mNfcAdapter!!.disableForegroundDispatch(activity)
    }

    fun onNfcIntent(intent: Intent?) {
        if (!this.isNfcEnabled || intent == null || !this.mNfcIntentFilters[0]!!.matchAction(intent.getAction())) {
            return
        }
        this.onNewNfcTag(intent.getParcelableExtra<Parcelable?>("android.nfc.extra.TAG") as Tag?)
    }

    private fun onNewNfcTag(nfcTag: Tag?) {
        if (nfcTag == null) {
            return
        }
        synchronized(this.mTagLock) {
            val previousTag = this.mCurrentTag
            val previousNdef = this.mCurrentNdef
            val previousTagWasCardboard = this.mCurrentTagIsCardboard
            this.closeCurrentNfcTag()
            this.mCurrentTag = nfcTag
            this.mCurrentNdef = Ndef.get(nfcTag)
            if (this.mCurrentNdef == null) {
                if (previousTagWasCardboard) {
                    this.sendDisconnectionEvent()
                }
                return
            }
            var isSameTag = false
            if (previousNdef != null) {
                val tagId1 = this.mCurrentTag!!.getId()
                val tagId2 = previousTag!!.getId()
                isSameTag = (tagId1 != null && tagId2 != null && tagId1.contentEquals(tagId2))
                if (!isSameTag && previousTagWasCardboard) {
                    this.sendDisconnectionEvent()
                }
            }
            val nfcTagContents: NdefMessage?
            try {
                this.mCurrentNdef!!.connect()
                nfcTagContents = this.mCurrentNdef!!.getCachedNdefMessage()
            } catch (e: Exception) {
                val s = "NfcSensor"
                val s2 = "Error reading NFC tag: "
                val value = e.toString().toString()
                Log.e(s, if (value.length != 0) (s2 + value) else String(s2) as kotlin.String)
                if (isSameTag && previousTagWasCardboard) {
                    this.sendDisconnectionEvent()
                }
                return
            }
            this.mCurrentTagIsCardboard = this.isCardboardNdefMessage(nfcTagContents)
            if (!isSameTag && this.mCurrentTagIsCardboard) {
                synchronized(this.mListeners) {
                    for (listener in this.mListeners) {
                        listener.onInsertedIntoCardboard(
                            CardboardDeviceParams.Companion.createFromNfcContents(
                                nfcTagContents
                            )
                        )
                    }
                }
            }
            if (this.mCurrentTagIsCardboard) {
                this.mTagConnectionFailures = 0
                (Timer("NFC disconnect timer").also { this.mNfcDisconnectTimer = it }).schedule(
                    object : TimerTask() {
                        override fun run() {
                            synchronized(this@NfcSensor.mTagLock) {
                                if (!this@NfcSensor.mCurrentNdef!!.isConnected()) {
                                    ++this@NfcSensor.mTagConnectionFailures
                                    if (this@NfcSensor.mTagConnectionFailures > 1) {
                                        this@NfcSensor.closeCurrentNfcTag()
                                        this@NfcSensor.sendDisconnectionEvent()
                                    }
                                }
                            }
                        }
                    },
                    250L,
                    250L
                )
            }
        }
    }

    private fun closeCurrentNfcTag() {
        if (this.mNfcDisconnectTimer != null) {
            this.mNfcDisconnectTimer!!.cancel()
        }
        if (this.mCurrentNdef == null) {
            return
        }
        try {
            this.mCurrentNdef!!.close()
        } catch (e: IOException) {
            Log.w("NfcSensor", e.toString())
        }
        this.mCurrentTag = null
        this.mCurrentNdef = null
        this.mCurrentTagIsCardboard = false
    }

    private fun sendDisconnectionEvent() {
        synchronized(this.mListeners) {
            for (listener in this.mListeners) {
                listener.onRemovedFromCardboard()
            }
        }
    }

    private fun isCardboardNdefMessage(message: NdefMessage?): Boolean {
        if (message == null) {
            return false
        }
        for (record in message.getRecords()) {
            if (this.isCardboardNdefRecord(record)) {
                return true
            }
        }
        return false
    }

    private fun isCardboardNdefRecord(record: NdefRecord?): Boolean {
        if (record == null) {
            return false
        }
        val uri = record.toUri()
        return uri != null && CardboardDeviceParams.Companion.isCardboardUri(uri)
    }

    private class ListenerHelper(
        val listener: OnCardboardNfcListener,
        private val mHandler: Handler
    ) : OnCardboardNfcListener {
        override fun onInsertedIntoCardboard(deviceParams: CardboardDeviceParams?) {
            this.mHandler.post(object : Runnable {
                override fun run() {
                    this@ListenerHelper.listener.onInsertedIntoCardboard(deviceParams)
                }
            } as Runnable)
        }

        override fun onRemovedFromCardboard() {
            this.mHandler.post(object : Runnable {
                override fun run() {
                    this@ListenerHelper.listener.onRemovedFromCardboard()
                }
            } as Runnable)
        }
    }

    interface OnCardboardNfcListener {
        fun onInsertedIntoCardboard(p0: CardboardDeviceParams?)

        fun onRemovedFromCardboard()
    }

    companion object {
        private const val TAG = "NfcSensor"
        private const val MAX_CONNECTION_FAILURES = 1
        private const val NFC_POLLING_INTERVAL_MS = 250L
        private var sInstance: NfcSensor? = null
        fun getInstance(context: Context): NfcSensor {
            if (sInstance == null) {
                sInstance = NfcSensor(context)
            }
            return sInstance!!
        }
    }
}
