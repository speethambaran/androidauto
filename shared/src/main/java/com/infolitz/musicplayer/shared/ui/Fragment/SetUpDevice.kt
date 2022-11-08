package com.infolitz.musicplayer.shared.ui.Fragment

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import me.jahnen.libaums.core.UsbMassStorageDevice
import me.jahnen.libaums.core.fs.FileSystem
import java.io.IOException

class SetUpDevice : AppCompatActivity() {

    lateinit var adapter: UsbFileListAdapter
    lateinit var currentFs: FileSystem
    lateinit var massStorageDevices: Array<UsbMassStorageDevice>
    private var currentDevice = -1


    private fun setupDevice() {
        try {
            massStorageDevices[currentDevice].init()

            // we always use the first partition of the device
            currentFs = massStorageDevices[currentDevice].partitions[0].fileSystem.also {
                Log.d("CDEVICE", "Capacity: " + it.capacity)
                Log.d("CDEVICE", "Occupied Space: " + it.occupiedSpace)
                Log.d("CDEVICE", "Free Space: " + it.freeSpace)
                Log.d("CDEVICE", "Chunk size: " + it.chunkSize)
            }

            val root = currentFs.rootDirectory
            val actionBar = supportActionBar
            actionBar!!.title = currentFs.volumeLabel
//            listView.adapter = UsbFileListAdapter(this, root).apply { adapter = this }

        } catch (e: IOException) {
            Log.e("CDEVICE", "error setting up device", e)
        }
    }


}