package com.example.homecenter.Bluetooth

import android.app.Service
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import com.example.homecenter.IServiceAIDL
import com.example.homecenter.IServiceCallback
import com.example.homecenter.base.BaseRemoteService
import com.example.homecenter.commands.ClientConnectEvent
import com.example.homecenter.commands.GeneralCmd
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * 通过蓝牙链接 的服务功能
 * */
class BluetoothService : BaseRemoteService() {
    val TAG = "BluetoothService"
    lateinit var bluetoothManager :BluetoothManager

    lateinit var bluetoothAdapter:BluetoothAdapter

    lateinit var gattServer :BluetoothGattServer


    val connectionDevices = HashMap<String,BluetoothDevice>()

    private val listenerThread = HandlerThread("socketListener").apply { start() }


    val gattServerCallback = object :BluetoothGattServerCallback(){
        override fun onDescriptorReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            descriptor: BluetoothGattDescriptor?
        ) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor)
            Log.i(TAG,"onDescriptorReadRequest :")
        }

        override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
            super.onNotificationSent(device, status)
            Log.i(TAG,"onNotificationSent :")
        }

        override fun onMtuChanged(device: BluetoothDevice?, mtu: Int) {
            super.onMtuChanged(device, mtu)
            Log.i(TAG,"onMtuChanged :")
        }

        override fun onPhyUpdate(device: BluetoothDevice?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyUpdate(device, txPhy, rxPhy, status)
            Log.i(TAG,"onPhyUpdate :")
        }

        override fun onExecuteWrite(device: BluetoothDevice?, requestId: Int, execute: Boolean) {
            super.onExecuteWrite(device, requestId, execute)
            Log.i(TAG,"onExecuteWrite :")
        }

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            super.onCharacteristicWriteRequest(
                device,
                requestId,
                characteristic,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
            Log.i(TAG,"onCharacteristicWriteRequest : ${device?.address} $value")
            runWork {//线程池中进行数据处理
                //写命令
                val dataStr=if (value != null) {
                    String(value, Charset.defaultCharset())
                }else{
                    ""
                }

                mCallback?.receiveResult(GeneralCmd<String>("$requestId","action","$dataStr").toString())
                gattServer.sendResponse(device,requestId,BluetoothGatt.GATT_SUCCESS,0,value)
            }

        }

        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
            Log.i(TAG,"onCharacteristicReadRequest :")
            gattServer.sendResponse(device,requestId,BluetoothGatt.GATT_SUCCESS,0,"RESPONSE".toByteArray())
        }

        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            Log.i(TAG,"onConnectionStateChange :")
            when(newState){
                BluetoothAdapter.STATE_CONNECTED->{
                    deviceConnected(device)
                }
                BluetoothAdapter.STATE_DISCONNECTED->{
                    deviceDisconnected(device)
                }
            }

        }

        override fun onPhyRead(device: BluetoothDevice?, txPhy: Int, rxPhy: Int, status: Int) {
            super.onPhyRead(device, txPhy, rxPhy, status)
            Log.i(TAG,"onPhyRead :")
        }

        override fun onDescriptorWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            descriptor: BluetoothGattDescriptor?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            super.onDescriptorWriteRequest(
                device,
                requestId,
                descriptor,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
            Log.i(TAG,"onDescriptorWriteRequest :")
        }

        override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
            super.onServiceAdded(status, service)
            Log.i(TAG,"onServiceAdded : ${service?.uuid}")
        }
    }

    private fun deviceDisconnected(device: BluetoothDevice?) {
        if (device != null) {
            connectionDevices.remove(device.address)
        }
        gattServer.cancelConnection(device)
        startAdvertise()
        val connectCmd = GeneralCmd<ClientConnectEvent>(
            "-1",
            "disconnect",
            ClientConnectEvent(device?.address ?: "")
        )
        mCallback?.receiveResult(connectCmd.toString())
    }

    private fun deviceConnected(device: BluetoothDevice?) {
        if (device != null) {
            connectionDevices[device.address] = device
        }
        stopAdvertise()
        val connectCmd = GeneralCmd<ClientConnectEvent>(
            "0",
            "connect",
            ClientConnectEvent(device?.address ?: "")
        )
        mCallback?.receiveResult(connectCmd.toString())
    }

    private fun openGattServer(){
        if (this::gattServer.isInitialized) {
            gattServer.clearServices()
            gattServer.close()
        }
        gattServer = bluetoothManager.openGattServer(this, gattServerCallback)
        for (service in BleServiceConfig.getServices()) {
            //todo  这里需要一个一个进行添加 同时添加会造成 handle 错误
            gattServer.addService(service)
        }
    }

    val advertiseCallback=object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            Log.i(TAG,"onStartSuccess : $settingsInEffect")

            openGattServer()
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Log.i(TAG,"onStartFailure errorCode = $errorCode")
        }

    }

    private fun startAdvertise(){
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(true)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .setIncludeTxPowerLevel(true)
            .build()

        bluetoothAdapter.bluetoothLeAdvertiser.startAdvertising(settings,data,advertiseCallback)
    }

    private fun stopAdvertise(){
        bluetoothAdapter.bluetoothLeAdvertiser.stopAdvertising(advertiseCallback)
    }

    private val listenerHandler = object : Handler(listenerThread.looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {//等待连接
                    if (bluetoothAdapter == null) {
                        Log.e(TAG, "handleMessage: defaultAdapter si null")
                        return
                    }


                }
                else -> {

                }
            }

        }
    }


    private val timer  = Timer()
    override fun onCreate() {
        super.onCreate()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    override fun onBind(intent: Intent): IBinder {
        listenerHandler.sendEmptyMessage(0)
        startAdvertise()
        return super.onBind(intent)
    }



    override fun onUnbind(intent: Intent?): Boolean {
        stopAdvertise()
        gattServer.close()
        return super.onUnbind(intent)
    }
}
