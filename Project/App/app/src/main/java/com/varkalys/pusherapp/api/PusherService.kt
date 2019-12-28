package com.varkalys.pusherapp.api

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import com.varkalys.pusherapp.api.message.DeviceMessage
import com.varkalys.pusherapp.api.message.NotificationMessage
import kotlinx.coroutines.channels.ReceiveChannel

interface PusherService {

    @Receive
    fun observeWebsocketEvent(): ReceiveChannel<WebSocket.Event>

    @Receive
    fun receiveNotificationAsync(): ReceiveChannel<NotificationMessage>

    @Send
    fun sendDeviceMessage(deviceMessage: DeviceMessage)
}