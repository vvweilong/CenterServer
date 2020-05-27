package com.example.homecenter.Bluetooth

import android.content.ComponentName
import android.os.IBinder
import android.util.Log
import com.example.homecenter.base.BaseServiceConn

class BleServiceConn: BaseServiceConn() {
    override fun dispatch(result: String?) {

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        super.onServiceDisconnected(name)
        Log.i("BleServiceConn","onServiceDisconnected")
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        super.onServiceConnected(name, service)
        Log.i("BleServiceConn","onServiceConnected")
    }
}