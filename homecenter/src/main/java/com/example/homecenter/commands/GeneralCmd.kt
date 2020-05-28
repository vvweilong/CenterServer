package com.example.homecenter.commands

import org.json.JSONObject


data class GeneralCmd<T>(val cid:String,val ac:String,val data:T){
    override fun toString(): String {
        val jsonObject = JSONObject()
        jsonObject.put("cmdId",cid)
        jsonObject.put("action",ac)
        jsonObject.put("data",data.toString())
        return jsonObject.toString()
    }
}