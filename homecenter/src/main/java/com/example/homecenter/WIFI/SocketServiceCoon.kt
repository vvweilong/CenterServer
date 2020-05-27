package com.example.homecenter.WIFI

import android.content.ComponentName
import android.os.IBinder
import android.util.Log
import com.example.homecenter.base.BaseServiceConn

/**
 * 局域网内通过 socket 链接进行的通信服务
 * */
class SocketServiceCoon: BaseServiceConn() {
    override fun dispatch(result: String?) {

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        super.onServiceDisconnected(name)
        Log.i("SocketServiceCoon","onServiceDisconnected")
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        super.onServiceConnected(name, service)
        Log.i("SocketServiceCoon","onServiceConnected")
    }
}