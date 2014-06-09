/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.wabe.salix.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.w3c.dom.Element;
import se.wabe.salix.exports.Event;
import se.wabe.salix.exports.OutputDevice;
import se.wabe.salix.exports.OutputDeviceClient;

/**
 *
 * @author Richard
 */
public class Alarm {
    private final String label;
    private final List<Day> days;
    private final List<OutputDeviceClient> services;    
    
    public Alarm(Element element, Map<String, OutputDevice> devices) {
        this.label = element.getAttribute("label");
        this.days = XPather.evaluate("when/day", element).map(e -> new Day(e)).collect(Collectors.toList());
        this.services = XPather.evaluate("what/service", element).map(e -> createService(e, devices)).collect(Collectors.toList());
    }
    
    public void onEvent(Event evt) {
        // TODO: if it is a time event, we need to check if it falls within days
        // if not then we close down operations
        services.stream().forEach(s -> s.onEvent(evt));
    }
    
    private static OutputDeviceClient createService(Element cfg, Map<String, OutputDevice> devices) {
        String deviceID = cfg.getAttribute("device");
        OutputDevice device = devices.get(deviceID);
        if(device == null) {
            throw new RuntimeException("Unknown device id: " + deviceID);            
        }
        return device.createService(cfg);
    }
    
}
