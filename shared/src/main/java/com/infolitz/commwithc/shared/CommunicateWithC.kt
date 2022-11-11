package com.infolitz.mycarspeed.shared

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CommunicateWithC : AppCompatActivity() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_speedometer)
//    }

//    external fun MyMethod(): String
    external fun useWiringLib(): Int
    external fun testConn(): Int

    companion object {
        init {
            System.loadLibrary("srkMyLib")
        }
    }
}
