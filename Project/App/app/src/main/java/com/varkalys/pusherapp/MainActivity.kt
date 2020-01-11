package com.varkalys.pusherapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.tinder.streamadapter.coroutines.CoroutinesStreamAdapterFactory
import com.varkalys.pusherapp.api.PusherService
import com.varkalys.pusherapp.api.message.DeviceMessage
import com.varkalys.pusherapp.api.message.NotificationMessage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    @UseExperimental(ObsoleteCoroutinesApi::class)
    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scarletInstance = Scarlet.Builder()
            .webSocketFactory(OkHttpClient.Builder().build().newWebSocketFactory("wss://pusher.varkalys.com/ws"))
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
            .build()

        val pusherService = scarletInstance.create<PusherService>()

        GlobalScope.launch {
            pusherService.observeWebsocketEvent().consumeEach {
                if (it is WebSocket.Event.OnConnectionOpened<*>) {
                    runOnUiThread {
                        textView.text = "Connected"
                    }
                    pusherService.sendDeviceMessage(
                        DeviceMessage(
                            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID),
                            Build.MANUFACTURER,
                            Build.MODEL,
                            BuildConfig.APPLICATION_ID
                        )
                    )

                    pusherService.receiveNotificationAsync().consumeEach { message ->
                        createNotificationChannel()
                        showNotification(message)
                    }
                }
            }
        }
    }

    private fun showNotification(message: NotificationMessage) {
        val notification = NotificationCompat.Builder(this@MainActivity, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(message.title)
            .setContentText(message.content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(this@MainActivity).notify(Random.nextInt(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Push notifications", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "default"
    }
}
