package com.example.homecenter.base

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.os.HandlerCompat
import com.example.homecenter.IServiceAIDL
import com.example.homecenter.IServiceCallback
import java.util.concurrent.Executors

abstract class BaseRemoteService: Service() {

    protected  var mCallback: IServiceCallback?=null

    companion object{
        /**
         * 线程池
         * */
        private val threadPool = Executors.newCachedThreadPool()
    }

    protected val mainHandler = Handler(Looper.getMainLooper())

    protected  val binder = object : IServiceAIDL.Stub() {
        override fun registCallback(callback: IServiceCallback?) {
            mCallback=callback
        }

        override fun sendCommand(cmd: String?) {

        }
    }


    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun noticeCenter(work: () -> Unit){
        mainHandler.post{
            work.invoke()
        }
    }

    protected fun receiveEvent(event:String){
        mCallback?.receiveResult(event)
    }

    fun startWork(work:()->Unit){
        threadPool.submit {
            work.invoke()
        }
    }
}