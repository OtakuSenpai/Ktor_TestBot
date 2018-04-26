package com.otakusenpai.testbot

import com.otakusenpai.testbot.connection.*
import com.otakusenpai.testbot.connection.BasicConnection
import kotlinx.coroutines.experimental.*

fun setConnection(conn: Connection) = runBlocking {
    try {
        println("Connecting....")
        var i = 0
        while(!conn.connected) {
            ++i
            delay(6000L)
            println("Trying for the ${i}'th time... ")
            conn.Connect()
        }

        conn.sendData("NICK BamBaka" + "\r\n")
        conn.sendData("USER " + "BamBaka" + " " + "0" + " * :" + "BamBaka" + "\r\n")
        //conn.sendData("PRIVMSG NickServ :identify password\r\n")
        println("Connected!")
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
        val port: Int = 6667
        val address = "chat.freenode.net"
        val conn: Connection = BasicConnection(port,address)
        var data: String?
        var running = false
        println("This bot will connect to chat.freenode.net, over the port 6667.")
        println("The name of the bot is KtorTestBot, and the channel it will join is ##bot-test")
        println("To see the bot in action, join ##bot-test(after connecting, type '/join ##bot-test'")
        println("This bot is intented to teach how to use the raw sockets of Ktor.")
        println("Made by Avra Neel aka OtakuSenpai, under public domain!!")
        println("")

        val one = async(CommonPool) {
            running = setConnection(conn)
            delay(500L)
        }

        runBlocking { one.await() }

        println("Entering loop...")
        while(running) {
            data = conn.receiveData()
            println("Received data, printing...")
            println(data)
            if(data == null)
                throw Exception("BasicBot.kt: Null value received from connection!")
            if(hasIt(data,"004")) {
                println("Connected...")
            }
            if(hasIt(data,"433")) {
                conn.sendData("NICK BamBaka1" + "\r\n")
            }
            if(hasIt(data,"PING")) {
                var s = data.substring(data.indexOf("PING"),data.length)
                conn.sendData("PONG " + s + "\r\n")
            }
        }

        // if(!running && !disconnect) Connect()
        // else if(disconnect) conn.Disconnect()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}