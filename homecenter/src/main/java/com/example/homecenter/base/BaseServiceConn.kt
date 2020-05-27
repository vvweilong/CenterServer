package com.example.homecenter.base

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.homecenter.IServiceAIDL
import com.example.homecenter.IServiceCallback
import org.json.JSONObject

/**
 *
 * 最基本的通信功能  采用json 格式字符串 进行跨进程通信
 * */
abstract class BaseServiceConn:ServiceConnection {

    val TAG = javaClass.simpleName
    protected  var iBinder: IServiceAIDL?=null

    abstract fun dispatch(result:String?)

    fun <T> getResult(result: String?):T?{
        return null
    }

    private val remoteCallback = object : IServiceCallback.Stub() {
        override fun receiveResult(result: String?) {
            Log.i(TAG,"sendResult :$result")
            dispatch(result)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        iBinder =null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        iBinder = IServiceAIDL.Stub.asInterface(service)
        iBinder?.registCallback(remoteCallback)
    }


    fun sendCommand(json:String){
        iBinder?.sendCommand(json)
    }

    protected fun buildJsonStr(jsonObj:JSONObject):String{
        return jsonObj.toString()
    }


}