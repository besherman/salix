/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.wabe.salix.clock;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import se.wabe.salix.exports.EventListener;
import se.wabe.salix.exports.InputDevice;

/**
 *
 * @author Richard
 */
public class ClockInputDevice extends TimerTask implements InputDevice {
    private final List<EventListener> listeners = new ArrayList<>(); 
    private final Timer timer = new Timer(true);
    
    @Override
    public void addEventListener(EventListener hub) {
        synchronized(listeners) {
            listeners.add(hub);
        }
    }

    @Override
    public void removeEventListener(EventListener hub) {
        synchronized(listeners) {
            listeners.remove(hub);
        }
    }

    @Override
    public void initialize() {
        timer.schedule(this, 1000, 1000);
    }

    @Override
    public void dispose() {
        timer.cancel();
    }

    @Override
    public void run() {
        TimeEvent evt = new TimeEvent(LocalTime.now());
        synchronized(listeners) {
            listeners.stream().forEach((l) -> { l.onEvent(evt); });
        }
    }
}
