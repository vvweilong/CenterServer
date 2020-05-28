package com.example.homecenter.WIFI

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Message
import com.example.homecenter.IServiceAIDL
import com.example.homecenter.IServiceCallback
import com.example.homecenter.base.BaseRemoteService
import com.example.homecenter.commands.ClientConnectEvent
import java.net.ServerSocket
import java.net.Socket

/**
 * 通过局域网 进行链接的操作
 * */
class SocketService : BaseRemoteService() {

    /**
     * 现有的链接集合
     * */
    private val connections = LinkedHashMap<String,ClientConnection>()

    private val listenerThread = HandlerThread("socketListener").apply { start() }

    lateinit var  serverSocket : ServerSocket
    private val listenerHandler =object :Handler(listenerThread.looper){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {//等待连接
//                    val clientSocket = serverSocket.accept()
//                    val connection = ClientConnection(clientSocket, serverSocket)
//                    //连接成功后 加入集合中
//                    connections.put(clientSocket.inetAddress.toString(),connection)
//                    sendEmptyMessage(0)//开启下一次等待
//                    //通知有用户接入
//                    noticeCenter {
//                        receiveEvent(ClientConnectEvent<String>("0","request_connect","userip").toString())
//                    }
                }
                else -> {

                }
            }

        }
    }


    private fun openServerSocket(){
        serverSocket = ServerSocket(9999)
    }


    override fun onBind(intent: Intent): IBinder {
        listenerHandler.sendEmptyMessage(0)//开启 连接监听
        return super.onBind(intent)
    }

    override fun unbindService(conn: ServiceConnection) {
        listenerHandler.removeMessages(0)
        connections.forEach{(key,connect) ->
            if (connect.socket.isConnected) {
                connect.socket.close()
            }
        }
        connections.clear()
        serverSocket.close()
        listenerThread.quit()
        super.unbindService(conn)
    }
}
