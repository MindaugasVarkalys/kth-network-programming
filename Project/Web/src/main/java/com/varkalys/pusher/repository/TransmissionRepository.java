package com.varkalys.pusher.repository;

import com.varkalys.pusher.entity.Device;
import com.varkalys.pusher.entity.Transmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransmissionRepository extends JpaRepository<Transmission, Integer> {

    @Query("select t from Transmission t where t.sentAt is null and t.device.id = ?1")
    List<Transmission> findNotSentByDevice(int deviceId);

}
