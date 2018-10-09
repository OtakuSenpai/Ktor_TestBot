package com.github.otakusenpai.testbot.connection

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.*
import io.ktor.network.tls.*
import io.ktor.network.util.ioCoroutineDispatcher
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.io.*
import java.net.*
import java.nio.ByteBuffer
import java.nio.charset.Charset

class SslConnection: Connection {

    constructor(Port: Int = 6697,Address: String): super() {
        runBlocking {
            try {
                port = Port
                address = Address
                socket = aSocket(ActorSelectorManager(ioCoroutineDispatcher)).
                        tcp().connect(InetSocketAddress(address,port)).tls()
                input = socket.openReadChannel()
                output = socket.openWriteChannel(autoFlush = false)
                connected = true
            } catch(e: Throwable) {
                e.printStackTrace()
                socket.close()
                connected = false
            }
        }
    }

    override fun Connect() = runBlocking {
        if(!connected) {
            try {
                socket = aSocket(ActorSelectorManager(ioCoroutineDispatcher)).
                        tcp().connect(InetSocketAddress(address,port)).tls()
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
            output.writeByte(data.toByte())
        } catch (e: Throwable) {
            e.printStackTrace()
            socket.close()
            connected = false
        }
    }

    override fun sendData(data: String) {
        try {
            runBlocking {
                output.writeByte(data.toByte())
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
            // 512 here is the MAX size of a IRC message
            var byteBuffer = ByteBuffer.wrap(ByteArray(512))
            input.read { byteBuffer }
            data = String(byteBuffer.array(), Charset.forName("ASCII"))
            if(data == null) {
                throw Exception("BasicConnection.kt: Didn't receive data from connection!")
            }
        } catch(e: Throwable) {
            e.printStackTrace()
        }
        data
    }

    override fun receiveUTF8Data(): String? = runBlocking {
        var data: String? = null

        try {
            data = input.readUTF8Line(512)
            println("Receiving: ${data}")
            if (data == null) {
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