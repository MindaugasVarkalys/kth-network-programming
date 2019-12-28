package com.varkalys.pusherapp

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.tinder.streamadapter.coroutines.CoroutinesStreamAdapterFactory
import com.varkalys.pusherapp.api.PusherService
import com.varkalys.pusherapp.api.message.DeviceMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {

    @UseExperimental(ObsoleteCoroutinesApi::class)
    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scarletInstance = Scarlet.Builder()
            .webSocketFactory(OkHttpClient.Builder().build().newWebSocketFactory("ws://192.168.0.109:8080/ws"))
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
            .build()

        val pusherService = scarletInstance.create<PusherService>()

        GlobalScope.launch {
            pusherService.observeWebsocketEvent().consumeEach {
                if (it is WebSocket.Event.OnConnectionOpened<*>) {
                    pusherService.sendDeviceMessage(
                        DeviceMessage(
                            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID),
                            Build.MANUFACTURER,
                            Build.MODEL,
                            BuildConfig.APPLICATION_ID
                        )
                    )

                    pusherService.receiveNotificationAsync().consumeEach {
                        Log.e("NOTIFICATION", it.content)
                    }
                }
            }

        }
    }
}
