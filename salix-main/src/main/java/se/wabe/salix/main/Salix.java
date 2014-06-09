/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.wabe.salix.main;

import java.time.LocalTime;
import se.wabe.salix.big.red.button.DebugSnoozeInputDevice;
import se.wabe.salix.clock.DebugAutoClockInputDevice;
import se.wabe.salix.core.Scheduler;
import se.wabe.salix.exports.InputDevice;
import se.wabe.salix.exports.OutputDevice;
import se.wabe.salix.sound.player.SoundPlayerOutputDevice;

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
