package com.buylog.platform

object MD5 {
    private val IV = intArrayOf(
        0x67452301.toInt(), 0xEFCDAB89.toInt(), 0x98BADCFE.toInt(), 0x10325476.toInt()
    )

    fun hash(input: String): String {
        val bytes = digest(input.encodeToByteArray())
        return bytes.joinToString("") { byteToHex(it) }
    }

    private fun byteToHex(b: Byte): String {
        val hex = "0123456789ABCDEF"
        val i = b.toInt() and 0xFF
        return "${hex[i shr 4]}${hex[i and 0x0F]}"
    }

    private fun digest(input: ByteArray): ByteArray {
        val state = IntArray(16)
        state[0] = IV[0]; state[1] = IV[1]; state[2] = IV[2]; state[3] = IV[3]
        val padded = pad(input)
        val blocks = padded.size / 64
        for (i in 0 until blocks) {
            val block = IntArray(16)
            for (j in 0 until 16) {
                val offset = i * 64 + j * 4
                block[j] = (padded[offset].toInt() and 0xFF) or
                        ((padded[offset + 1].toInt() and 0xFF) shl 8) or
                        ((padded[offset + 2].toInt() and 0xFF) shl 16) or
                        ((padded[offset + 3].toInt() and 0xFF) shl 24)
            }
            transform(state, block)
        }
        val result = ByteArray(16)
        for (i in 0 until 4) {
            val v = state[i]
            result[i * 4] = (v and 0xFF).toByte()
            result[i * 4 + 1] = ((v ushr 8) and 0xFF).toByte()
            result[i * 4 + 2] = ((v ushr 16) and 0xFF).toByte()
            result[i * 4 + 3] = ((v ushr 24) and 0xFF).toByte()
        }
        return result
    }

    private fun pad(input: ByteArray): ByteArray {
        val msgLen = input.size
        val bitLen = msgLen * 8L
        val padLen = if (msgLen % 64 < 56) 56 - msgLen % 64 else 120 - msgLen % 64
        val totalLen = msgLen + padLen + 8
        val result = ByteArray(totalLen)
        input.copyInto(result)
        result[msgLen] = 0x80.toByte()
        for (i in 0 until 8) {
            result[totalLen - 8 + i] = ((bitLen ushr (i * 8)) and 0xFF).toByte()
        }
        return result
    }

    private fun transform(state: IntArray, block: IntArray) {
        var a = state[0]; var b = state[1]; var c = state[2]; var d = state[3]
        val S = intArrayOf(7, 12, 17, 22, 5, 9, 14, 20, 4, 11, 16, 23, 6, 10, 15, 21)
        val K = intArrayOf(
            0xd76aa478.toInt(), 0xe8c7b756.toInt(), 0x242070db.toInt(), 0xc1bdceee.toInt(),
            0xf57c0faf.toInt(), 0x4787c62a.toInt(), 0xa8304613.toInt(), 0xfd469501.toInt(),
            0x698098d8.toInt(), 0x8b44f7af.toInt(), 0xffff5bb1.toInt(), 0x895cd7be.toInt(),
            0x6b901122.toInt(), 0xfd987193.toInt(), 0xa679438e.toInt(), 0x49b40821.toInt(),
            0xf61e2562.toInt(), 0xc040b340.toInt(), 0x265e5a51.toInt(), 0xe9b6c7aa.toInt(),
            0xd62f105d.toInt(), 0x02441453.toInt(), 0xd8a1e681.toInt(), 0xe7d3fbc8.toInt(),
            0x21e1cde6.toInt(), 0xc33707d6.toInt(), 0xf4d50d87.toInt(), 0x455a14ed.toInt(),
            0xa9e3e905.toInt(), 0xfcefa3f8.toInt(), 0x676f02d9.toInt(), 0x8d2a4c8a.toInt(),
            0xfffa3942.toInt(), 0x8771f681.toInt(), 0x6d9d6122.toInt(), 0xfde5380c.toInt(),
            0xa4beea44.toInt(), 0x4bdecfa9.toInt(), 0xf6bb4b60.toInt(), 0xbebfbc70.toInt(),
            0x289b7ec6.toInt(), 0xeaa127fa.toInt(), 0xd4ef3085.toInt(), 0x04881d05.toInt(),
            0xd9d4d039.toInt(), 0xe6db99e5.toInt(), 0x1fa27cf8.toInt(), 0xc4ac5665.toInt(),
            0xf4292244.toInt(), 0x432aff97.toInt(), 0xab9423a7.toInt(), 0xfc93a039.toInt(),
            0x655b59c3.toInt(), 0x8f0ccc92.toInt(), 0xffeff47d.toInt(), 0x85845dd1.toInt(),
            0x6fa87e4f.toInt(), 0xfe2ce6e0.toInt(), 0xa3014314.toInt(), 0x4e0811a1.toInt(),
            0xf7537e82.toInt(), 0xbd3af235.toInt(), 0x2ad7d2bb.toInt(), 0xeb86d391.toInt()
        )
        for (i in 0 until 64) {
            val f: Int; val g: Int
            when (i / 16) {
                0 -> { f = (b and c) or (b.inv() and d); g = i }
                1 -> { f = (d and b) or (d.inv() and c); g = (5 * i + 1) % 16 }
                2 -> { f = b xor c xor d; g = (3 * i + 5) % 16 }
                else -> { f = c xor (b or d.inv()); g = (7 * i) % 16 }
            }
            val temp = d; d = c; c = b
            b = b + rotateLeft(a + f + K[i] + block[g], S[(i / 16) * 4 + i % 4]); a = temp
        }
        state[0] += a; state[1] += b; state[2] += c; state[3] += d
    }

    private fun rotateLeft(value: Int, bits: Int): Int = ((value shl bits) or (value ushr (32 - bits)))
}
