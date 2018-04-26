package com.otakusenpai.testbot.connection

import com.otakusenpai.testbot.connection.Connection
import io.ktor.network.sockets.*
import io.ktor.network.sockets.Socket
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.io.*
import java.net.*

class BasicConnection: Connection {

    constructor(Port: Int = 6667,Address: String): super() {
        runBlocking {
            try {
                port = Port
                address = Address
                socket = aSocket().tcp().connect(InetSocketAddress(address, port))
                input = socket.openReadChannel()
                output = socket.openWriteChannel(autoFlush = true)
                connected = true
            } catch (e: Throwable) {
                e.printStackTrace()
                socket.close()
                connected = false
            }
        }
    }

    override fun Connect() = runBlocking {
        if(!connected) {
            try {
                socket = aSocket().tcp().connect(InetSocketAddress(address,port))
                input = socket.openReadChannel()
                output = socket.openWriteChannel(autoFlush = true)
                connected = true
            } catch(e: Throwable) {
                e.printStackTrace()
                socket.close()
                connected = false
            }
        }
    }

    override fun sendDataAsync(data: String) = async {
        try {
            output.writeBytes(data)
            println("Sending: ${data}")
        } catch (e: Throwable) {
            e.printStackTrace()
            socket.close()
            connected = false
        }
    }

    override fun sendData(data: String) {
        try {
            runBlocking {
                output.writeBytes(data)
                println("Sending: ${data}")
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            socket.close()
            connected = false
        }
    }

    override fun receiveData(): String? = runBlocking {
        var data: String? = null
        try {
            data = input.readASCIILine()
            if(data == null) {
                throw Exception("BasicConnection.kt: Didn't receive data from connection!")
            }
        } catch(e: Throwable) {
            e.printStackTrace()
        }
        data
    }

    override fun receiveUTF8Data(): String? = runBlocking{
        var data: String? = null

        try {
            //log("In BasicConn receiveData,taking input!")
            data = input.readUTF8Line()
            println("Receiving: ${data}")
            if(data == null) {
                throw Exception("BasicConnection.kt: Didn't receive data from connection!")
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            socket.close()
            connected = false
            null
        }
        data
    }
}