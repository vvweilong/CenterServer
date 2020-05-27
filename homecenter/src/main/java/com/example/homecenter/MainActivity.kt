package com.example.homecenter

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatToggleButton
import com.example.homecenter.Bluetooth.BleServiceConn
import com.example.homecenter.Bluetooth.BluetoothService
import com.example.homecenter.LoWPAN.LoWPANServiceConn
import com.example.homecenter.WIFI.SocketService
import com.example.homecenter.WIFI.SocketServiceCoon

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the PeripheralManager
 * For example, the snippet below will open a GPIO pin and set it to HIGH:
 *
 * val manager = PeripheralManager.getInstance()
 * val gpio = manager.openGpio("BCM6").apply {
 *     setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * }
 * gpio.value = true
 *
 * You can find additional examples on GitHub: https://github.com/androidthings
 */
class MainActivity : AppCompatActivity() {
    //蓝牙服务
    val bleService = BleServiceConn()
    //socket 服务
    val socketService=SocketServiceCoon()
    //lowpan 服务
    val loWPANService= LoWPANServiceConn()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<AppCompatToggleButton>(R.id.ble_toggle).setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                BluetoothAdapter.getDefaultAdapter().enable()
            }else{
                BluetoothAdapter.getDefaultAdapter().disable()
            }

        }

        bindServices()
    }

    private fun bindServices(){
        //绑定蓝牙服务
        val bleIntent = Intent(this,BluetoothService::class.java)
        bindService(bleIntent,bleService,Service.BIND_AUTO_CREATE)
        // socket服务
        val socketIntent = Intent(this,SocketService::class.java)
        bindService(socketIntent,socketService,Service.BIND_AUTO_CREATE)
        //
    }
    private fun unbindServices(){
        //解绑蓝牙服务
        unbindService(bleService)
        unbindService(socketService)

    }

    override fun onDestroy() {
        unbindServices()
        super.onDestroy()
    }
}
