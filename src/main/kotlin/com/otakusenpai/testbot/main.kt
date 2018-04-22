package com.otakusenpai.testbot

import com.otakusenpai.testbot.BasicConnection
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.io.*

fun setConnection(conn: BasicConnection) = runBlocking {
    try {
        while(!conn.connected) {
            delay(6000L)
            conn.Connect()
        }
        conn.sendData("NICK NikoCat" + "\r\n")
        conn.sendData("USER " + "NikoCat" + " " + "0" + " * :" + "NikoCat" + "\r\n")
        true
    } catch(e: Throwable) {
        e.printStackTrace()
        false
    }
}

fun hasIt(data: String?, key: String): Boolean {
    var found = false
    if(data != null) {
        var dataList =  data.split("\\s{1,}".toRegex())

        for(i in dataList) {
            if(i == key) {
                found = true
                break
            }
        }
    } else throw Exception("Constants.kt: Can't check null strings!")
    return found
}

fun main(args: Array<String>) {
    try {
        val name = "KtorTestBot"
        val user = "KtorTestBot"
        val realname: String = "OtakuSenpai"
        val port: Int = 6667
        val address = "chat.freenode.net"
        val conn = BasicConnection(port,address)
        var data: String

        println("This bot will connect to chat.freenode.net, over the port 6667.")
        println("The name of the bot is KtorTestBot, and the channel it will join is ##bot-test")
        println("To see the bot in action, join ##bot-test(after connecting, type '/join ##bot-test'")
        println("This bot is intented to teach how to use the raw sockets of Ktor.")
        println("Made by Avra Neel aka OtakuSenpai, under public domain!!") 
        println("") 

        var running = setConnection(conn)

        while(running) {
            data = conn.receiveData() as String
            println(data)
            if(hasIt(data,"004")) {
                println("Connected...")
                var temp = "JOIN ##bot-test\r\n"
                conn.sendData(temp)
            }
            //PING :verne.freenode.net 
            if(hasIt(data,"PING")) {
                var temp = data
                var contents = temp.substring(temp.indexOf(" ")+1, temp.length)
                println("Sending ping: ${contents}")
                conn.sendData(temp)
            }
        }
    } catch(e: Throwable) {
        e.printStackTrace()
    }

}