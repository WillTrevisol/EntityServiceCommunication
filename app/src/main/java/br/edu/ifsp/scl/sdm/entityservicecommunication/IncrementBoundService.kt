package br.edu.ifsp.scl.sdm.entityservicecommunication

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log

class IncrementBoundService : Service() {

    private inner class IncrementBoundServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            msg.replyTo?.also {
                 clientMessenger = it
            }

            msg.data.getInt("VALUE", -1).also { value ->
                if (value != -1) {
                    Log.v(this.javaClass.simpleName, "Returning incremented value to client.")
                    clientMessenger.send(Message.obtain().apply { data.putInt("VALUE", value + 1)})
                }
            }
        }
    }

    private lateinit var incrementBoundServiceMessenger: Messenger
    private lateinit var incrementBoundServiceHandler: IncrementBoundServiceHandler
    private lateinit var clientMessenger: Messenger

    override fun onCreate() {
        super.onCreate()
        HandlerThread(this.javaClass.simpleName).apply {
            start()
            incrementBoundServiceHandler = IncrementBoundServiceHandler(looper)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.v(this.javaClass.simpleName, "Entity bound to the service.")
        incrementBoundServiceMessenger = Messenger(incrementBoundServiceHandler)
        return incrementBoundServiceMessenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.v(this.javaClass.simpleName, "Entity unbound to the service.")
        return super.onUnbind(intent)
    }
}
