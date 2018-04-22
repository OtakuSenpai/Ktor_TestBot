package com.otakusenpai.testbot

import io.ktor.network.sockets.*
import io.ktor.network.sockets.Socket
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.io.*
import java.net.*

class BasicConnection() {

    constructor(Port: Int = 6667,Address: String): this() {
        runBlocking {
            //log("In BasicConn constructor!")
            try {
                port = Port
                address = Address
                socket = aSocket().tcp().connect(InetSocketAddress(address,port))
                input = socket.openReadChannel()
                output = socket.openWriteChannel(autoFlush = true)
                connected = true
            } catch(e: Throwable) {
                e.printStackTrace()
                socket.close()
                connected = false
            }
            //log("In BasicConn constructor,done constructing!")
        }
    }

    fun Connect() = runBlocking {
        //log("In BasicConn Connect!")
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
        //log("In BasicConn Connect, done connecting!")
    }

    fun sendData(data: String) = async {
        //log("In BasicConn sendData!")
        try {
            output.writeBytes(data)
        } catch (e: Throwable) {
            e.printStackTrace()
            socket.close()
            connected = false
        }
        //log("In BasicConn sendData, done sending!")
    }

    fun receiveData(): String? {
        //log("In BasicConn receiveData!")
        var data: String? = null
        val one = async(CommonPool) {
            data = try {
                var stringBuilder: StringBuilder = StringBuilder()
                //log("In BasicConn receiveData,taking input!")
                if(!input.readUTF8LineTo(stringBuilder)) {
                    log("In BasicConn receiveData, found error!")
                    throw Exception("BasicConnection.kt: Didn't receive data from connection!")
                }
                //log("In BasicConn receiveData,done!")
                stringBuilder.toString()
            } catch (e: Throwable) {
                e.printStackTrace()
                socket.close()
                connected = false
                null
            }
        }

        runBlocking {
            one.await()
        }
        //log("In BasicConn receiveData,exiting!")
        return data
    }

    fun Disconnect() {
        socket.close()
        connected = false
    }

    var connected: Boolean = false
    private lateinit var output : ByteWriteChannel
    private lateinit var input: ByteReadChannel
    private lateinit var socket: Socket
    private var port: Int = 6667
    private var address: String = ""
}