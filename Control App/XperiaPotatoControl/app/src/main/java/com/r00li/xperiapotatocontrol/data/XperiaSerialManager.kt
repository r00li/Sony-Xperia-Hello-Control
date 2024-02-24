package com.r00li.xperiapotatocontrol.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import tp.xmaihh.serialport.SerialHelper
import tp.xmaihh.serialport.bean.ComBean
import tp.xmaihh.serialport.stick.SpecifiedStickPackageHelper
import java.util.*

class XperiaSerialManager {

    private var connectedPort: SerialHelper? = null
    //private var helloPort: SerialPort? = null

    // TODO Should probably be private
    var tiltPosition: Short = 0; private set
    var panPosition: Short = 0; private set
    var bodyPosition: Short = 0; private set
    var letfBitset = BitSet(5); private set
    var rightBitSet = BitSet(5); private set

    fun connect() = callbackFlow {
        // Time to disable SELinux
        execSUCommand("setenforce 0")

        // Needs a bit of time before we can continue
        delay(2000)

        if (connectedPort?.isOpen == true) {
            println("Port is already open!")
            trySend(Result.Opened)
        }

        connectedPort = object : SerialHelper("/dev/ttyHS1", 115200) {
            override fun onDataReceived(comBean: ComBean) {
                trySend(Result.DataReceived(String(comBean.bRec)))
            }
        }
        connectedPort?.stickPackageHelper = SpecifiedStickPackageHelper(
            ":".toByteArray(Charsets.UTF_8),
            "\r\n".toByteArray(Charsets.UTF_8)
        )
        connectedPort?.open()

        zeroMotors()
        updateEyeLeds(BitSet(5), BitSet(5))
        updateNeckColor("000000")

        trySend(Result.Opened)

        // keep opened to receive data
        awaitClose { }

        //TestLoader()

        //try {
        /*val port = SerialPort.getCommPort("/dev/ttyHS1")
        port.setComPortParameters(115200, 8, 1, 0)
        port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0)
        if (port.openPort()) {
            connectionState.value = true
            helloPort = port
            port.addDataListener(object : SerialPortDataListener {
                override fun getListeningEvents(): Int {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE
                }

                override fun serialEvent(event: SerialPortEvent) {
                    if (event.eventType != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return
                    val newData = ByteArray(port.bytesAvailable())
                    val numRead: Int = port.readBytes(newData, newData.size.toLong())

                    val readString = String(newData)
                    println("Received: $readString")
                }
            })
            println("Connection established to \"${port.descriptivePortName}\"")
            //val toSend = ":LED2000000\\r\\n".toByteArray(Charsets.UTF_8)
            //port.outputStream.write(toSend)
        } else {
            connectionState.value = false
            println("Connection failed")
        }*/
        //} catch (e: Exception) {
        //    throw e.fillInStackTrace()
        //}
    }

    suspend fun zeroMotors() {
        updateTiltPosition(0)
        updatePanPosition(0)
        updateRotationPosition(0)
    }

    suspend fun updateTiltPosition(newPosition: Short) {
        tiltPosition = newPosition
        updateMotorPosition(newPosition, "0")
    }

    suspend fun updatePanPosition(newPosition: Short) {
        panPosition = newPosition
        updateMotorPosition(newPosition, "1")
    }

    suspend fun updateRotationPosition(newPosition: Short) {
        bodyPosition = newPosition
        updateMotorPosition(newPosition, "2")
    }

    private suspend fun updateMotorPosition(
        newPosition: Short,
        motor: String,
        speed: String = "1F40"
    ) = withContext(Dispatchers.IO) {
        // MOTOR: 0: Tilt, 1: Pan, 2: Body
        println(newPosition)

        val sign = if (newPosition >= 0) "0" else "F"
        val toSendString = ":P2P" + motor + sign + "%04X".format(newPosition) + speed + "\r\n"
        println("Sending: $toSendString")
        val toSend = toSendString.toByteArray(Charsets.UTF_8)

        //helloPort?.outputStream?.write(toSend)
        connectedPort?.send(toSend)
    }

    suspend fun sendData(data: ByteArray) = withContext(Dispatchers.IO) {
        connectedPort?.send(data)
    }

    suspend fun updateEyeLeds(leftBitSet: BitSet, rightBitSet: BitSet) = withContext(Dispatchers.IO) {
        val leftValue = "%02X".format(leftBitSet.toByteArray().getOrElse(0, { 0.toByte() }))
        val rightValue = "%02X".format(rightBitSet.toByteArray().getOrElse(0, { 0.toByte() }))

        val toSendString = ":LED4" + leftValue + rightValue + "\r\n"
        val toSend = toSendString.toByteArray(Charsets.UTF_8)
        connectedPort?.send(toSend)
    }

    suspend fun updateNeckColor(rgbColor: String) = withContext(Dispatchers.IO) {
        val toSendString = ":LED2" + rgbColor + "\r\n"
        val toSend = toSendString.toByteArray(Charsets.UTF_8)
        connectedPort?.send(toSend)
    }

    /*fun doThread() {
        val mainLooper = Looper.getMainLooper()

        GlobalScope.launch {
            while (sendingEnabled.value == true) {
                Thread.sleep(2000)
                var toSend = ":MPS00".toByteArray(Charsets.UTF_8)

                toSend = toSend + "%04x".format(whatever).toByteArray(Charsets.UTF_8) + "\\r\\n".toByteArray(Charsets.UTF_8)
                whatever = (whatever + 50).toShort()

                Handler(mainLooper).post {
                    inputText.value = String(toSend)
                    helloPort?.outputStream?.write(toSend)
                }
            }
        }
    }*/

    var process: Process? = null

    private fun execSUCommand(command: String): Boolean {
        return try {
            if (process == null || process!!.outputStream == null) {
                process = ProcessBuilder().command("su").start()
            }

            process!!.outputStream.write("""$command""".toByteArray(charset("ASCII")))
            process!!.outputStream.flush()

            Thread.sleep(100)
            true
        } catch (e: Exception) {
            false
        }
    }

    sealed interface Result {
        object Opened : Result
        data class DataReceived(val data: String) : Result
    }
}
