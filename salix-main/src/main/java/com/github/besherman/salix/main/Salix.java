/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.main;

import com.github.besherman.salix.bigredbutton.DebugSnoozeInputDevice;
import com.github.besherman.salix.clock.ClockInputDevice;
import com.github.besherman.salix.clock.DebugAutoClockInputDevice;
import com.github.besherman.salix.exports.InputDevice;
import com.github.besherman.salix.exports.OutputDevice;
import com.github.besherman.salix.soundplayer.SoundPlayerOutputDevice;
import com.github.besherman.salix.lifx.LifxOutputDevice;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;

/**
 *
 * @author Richard
 */
public class Salix {
    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().readConfiguration(Salix.class.getResourceAsStream("/logging.properties"));
        
        System.out.println("starting scheduler...");
        Scheduler scheduler = new Scheduler();
        
        List<InputDevice> inputs = Arrays.asList(
            //new DebugAutoClockInputDevice(LocalTime.parse("08:28:56")),
            new ClockInputDevice(),
            new DebugSnoozeInputDevice()
        );
        
        List<OutputDevice> outputs = Arrays.asList(
                new SoundPlayerOutputDevice(),
                new LifxOutputDevice()
        );
        
        inputs.stream().forEach(i -> { 
            i.initialize();
            scheduler.addInputDevice(i);
        });
        
        outputs.stream().forEach(o -> {
            o.initialize();
            scheduler.addOutputDevice(o);
        });
        
        
        scheduler.load(Salix.class.getResourceAsStream("/config.xml"));        
        
        Thread.sleep(31 * 60 * 1000);
        
        System.out.println("Shutting down");
        
        inputs.forEach(InputDevice::dispose);
        outputs.forEach(OutputDevice::dispose);
    }
}
