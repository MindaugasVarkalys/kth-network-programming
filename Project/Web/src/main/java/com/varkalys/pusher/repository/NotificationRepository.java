package com.varkalys.pusher.repository;

import com.varkalys.pusher.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

}
