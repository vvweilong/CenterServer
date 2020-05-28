package com.example.homecenter.Bluetooth

import android.content.ComponentName
import android.os.IBinder
import android.util.Log
import com.example.homecenter.base.BaseServiceConn
typealias CallbackFun =(data:String?)->Unit
class BleServiceConn: BaseServiceConn() {
    override fun dispatch(result: String?) {
        Log.i("BleServiceConn","dispatch  $result")
        actionCallback?.onClientConnected(result)
        mCallback?.invoke(result)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        super.onServiceDisconnected(name)
        Log.i("BleServiceConn","onServiceDisconnected")
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        super.onServiceConnected(name, service)
        Log.i("BleServiceConn","onServiceConnected")
    }

    private var actionCallback:BleActionCallback?=null
    private var mCallback:CallbackFun?=null
    fun setActionCallback(callback:(data:String?)->Unit){
        mCallback = callback
    }

    interface BleActionCallback{
        fun onClientConnected(data:String?)
    }
}