package com.github.otakusenpai.testbot.connection

import io.ktor.network.sockets.Socket
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.io.ByteReadChannel
import kotlinx.coroutines.experimental.io.ByteWriteChannel

abstract class Connection() {

    abstract fun Connect()

    abstract fun sendDataAsync(data: String): Deferred<Unit>
    abstract fun sendData(data: String)
    abstract fun receiveData(): String?
    abstract fun receiveUTF8Data(): String?

    fun Disconnect() {
        socket.close()
        connected = false
    }

    open var connected: Boolean = false
    open lateinit var output : ByteWriteChannel
    open lateinit var input: ByteReadChannel
    open lateinit var socket: Socket
    open var port: Int = 6667
    open var address: String = ""
}