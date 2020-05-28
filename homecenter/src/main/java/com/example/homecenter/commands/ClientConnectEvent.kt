package com.example.homecenter.commands

import com.example.homecenter.base.BaseCommand
import org.json.JSONObject

/**
 * @param mac 接入的设备 mac
 * */
data class ClientConnectEvent(val mac:String)
