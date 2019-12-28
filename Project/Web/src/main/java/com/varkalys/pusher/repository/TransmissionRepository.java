package com.varkalys.pusher.repository;

import com.varkalys.pusher.entity.Device;
import com.varkalys.pusher.entity.Transmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransmissionRepository extends JpaRepository<Transmission, Integer> {

    List<Transmission> findByDevice(Device device);

}
