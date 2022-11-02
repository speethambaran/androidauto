package com.infolitz.newcar.shared

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CommunicateWithC : AppCompatActivity() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_speedometer)
//    }

    external fun Method(): String

    companion object {
        init {
            System.loadLibrary("cpp_code")
        }
    }
}
