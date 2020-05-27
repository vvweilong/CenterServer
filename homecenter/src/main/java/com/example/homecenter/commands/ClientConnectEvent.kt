package com.example.homecenter.commands

import com.example.homecenter.base.BaseCommand

data class ClientConnectEvent<T>(val cid:String,val ac:String,val data:T):BaseCommand(cid, ac){
    override fun toString(): String {

        return super.toString()
    }
}