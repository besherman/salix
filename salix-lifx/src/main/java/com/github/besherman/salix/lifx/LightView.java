/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.lifx;

import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.Optional;
import lifx.java.android.entities.LFXHSBKColor;
import lifx.java.android.entities.LFXTypes;
import lifx.java.android.light.LFXLight;

/**
 *
 * @author Richard
 */
public class LightView {
    private volatile LFXLight light;
    
    private Optional<String> label = Optional.empty();
    private Optional<LFXHSBKColor> color = Optional.empty();
    private Optional<LFXTypes.LFXPowerState> powerState = Optional.empty();
    
    private final String deviceID;
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public LightView(LFXLight light) {
        this.light = light;
        this.deviceID = light.getDeviceID();
        light.addLightListener(handler);
    }    
    
    public Optional<LFXLight> getLight() {
        return Optional.ofNullable(light);
    } 
    
    public String getDeviceID() {
        return deviceID;
    }
    
    public Optional<String> getLabel() {
        return label;
    }

    public Optional<LFXHSBKColor> getColor() {
        return color;
    }

    public Optional<LFXTypes.LFXPowerState> getPowerState() {
        return powerState;
    }

    public void dispose() {
        light.removeLightListener(handler);
        light = null;
    }
    
    public boolean isDisposed() {
        return light == null;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.deviceID);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LightView other = (LightView) obj;
        if (!Objects.equals(this.deviceID, other.deviceID)) {
            return false;
        }
        return true;
    }
    
    

    
    private final LFXLight.LFXLightListener handler = new LFXLight.LFXLightListener() {
        @Override
        public void lightDidChangeLabel(LFXLight light, String newLabel) {            
            Optional<String> old = label;
            label = Optional.ofNullable(newLabel);
            if(!old.equals(label)) {
                pcs.firePropertyChange("label", old, label);
            }
        }

        @Override
        public void lightDidChangeColor(LFXLight light, LFXHSBKColor newColor) {
            Optional<LFXHSBKColor> old = color;
            color = Optional.ofNullable(newColor);
            if(!old.equals(color)) {
                pcs.firePropertyChange("color", old, color);
            }
        }

        @Override
        public void lightDidChangePowerState(LFXLight light, LFXTypes.LFXPowerState newPowerState) {
            Optional<LFXTypes.LFXPowerState> old = powerState;
            powerState = Optional.ofNullable(newPowerState);
            if(!old.equals(powerState)) {
                pcs.firePropertyChange("powerState", old, powerState);
            }
        }        
    };
}
