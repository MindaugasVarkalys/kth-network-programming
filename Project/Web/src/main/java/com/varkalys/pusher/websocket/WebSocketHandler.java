package com.varkalys.pusher.websocket;

import com.google.gson.Gson;
import com.varkalys.pusher.entity.Device;
import com.varkalys.pusher.entity.Notification;
import com.varkalys.pusher.entity.Transmission;
import com.varkalys.pusher.message.DeviceMessage;
import com.varkalys.pusher.message.NotificationMessage;
import com.varkalys.pusher.repository.DeviceRepository;
import com.varkalys.pusher.repository.TransmissionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Gson gson = new Gson();
    private final DeviceRepository deviceRepository;
    private final TransmissionRepository transmissionRepository;
    private final Map<String, WebSocketSession> sessions = new HashMap<>();

    public WebSocketHandler(DeviceRepository deviceRepository, TransmissionRepository transmissionRepository) {
        this.deviceRepository = deviceRepository;
        this.transmissionRepository = transmissionRepository;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        DeviceMessage deviceMessage = gson.fromJson(message.getPayload(), DeviceMessage.class);
        Device device = getOrCreateDevice(deviceMessage);
        sessions.put(device.getAndroidId(), session);
        sendPendingDeviceNotifications(device);
    }

    private Device getOrCreateDevice(DeviceMessage deviceMessage) {
        Device device = deviceRepository.findByAndroidId(deviceMessage.getAndroidId());
        if (device == null) {
            device = new Device();
            device.setAndroidId(deviceMessage.getAndroidId());
        }
        device.setModel(deviceMessage.getModel());
        device.setName(deviceMessage.getName());
        device.setAppPackage(deviceMessage.getAppPackage());
        device.setActive(true);
        device.setActiveAt(new Date());
        return deviceRepository.saveAndFlush(device);
    }

    private void sendPendingDeviceNotifications(Device device) {
        List<Transmission> transmissions = transmissionRepository.findNotSentByDevice(device.getId());
        for (Transmission transmission : transmissions) {
            tryTransmitNotification(transmission);
        }
    }

    public void tryTransmitNotification(Transmission transmission) {
        try {
            transmitNotification(transmission);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void transmitNotification(Transmission transmission) throws IOException {
        WebSocketSession session = sessions.get(transmission.getDevice().getAndroidId());
        if (session == null) {
            return;
        }
        NotificationMessage message = new NotificationMessage();
        message.setTitle(transmission.getNotification().getTitle());
        message.setContent(transmission.getNotification().getContent());
        String payload = gson.toJson(message);
        session.sendMessage(new TextMessage(payload));
        transmission.setSentAt(new Date());
        transmissionRepository.saveAndFlush(transmission);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        for (String deviceId : sessions.keySet()) {
            if (sessions.get(deviceId).equals(session)) {
                Device device = deviceRepository.findByAndroidId(deviceId);
                device.setActive(false);
                device.setActiveAt(new Date());
                sessions.remove(deviceId);
                deviceRepository.saveAndFlush(device);
                return;
            }
        }
    }
}
