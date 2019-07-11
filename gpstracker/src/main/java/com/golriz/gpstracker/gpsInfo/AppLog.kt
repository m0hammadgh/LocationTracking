package com.golriz.gpstracker.gpsInfo

import android.util.Log
import com.golriz.gpstracker.BuildConfig
import java.util.*

/**
 *
 */
object AppLog {
    private val CLASSNAME_TO_ESCAPE = escapedClassNames
    private const val INCLUDE_METHOD = true
    private const val LINE_PREFIX = "APP:"
    private const val MAX_TAG_LENGTH = 50
    private const val PACKAGE_PREFIX = BuildConfig.APPLICATION_ID + "."

    private val callingMethod: String?
        get() {
            val stacks = Thread.currentThread().stackTrace
            for (stack in stacks) {
                val cn = stack.className
                if (cn != null && !CLASSNAME_TO_ESCAPE.contains(cn)) {
                    return cn + "#" + stack.methodName
                }
            }
            return null
        }

    private val escapedClassNames: Set<String>
        get() {
            val set = HashSet<String>()

            set.add("java.lang.Thread")
            set.add("dalvik.system.VMStack")
            set.add(Log::class.java.name)
            set.add(AppLog::class.java.name)

            return set
        }

    fun v(message: String) {
        vInternal(message)
    }

    fun i(message: String) {
        iInternal(message)
    }

    fun d(message: String) {
        dInternal(message)
    }

    fun e(message: String) {
        eInternal(message, null)
    }

    fun e(message: String, e: Exception) {
        eInternal(message, e)
    }

    fun w(message: String) {
        wInternal(message, null)
    }

    fun w(message: String, e: Exception) {
        wInternal(message, e)
    }

    private fun vInternal(message: String) {
        Log.v(calcTag(), calcMessage(message))

    }

    private fun iInternal(message: String) {
        Log.i(calcTag(), calcMessage(message))

    }

    private fun dInternal(message: String) {
        Log.d(calcTag(), calcMessage(message))
    }

    private fun calcTag(): String {
        val caller = callingMethod
        return if (caller == null) {
            ""
        } else {
            val shortTag = caller.replace(PACKAGE_PREFIX, "")
            val shouldBeShorter = shortTag.length > MAX_TAG_LENGTH

            if (shouldBeShorter) {
                val length = shortTag.length
                val start = length - MAX_TAG_LENGTH
                shortTag.substring(start, length)
            } else {
                shortTag
            }
        }
    }

    private fun calcMessage(message: String): String {
        return LINE_PREFIX + message
    }

    private fun eInternal(message: String, e: Exception?) {
        if (INCLUDE_METHOD) {
            Log.e(calcTag(), calcMessage(message), e)
        }
    }

    private fun wInternal(message: String, e: Exception?) {
        if (INCLUDE_METHOD) {
            Log.w(calcTag(), calcMessage(message), e)
        }
    }
}// Avoid instantiation
