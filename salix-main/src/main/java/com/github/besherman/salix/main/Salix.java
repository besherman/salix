/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.main;

import java.time.LocalTime;
import com.github.besherman.salix.bigredbutton.DebugSnoozeInputDevice;
import com.github.besherman.salix.clock.DebugAutoClockInputDevice;
import com.github.besherman.salix.core.Scheduler;
import com.github.besherman.salix.exports.InputDevice;
import com.github.besherman.salix.exports.OutputDevice;
import com.github.besherman.salix.soundplayer.SoundPlayerOutputDevice;

/**
 *
 * @author Richard
 */
public class Salix {
    public static void main(String[] args) throws Exception {
        System.out.println("starting scheduler...");
        Scheduler scheduler = new Scheduler();
        InputDevice clock = new DebugAutoClockInputDevice(LocalTime.parse("06:29:57"));
        scheduler.addInputDevice(clock);
        
        DebugSnoozeInputDevice button = new DebugSnoozeInputDevice();
        scheduler.addInputDevice(button);
        
        OutputDevice soundPlayer = new SoundPlayerOutputDevice();
        scheduler.addOutputDevice(soundPlayer);
        
        scheduler.load(Salix.class.getResourceAsStream("/config.xml"));        
        
        Thread.sleep(16 * 1000);
        
        button.snooze();
        
        Thread.sleep(6 * 1000);
        
        button.snooze();
        

    }
}
