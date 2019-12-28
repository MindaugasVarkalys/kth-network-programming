package com.varkalys.pusher.repository;

import com.varkalys.pusher.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Integer> {

    Device findByAndroidId(String androidId);

}
