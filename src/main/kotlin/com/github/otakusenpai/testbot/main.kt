package com.github.otakusenpai.testbot

import com.github.otakusenpai.testbot.connection.BasicConnection
import com.github.otakusenpai.testbot.connection.Connection
import com.github.otakusenpai.testbot.connection.SslConnection
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import java.util.*

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
        println("This is a demo of Raw Sockets using Ktor." +
                "\nIn this demo we connect to any IRC server using both SSL and PLAIN connection." +
                "\nThis is a demo only, feel free to use the code as you wish.")
        println("Made by Avra Neel aka OtakuSenpai, under public domain!!")
        println("")

        var address = ""                           // The address to connect to
        var data: String?                          // The data input from the server
        val sc = Scanner(System.`in`)              // for input
        var running = false                        // Used in bot's loop
        var choice = ""                            // User choice between ssl and plain
        lateinit var conn: Connection              // connection demo

        println("Enter the address: ")
        address = sc.nextLine()

        println("Enter connection mechanism:" +
                "\n1) Plain = plain" +
                "\n2) Ssl = ssl")
        choice = sc.nextLine()
        if(choice == "plain")
            conn = BasicConnection(6667,address)
        else if(choice == "ssl")
            conn = SslConnection(6697,address)
        else {
            println("Defaulted to PLAIN ...")
            conn = BasicConnection(6667,address)
        }

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