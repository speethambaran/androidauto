package com.infolitz.mycarspeed.shared

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.PyException
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.infolitz.commwithc.shared.R

class CommunicateWithC : AppCompatActivity() {
    fun pyKot():String {

        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()
        val module = py.getModule("uart") //setting the py
        Log.e("haiii in kotlin","::kotlinn")

            try {
                val bytes1 = module.callAttr("uart_method").toJava(String::class.java)
                Log.e("yess returned::", bytes1.toString())
                return (bytes1.toString())

            } catch (e: PyException) {
                Log.e("failed returned::", e.message.toString())
                return e.message.toString()
            }

    }
    external fun useWiringLib():String
    companion object {
        init {
            System.loadLibrary("srkMyLib")
        }
    }
}
