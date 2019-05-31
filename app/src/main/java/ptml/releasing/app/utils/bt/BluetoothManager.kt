package ptml.releasing.app.utils.bt

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothManager(var activity: Activity) {
    var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    var bluetoothDevice: BluetoothDevice? = null
    var bluetoothDeviceList = mutableMapOf<String, BluetoothDevice>()
    var bondedDeviceList = mutableListOf<BluetoothDevice>()
    var bluetoothNameList = mutableListOf<String>()
    var bondedDeviceNameList = mutableListOf<String>()

    var socket: BluetoothSocket? = null
    var inputStream: InputStream? = null
    var outputStream: OutputStream? = null
    var listener: Listener? = null
    var adapterListener: AdapterListener? = null

    @Volatile
    var connectionState = false
    @Volatile
    var writeState = false
    @Volatile
    var outString = ""

    init {
        if(bluetoothAdapter == null){
            adapterListener?.onAdapterError()
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == BluetoothDevice.ACTION_FOUND) {
                val blueTemp = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                bluetoothDeviceList[blueTemp.address]= blueTemp
                bluetoothNameList.add(blueTemp.name + "\n" + blueTemp.address)
                Timber.d("Adding device: %s", blueTemp.name)
            } else if (intent.action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                listener?.onDiscoveryComplete()
                Timber.d("Discovery complete")
            }
        }
    }

    /* init {
         if (enable) {
             if (!bluetoothAdapter.isEnabled) {
                 val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                 activity.startActivity(intent)
             }
         }
         activity.registerReceiver(broadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))

         val bondedDevice = bluetoothAdapter.bondedDevices
         for (device in bondedDevice) {
             bondedDeviceList.add(device)
             bondedDeviceNameList.add(device.name + "\n" + device.address)
         }
     }*/

    fun unRegisterReceiver() {
        Timber.d("Unregistering receiver")
        activity.unregisterReceiver(broadcastReceiver)
    }

    fun registerReceiver() {
        Timber.d("Registering receiver")
        activity.registerReceiver(
            broadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        )

        activity.registerReceiver(
            broadcastReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )


    }

    fun pairDevice(device: BluetoothDevice){

    }

    fun enable(enable: Boolean) {
        if (enable) {
            bluetoothAdapter?.enable()
            /*if (!bluetoothAdapter.isEnabled) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                activity.startActivity(intent)
            }*/
            activity.registerReceiver(
                broadcastReceiver,
                IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            )
            //activity.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        } else {
            bluetoothAdapter?.disable()
        }
    }

    fun startDiscovery() {
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery();
        }
        bluetoothAdapter?.startDiscovery()
    }

    fun stopDiscovery() {
        bluetoothAdapter?.cancelDiscovery()
    }

    fun setDiscoverable() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        activity.startActivity(intent)
    }

    fun createServerSocket() {
        try {
            val serverSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "bluetoothServer",
                uuid
            )
            Toast.makeText(activity, "READY to connect to other device", Toast.LENGTH_LONG).show()
            Thread(Runnable {
                try {
                    socket = serverSocket?.accept()
                    Timber.d("SERVER SOCKET", "Server socket accepted")
                    listenForMessage()
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                }
            }).start()
        } catch (ioe: IOException) {
            Toast.makeText(
                activity,
                "NOT READY to connect to other devices. \n try RESTARTING app",
                Toast.LENGTH_LONG
            ).show()
            ioe.printStackTrace()
        }

    }

    fun createClientSocket(device: BluetoothDevice) {
        bluetoothDevice = device
        //new Thread(new Runnable()
        run {
            //@Override
            //public void run() {
            try {
                socket = bluetoothDevice?.createInsecureRfcommSocketToServiceRecord(uuid)
                socket?.connect()
                connectionState = true
                Timber.d("Device connected")
                listenForMessage()
            } catch (ioe: IOException) {
                Timber.e(ioe)
                Timber.d("Trying fallback...")
                try {
                    socket = bluetoothDevice?.javaClass?.getMethod("createRfcommSocket", Int::class.java)?.invoke(
                        bluetoothDevice,
                        1
                    ) as BluetoothSocket
                    socket?.connect()
                    connectionState = true
                    Timber.d("CONNECTED", "Device Connected")
                    listenForMessage()
                } catch (e: Exception) {
                    connectionState = false
                    Timber.e("BLUETOOTH CONNECT", "error connecting")
                }

            }
        }
        //}).start();
    }

    fun listenForMessage() {
        try {
            inputStream = socket?.inputStream
            outputStream = socket?.outputStream

            Thread(Runnable {
                val buffer = ByteArray(1024)
                try {
                    while (bluetoothAdapter?.isEnabled ==true) {
                        val bytes = inputStream?.read(buffer)
                        outString += String(buffer, 0, 1024)
                    }
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                }
            }).start()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }

    }

    fun write(ch: Char) {
        if (outputStream != null) {
            try {
                outputStream!!.write(ch.toInt())
                outputStream!!.flush()
                writeState = true
            } catch (ioe: IOException) {
                ioe.printStackTrace()
                writeState = false
            }

        }
    }

    fun write(`in`: Int) {
        if (outputStream != null) {
            try {
                outputStream!!.write(`in`)
                outputStream!!.flush()
                writeState = true
            } catch (ioe: IOException) {
                ioe.printStackTrace()
                writeState = false
            }

        }
    }

    fun write(str: String) {
        for (cn in 0 until str.length) {
            write(str[cn])
        }
    }

    fun writeBytes(str: String) {
        run {
            try {
                write('1')
                Thread.sleep(1000)
            } catch (e: InterruptedException) {

            }

            for (cn in 0 until str.length) {
                if (outputStream != null) {
                    try {
                        outputStream!!.write(change(str[cn]).toInt())
                        outputStream!!.flush()
                        writeState = true
                    } catch (ioe: IOException) {
                        ioe.printStackTrace()
                        writeState = false
                    }

                }
            }
        }
    }

    fun provideBondedDeviceList(): List<BluetoothDevice> {
        val bondedDevice = bluetoothAdapter?.bondedDevices
        bondedDeviceList.clear()
        for (device in bondedDevice ?: mutableSetOf()) {
            bondedDeviceList.add(device)
            bondedDeviceNameList.add(device.name + "\n" + device.address)
        }
        return bondedDeviceList
    }


    fun closeSocket() {
        try {
            socket?.close()
        } catch (ioe: IOException) {
            Timber.e(ioe)
        }

    }

    protected fun change(inputChar: Char): Byte {
        when (inputChar) {
            'A' -> return 1
            'B' -> return 2
            'C' -> return 3
            'D' -> return 4
            'E' -> return 5
            'F' -> return 6
            'G' -> return 7
            'H' -> return 8
            'I' -> return 9
            'J' -> return 10
            'K' -> return 11
            'L' -> return 12
            'M' -> return 13
            'N' -> return 14
            'O' -> return 15
            'P' -> return 16
            'Q' -> return 17
            'R' -> return 18
            'S' -> return 19
            'T' -> return 20
            'U' -> return 21
            'V' -> return 22
            'W' -> return 23
            'X' -> return 24
            'Y' -> return 25
            'Z' -> return 26
            '1' -> return 27
            '2' -> return 28
            '3' -> return 29
            '4' -> return 30
            '5' -> return 31
            '6' -> return 32
            '7' -> return 33
            '8' -> return 34
            '9' -> return 35
            '0' -> return 36
            ' ' -> return 37
            else -> return 100
        }
    }

    fun disableBluetooth() {
        if (bluetoothAdapter?.isEnabled == true) {
            bluetoothAdapter?.disable()
        }
    }

    fun enableBluetooth() {
        if (bluetoothAdapter?.isEnabled == false) {
            bluetoothAdapter?.enable()
        }
    }

    companion object {
        var uuid = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB")


        @JvmStatic
        fun createSocketForClient(device: BluetoothDevice): Boolean {
            var socket: BluetoothSocket

            //new Thread(new Runnable()
            run {
                //@Override
                //public void run() {
                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid)
                    socket.connect()
                    Timber.d("Device connected")
                    return true

                } catch (ioe: IOException) {
                    Timber.e(ioe)
                    Timber.d("Trying fallback")
                    try {
                        socket = device.javaClass.getMethod(
                            "createRfcommSocket",
                            Int::class.java
                        ).invoke(device, 1) as BluetoothSocket
                        socket.connect()
                        Timber.d("Device Connected")
                        return true

                    } catch (e: Exception) {
                        Timber.e(e, "error connecting")
                        return false
                    }

                }
            }
        }
    }

    interface Listener {
        fun onDiscoveryComplete()
    }

    interface AdapterListener{
        fun onAdapterError()
    }
}


