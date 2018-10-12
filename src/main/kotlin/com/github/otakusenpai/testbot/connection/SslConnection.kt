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
import java.nio.charset.StandardCharsets

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
            output.write { ByteBuffer.wrap(data.toByteArray()) }
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
                output.write { ByteBuffer.wrap(data.toByteArray()) }
                println("Sending: ${data}")
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            socket.close()
            connected = false
        }
    }

    @Deprecated("This function us deprecated, use receiveUTF8Data(): String?")
    override fun receiveData(): String? = runBlocking {
        var data: String? = null
        try {
            // 512 here is the MAX size of a IRC message
            val bbToString = { bb: ByteBuffer ->
                try {
                    var decoder = StandardCharsets.US_ASCII.newDecoder()
                    var charBuffer = decoder.decode(bb)
                    data = charBuffer.toString()
                } catch(e: Throwable) {
                    throw e
                }
            }
            input.read{ byteBuffer: ByteBuffer -> bbToString(byteBuffer) }
            if(data == null) {
                throw Exception("SslConnection.kt: Didn't receive data from connection!")
            }
            println("Data size = ${data?.length}")
        } catch(e: Throwable) {
            e.printStackTrace()
        }
        data
    }

    override fun receiveUTF8Data(): String? = runBlocking {
        var data: String? = null

        try {
            data = input.readUTF8Line(512)
            if (data == null)
                throw Exception("SslConnection.kt: Didn't receive data from connection!")
        } catch (e: Throwable) {
            e.printStackTrace()
            socket.close()
            connected = false
            null
        }
        data
    }
}