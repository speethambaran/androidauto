package com.infolitz.mycarspeed.shared

import androidx.appcompat.app.AppCompatActivity

class CommunicateWithC : AppCompatActivity() {
    external fun useWiringLib():String
    companion object {
        init {
            System.loadLibrary("srkMyLib")
        }
    }
}
