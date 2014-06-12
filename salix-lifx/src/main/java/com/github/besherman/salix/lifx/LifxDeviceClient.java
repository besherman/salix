/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.lifx;

import static com.github.besherman.salix.exports.CfgUtil.*;
import com.github.besherman.salix.exports.Event;
import com.github.besherman.salix.exports.OutputDeviceClient;
import java.time.Duration;
import java.time.LocalTime;
import lifx.java.android.entities.LFXHSBKColor;
import lifx.java.android.entities.LFXTypes;
import org.w3c.dom.Element;

/**
 *
 * @author Richard
 */
public class LifxDeviceClient implements OutputDeviceClient {
    private final LifxOutputDevice device;
    private final String label;
    private final LocalTime stop;
    private final LocalTime start;
    private final Duration duration;
    
    
    private enum State { ON, OFF };
    
    private State currentState = State.OFF;
    private LocalTime now;
    private ColorInterpolator colorInterpol;
    
    
    
    public LifxDeviceClient(LifxOutputDevice device, Element cfg) {
        this.device = device;
        this.stop = readTime(cfg, "start");
        this.duration = readDuration(cfg, "sunrise-duration");
        this.label = readString(cfg, "device-label");
        
        this.start = stop.minus(duration);
    }
    
    @Override
    public void onEvent(Event evt) {
        if("Time".equals(evt.getTopic())) {
            now = (LocalTime)evt.getProperties().get("time");
            if(currentState == State.OFF) {
                if(now.equals(start) || (now.isAfter(start) && now.isBefore(start.plusMinutes(1)))) {
                    device.getLightByLabel(label).ifPresent(light -> {
                        colorInterpol = new ColorInterpolator(start, stop);
                        light.setPowerState(LFXTypes.LFXPowerState.ON);                        
                        light.setColorOverDuration(LFXHSBKColor.getColor(0, 1f, 0f, 0), 0);
                        currentState = State.ON;
                    });
                }
            } else {
                if(now.isBefore(stop)) {
                    device.getLightByLabel(label).ifPresent(light -> {
                        colorInterpol.setColor(light, now);
                    });
                }
            }
        }        
    }
    
    
    
}
