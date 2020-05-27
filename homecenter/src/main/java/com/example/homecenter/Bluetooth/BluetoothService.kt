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
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * 通过蓝牙链接 的服务功能
 * */
class BluetoothService : BaseRemoteService() {
    val TAG = "BluetoothService"
    val bleConnections = LinkedHashMap<String, BluetoothGatt>()

    private val listenerThread = HandlerThread("socketListener").apply { start() }

    val defaultAdapter = BluetoothAdapter.getDefaultAdapter()

    val bluetoothGattCallback = object :BluetoothGattCallback(){
        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            Log.i("BluetoothService","onReadRemoteRssi")
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Log.i("BluetoothService","onServicesDiscovered")
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Log.i("BluetoothService","onConnectionStateChange")
        }
    }

    val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            Log.i("BluetoothService", "onConnectionStateChange ${device?.uuids} $status  $newState")
            when (newState) {
                2 -> {//变为连接状态
                    stopAdvertising()
                    Log.i("BluetoothService", "onConnectionStateChange device $device")
//                    if (device != null) {
//
//                        val connectGatt =
//                            device.connectGatt(this@BluetoothService, true, bluetoothGattCallback)
//                        bleConnections.put(device.address,connectGatt)
//                    }


                }
                0 -> {//断开状态
                    startAdvertising()
                }
                else -> {
                }
            }
        }
    }


    //    val bluetoothGattServer = bluetoothManager.openGattServer(this,gattServerCallback)
    private val listenerHandler = object : Handler(listenerThread.looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {//等待连接
                    if (defaultAdapter == null) {
                        Log.e(TAG, "handleMessage: defaultAdapter si null")
                        return
                    }

                    initBluetoothAsServer()
                }
                else -> {

                }
            }

        }
    }

    private fun buildService():BluetoothGattService{
        val bluetoothGattService =
            BluetoothGattService(UUID.randomUUID(), BluetoothGattService.SERVICE_TYPE_PRIMARY)
        val characteristic = BluetoothGattCharacteristic(
            UUID.randomUUID().apply {
                Log.i("BluetoothService","buildService ${this.toString()}")
            },
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
//        val descriptor=BluetoothGattDescriptor(UUID.randomUUID(),)
//        characteristic.addDescriptor()
        bluetoothGattService.addCharacteristic(characteristic)
        return bluetoothGattService
    }

    val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            Log.i("BluetoothService", "onStartSuccess")
            val systemService: BluetoothManager =
                getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val gattServer =
                systemService.openGattServer(this@BluetoothService, gattServerCallback)
            gattServer.addService(buildService())
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Log.i("BluetoothService", "onStartFailure $errorCode")
        }
    }

    private fun initBluetoothAsServer() {
        if (!defaultAdapter.isEnabled) {
            defaultAdapter.enable()
        }
        startAdvertising()
    }

    private fun startAdvertising() {
        val bluetoothLeAdvertiser = defaultAdapter.bluetoothLeAdvertiser
        val builder = AdvertiseSettings.Builder()
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
        builder.setConnectable(true)
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
        val settings = builder.build()
        val dataBuilder = AdvertiseData.Builder()

//        val buildService = buildService()
//        dataBuilder.addServiceUuid(ParcelUuid(buildService.uuid))


        dataBuilder.setIncludeDeviceName(true)
        dataBuilder.setIncludeTxPowerLevel(true)
        val data = dataBuilder.build()


        bluetoothLeAdvertiser.startAdvertising(settings, data, advertiseCallback)
    }

    private fun stopAdvertising() {
        defaultAdapter.bluetoothLeAdvertiser.stopAdvertising(advertiseCallback)
    }


    override fun onBind(intent: Intent): IBinder {
        listenerHandler.sendEmptyMessage(0)
        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopAdvertising()
        for ((key,gatt) in bleConnections) {
            gatt.disconnect()
        }
        defaultAdapter.disable()
        return super.onUnbind(intent)
    }
}
