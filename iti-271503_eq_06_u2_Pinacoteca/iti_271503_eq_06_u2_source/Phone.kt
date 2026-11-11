package upv.pm.may_ago_26.iti_271503.pgu2_eq06

import com.google.protobuf.nano.CodedInputByteBufferNano
import com.google.protobuf.nano.CodedOutputByteBufferNano
import com.google.protobuf.nano.InternalNano
import com.google.protobuf.nano.InvalidProtocolBufferNanoException
import com.google.protobuf.nano.MessageNano
import com.google.protobuf.nano.WireFormatNano

import java.io.IOException
import kotlin.concurrent.Volatile

interface Phone {

    class PhoneParams : MessageNano() {

        private var bitField0_ = 0

        var xPpi: Float = 0f
            private set

        var yPpi: Float = 0f
            private set

        var bottomBezelHeight: Float = 0f
            private set

        var gyroBias: FloatArray = WireFormatNano.EMPTY_FLOAT_ARRAY

        init {
            clear()
        }

        fun setXPpi(value: Float): PhoneParams {
            xPpi = value
            bitField0_ = bitField0_ or 0x1
            return this
        }

        fun hasXPpi(): Boolean {
            return (bitField0_ and 0x1) != 0
        }

        fun clearXPpi(): PhoneParams {
            xPpi = 0.0f
            bitField0_ = bitField0_ and -0x2
            return this
        }

        fun setYPpi(value: Float): PhoneParams {
            yPpi = value
            bitField0_ = bitField0_ or 0x2
            return this
        }

        fun hasYPpi(): Boolean {
            return (bitField0_ and 0x2) != 0
        }

        fun clearYPpi(): PhoneParams {
            yPpi = 0.0f
            bitField0_ = bitField0_ and -0x3
            return this
        }

        fun setBottomBezelHeight(value: Float): PhoneParams {
            bottomBezelHeight = value
            bitField0_ = bitField0_ or 0x4
            return this
        }

        fun hasBottomBezelHeight(): Boolean {
            return (bitField0_ and 0x4) != 0
        }

        fun clearBottomBezelHeight(): PhoneParams {
            bottomBezelHeight = 0.0f
            bitField0_ = bitField0_ and -0x5
            return this
        }

        fun clear(): PhoneParams {
            bitField0_ = 0
            xPpi = 0.0f
            yPpi = 0.0f
            bottomBezelHeight = 0.0f
            gyroBias = WireFormatNano.EMPTY_FLOAT_ARRAY
            cachedSize = -1
            return this
        }

        @Throws(IOException::class)
        override fun writeTo(output: CodedOutputByteBufferNano) {
            if ((bitField0_ and 0x1) != 0) {
                output.writeFloat(1, xPpi)
            }

            if ((bitField0_ and 0x2) != 0) {
                output.writeFloat(2, yPpi)
            }

            if ((bitField0_ and 0x4) != 0) {
                output.writeFloat(3, bottomBezelHeight)
            }

            if (gyroBias.isNotEmpty()) {
                val dataSize = 4 * gyroBias.size
                output.writeRawVarint32(34)
                output.writeRawVarint32(dataSize)

                for (value in gyroBias) {
                    output.writeFloatNoTag(value)
                }
            }

            super.writeTo(output)
        }

        override fun computeSerializedSize(): Int {
            var size = super.computeSerializedSize()

            if ((bitField0_ and 0x1) != 0) {
                size += CodedOutputByteBufferNano.computeFloatSize(1, xPpi)
            }

            if ((bitField0_ and 0x2) != 0) {
                size += CodedOutputByteBufferNano.computeFloatSize(2, yPpi)
            }

            if ((bitField0_ and 0x4) != 0) {
                size += CodedOutputByteBufferNano.computeFloatSize(3, bottomBezelHeight)
            }

            if (gyroBias.isNotEmpty()) {
                val dataSize = 4 * gyroBias.size
                size += dataSize
                size += 1
                size += CodedOutputByteBufferNano.computeRawVarint32Size(dataSize)
            }

            return size
        }

        @Throws(IOException::class)
        override fun mergeFrom(input: CodedInputByteBufferNano): PhoneParams {
            while (true) {
                when (val tag = input.readTag()) {
                    0 -> {
                        return this
                    }

                    13 -> {
                        xPpi = input.readFloat()
                        bitField0_ = bitField0_ or 0x1
                    }

                    21 -> {
                        yPpi = input.readFloat()
                        bitField0_ = bitField0_ or 0x2
                    }

                    29 -> {
                        bottomBezelHeight = input.readFloat()
                        bitField0_ = bitField0_ or 0x4
                    }

                    37 -> {
                        val arrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 37)
                        var i = gyroBias.size
                        val newArray = FloatArray(i + arrayLength)

                        if (i != 0) {
                            System.arraycopy(gyroBias, 0, newArray, 0, i)
                        }

                        while (i < newArray.size - 1) {
                            newArray[i] = input.readFloat()
                            input.readTag()
                            i++
                        }

                        newArray[i] = input.readFloat()
                        gyroBias = newArray
                    }

                    34 -> {
                        val length = input.readRawVarint32()
                        val limit = input.pushLimit(length)
                        val arrayLength = length / 4

                        var j = gyroBias.size
                        val newArray = FloatArray(j + arrayLength)

                        if (j != 0) {
                            System.arraycopy(gyroBias, 0, newArray, 0, j)
                        }

                        while (j < newArray.size) {
                            newArray[j] = input.readFloat()
                            j++
                        }

                        gyroBias = newArray
                        input.popLimit(limit)
                    }

                    else -> {
                        if (!WireFormatNano.parseUnknownField(input, tag)) {
                            return this
                        }
                    }
                }
            }
        }

        companion object {

            @Volatile
            private var _emptyArray: Array<PhoneParams?>? = null

            fun emptyArray(): Array<PhoneParams?> {
                if (_emptyArray == null) {
                    synchronized(InternalNano.LAZY_INIT_LOCK) {
                        if (_emptyArray == null) {
                            _emptyArray = arrayOfNulls(0)
                        }
                    }
                }

                return _emptyArray!!
            }

            @Throws(InvalidProtocolBufferNanoException::class)
            fun parseFrom(data: ByteArray): PhoneParams {
                return MessageNano.mergeFrom(PhoneParams(), data) as PhoneParams
            }

            @Throws(IOException::class)
            fun parseFrom(input: CodedInputByteBufferNano): PhoneParams {
                return PhoneParams().mergeFrom(input)
            }
        }
    }
}