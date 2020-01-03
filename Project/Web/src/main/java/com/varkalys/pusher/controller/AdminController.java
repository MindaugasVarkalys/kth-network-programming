package com.varkalys.pusher.controller;

import com.varkalys.pusher.entity.Device;
import com.varkalys.pusher.entity.Notification;
import com.varkalys.pusher.entity.Transmission;
import com.varkalys.pusher.repository.DeviceRepository;
import com.varkalys.pusher.repository.NotificationRepository;
import com.varkalys.pusher.repository.TransmissionRepository;
import com.varkalys.pusher.websocket.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
public class AdminController {

    private final DeviceRepository deviceRepository;
    private final TransmissionRepository transmissionRepository;
    private final NotificationRepository notificationRepository;
    private final WebSocketHandler webSocketHandler;

    @Autowired
    public AdminController(DeviceRepository deviceRepository, TransmissionRepository transmissionRepository, NotificationRepository notificationRepository, WebSocketHandler webSocketHandler) {
        this.deviceRepository = deviceRepository;
        this.transmissionRepository = transmissionRepository;
        this.notificationRepository = notificationRepository;
        this.webSocketHandler = webSocketHandler;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        List<Device> devices = deviceRepository.findAllOrderedByActiveDesc();
        model.addAttribute("devices", devices);
        return "mainPage";
    }

    @PostMapping("/")
    public String sendNewNotification(@RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("deviceIds") int[] deviceIds) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setCreatedAt(new Date());
        notificationRepository.saveAndFlush(notification);
        for (int deviceId : deviceIds) {
            Transmission transmission = new Transmission();
            transmission.setDevice(deviceRepository.findById(deviceId).orElseThrow(() -> new RuntimeException("Device not found")));
            transmission.setNotification(notification);
            transmission = transmissionRepository.save(transmission);
            webSocketHandler.tryTransmitNotification(transmission);
        }
        transmissionRepository.flush();
        return "redirect:/";
    }
}
