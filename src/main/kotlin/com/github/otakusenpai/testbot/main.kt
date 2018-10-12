package com.github.otakusenpai.testbot

import com.github.otakusenpai.testbot.connection.BasicConnection
import com.github.otakusenpai.testbot.connection.Connection
import com.github.otakusenpai.testbot.connection.SslConnection
import kotlinx.coroutines.experimental.*
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

        conn.sendData("NICK KtorBot" + "\r\n")
        conn.sendData("USER " + "KtorBot" + " " + "0" + " * :" + "KtorBot" + "\r\n")
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

fun main(args: Array<String>) = runBlocking {
    try {
        println("This is a demo of Raw Sockets using Ktor." +
                "\nIn this demo we connect to any IRC server using both SSL and PLAIN connection." +
                "\nThis is a demo only, feel free to use the code as you wish.")
        println("Made by Avra Neel aka OtakuSenpai, under public domain!!")
        println("")

        var address = ""                           // The address to connect to
        var data: String? = null                   // The data input from the server
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

        val one = GlobalScope.async {
            running = setConnection(conn)
        }

        runBlocking { one.await() }

        println("Entering loop...")
        while(running) {

            val job = GlobalScope.async { data = conn.receiveUTF8Data() }
            job.await()
            println("Received data: $data")
            if(data == null)
                throw Exception("BasicBot.kt: Null value received from connection!")

            if(hasIt(data,"004")) {
                println("Joining channel...")
                conn.sendData("JOIN ##Ktor")
            }
            if(hasIt(data,"433")) {
                conn.sendData("NICK GuestUser687328" + "\r\n")
            }
            if(hasIt(data,"PING")) {
                var s = data?.substring(data?.indexOf("PING") as Int,data?.length as Int)
                conn.sendData("PONG " + s + "\r\n")
            }
        }

        // if(!running && !disconnect) Connect()
        // else if(disconnect) conn.Disconnect()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}