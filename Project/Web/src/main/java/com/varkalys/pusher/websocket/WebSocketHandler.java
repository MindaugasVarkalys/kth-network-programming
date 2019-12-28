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
    private final Map<Device, WebSocketSession> sessions = new HashMap<>();

    public WebSocketHandler(DeviceRepository deviceRepository, TransmissionRepository transmissionRepository) {
        this.deviceRepository = deviceRepository;
        this.transmissionRepository = transmissionRepository;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        DeviceMessage deviceMessage = gson.fromJson(message.getPayload(), DeviceMessage.class);
        Device device = getOrCreateDevice(deviceMessage);
        sessions.put(device, session);
        sendPendingDeviceNotifications(device);

        Notification n = new Notification();
        n.setContent("Not");
        n.setTitle("TTTT");
        Transmission t = new Transmission();
        t.setNotification(n);
        t.setDevice(device);
        try {
            transmitNotification(t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Device getOrCreateDevice(DeviceMessage deviceMessage) {
        Device device = deviceRepository.findByAndroidId(deviceMessage.getAndroidId());
        if (device == null) {
            device = new Device();
            device.setAndroidId(deviceMessage.getAndroidId());
            deviceRepository.save(device);
        }
        device.setModel(deviceMessage.getModel());
        device.setName(deviceMessage.getName());
        device.setAppPackage(deviceMessage.getAppPackage());
        device.setActive(true);
        deviceRepository.flush();
        return device;
    }

    private void sendPendingDeviceNotifications(Device device) {
        List<Transmission> transmissions = transmissionRepository.findByDevice(device);
        for (Transmission transmission : transmissions) {
            tryTransmitNotification(transmission);
        }
    }

    private void tryTransmitNotification(Transmission transmission) {
        try {
            transmitNotification(transmission);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void transmitNotification(Transmission transmission) throws IOException {
        NotificationMessage message = new NotificationMessage();
        message.setTitle(transmission.getNotification().getTitle());
        message.setContent(transmission.getNotification().getContent());
        String payload = gson.toJson(message);
        WebSocketSession session = sessions.get(transmission.getDevice());
        session.sendMessage(new TextMessage(payload));
        transmission.setSentAt(new Date());
        transmissionRepository.flush();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        for (Device device : sessions.keySet()) {
            if (sessions.get(device).equals(session)) {
                device.setActive(false);
                sessions.remove(device);
                deviceRepository.flush();
                return;
            }
        }
    }
}
