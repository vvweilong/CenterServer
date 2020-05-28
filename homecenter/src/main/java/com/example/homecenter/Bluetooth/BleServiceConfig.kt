package com.example.homecenter.Bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import java.util.*

/**
 * 蓝牙配置
 * 默认配置 ble蓝牙服务
 * 分为1、订阅服务 接收服务器实时数据改变
 * 2、单独的读数据服务
 * 3、单独的写数据服务
 *
 * 由于ble 数据报的长度显示20byte  需要对较长的数据内容进行分包处理
 * */
object BleServiceConfig {
    //周期性通知服务
    const val NOTIFI_SERVICE_UUID = "ae950417-5175-4395-9db5-5ba5cf083050"
    const val NOTIFI_CHARACTERISTIC_UUID = "ae950417-5175-4395-9db5-5ba5cf083050"
    //命令读写服务
    const val CMD_SERVICE_UUID = "ae950417-5175-4395-9db5-5ba5cf083050"
    const val CMD_READ_CHARACTERISTIC_UUID="ae950417-5175-4395-9db5-5ba5cf083050"
    const val CMD_WRITE_CHARACTERISTIC_UUID="ae950417-5175-4395-9db5-5ba5cf083050"


    fun getServices():List<BluetoothGattService>{
        val notifiService = BluetoothGattService(
            UUID.fromString(NOTIFI_SERVICE_UUID),
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        val notifiCharacteristic = BluetoothGattCharacteristic(
            UUID.fromString(NOTIFI_CHARACTERISTIC_UUID),
            BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )

        notifiService.addCharacteristic(notifiCharacteristic)

        val cmdService = BluetoothGattService(
            UUID.fromString(CMD_SERVICE_UUID),
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        val cmdReadCharacteristic = BluetoothGattCharacteristic(
            UUID.fromString(CMD_READ_CHARACTERISTIC_UUID),
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        val cmdWriteCharacteristic = BluetoothGattCharacteristic(
            UUID.fromString(CMD_WRITE_CHARACTERISTIC_UUID),
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        cmdService.addCharacteristic(cmdReadCharacteristic)
        cmdService.addCharacteristic(cmdWriteCharacteristic)

        return listOf<BluetoothGattService>(notifiService,cmdService)
    }


}