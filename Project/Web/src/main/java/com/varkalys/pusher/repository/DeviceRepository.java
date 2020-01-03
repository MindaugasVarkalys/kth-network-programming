package com.varkalys.pusher.repository;

import com.varkalys.pusher.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Integer> {

    Device findByAndroidId(String androidId);

    @Query("select d from Device d order by d.isActive desc")
    List<Device> findAllOrderedByActiveDesc();
}
