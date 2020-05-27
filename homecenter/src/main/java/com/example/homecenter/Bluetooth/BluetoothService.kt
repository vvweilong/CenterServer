package com.example.homecenter.Bluetooth

import android.app.Service
import android.bluetooth.BluetoothGatt
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import com.example.homecenter.IServiceAIDL
import com.example.homecenter.IServiceCallback
import com.example.homecenter.base.BaseRemoteService

/**
 * 通过蓝牙链接 的服务功能
 * */
class BluetoothService : BaseRemoteService() {
    val bleConnections = LinkedHashMap<String,BluetoothGatt>()





    override fun onBind(intent: Intent): IBinder {



        return super.onBind(intent)
    }
}
