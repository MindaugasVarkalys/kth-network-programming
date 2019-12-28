package com.varkalys.pusher.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceMessage {

    private String androidId;
    private String model;
    private String name;
    private String appPackage;
}
