/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.bigredbutton;

import java.util.ArrayList;
import java.util.List;
import com.github.besherman.salix.exports.EventListener;
import com.github.besherman.salix.exports.InputDevice;

/**
 *
 * @author Richard
 */
public class BigRedButtonInputDevice implements InputDevice {
    private List<EventListener> listeners = new ArrayList<>();

    @Override
    public void addEventListener(EventListener hub) {
        listeners.add(hub);
    }

    @Override
    public void removeEventListener(EventListener hub) {
        listeners.remove(hub);
    }

    @Override
    public void initialize() {
        
    }

    @Override
    public void dispose() {
        
    }
    
}
