/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.soundplayer;

import java.time.Duration;
import java.time.LocalTime;
import org.w3c.dom.Element;
import com.github.besherman.salix.exports.Event;
import com.github.besherman.salix.exports.OutputDeviceClient;
import static com.github.besherman.salix.exports.CfgUtil.*;

/**
 *
 * @author Richard
 */
public class SoundDeviceClient implements OutputDeviceClient {
    private boolean playing;
    private final LocalTime start;
    private final String play;
    private final Duration snooze;
    private final Integer snoozeAllowed;
    private final LocalTime snoozeUntil;
    private final SoundPlayer player;
    
    private enum State { WAITING, PLAYING, SNOOZING };
    
    private State currentState = State.WAITING;
    private LocalTime startedPlaying;
    private int snoozeCount = 0;
    private LocalTime startedSnooze;
    
    private LocalTime now;
    
    public SoundDeviceClient(Element cfg) {
        this.start = readTime(cfg, "start");
        this.play = readString(cfg, "play");
        this.snooze = readDuration(cfg, "snooze");
        this.snoozeAllowed = readInt(cfg, "snooze-allowed");
        this.snoozeUntil = readTime(cfg, "snooze-until");
        
        this.player = new SoundPlayer(play);
        this.player.setRepeat(true);
    }

    @Override
    public void onEvent(Event evt) {
        if("Time".equals(evt.getTopic())) {
            now = (LocalTime)evt.getProperties().get("time");
            if(currentState == State.WAITING) {
                if(now.equals(start) || (now.isAfter(start) && now.isBefore(start.plusMinutes(1)))) {
                    // start playing!
                    System.out.println("starting to play");
                    currentState = State.PLAYING;
                    startedPlaying = now;
                    player.play();
                }
            } else if(currentState == State.PLAYING) {
                // TODO: how long should we continue to torment the neighbours?                
            } else if(currentState == State.SNOOZING && snooze != null) {
                if(now.isAfter(startedSnooze.plus(snooze))) {
                    System.out.println("no more snooze! Starting to play again");
                    currentState = State.PLAYING;
                    startedPlaying = now;
                    player.play();                    
                }
            }
        } else if("Snooze".equals(evt.getTopic())) {
            if(currentState == State.PLAYING && snooze != null && now != null) {
                boolean allowed = true;
                if(snoozeAllowed != null) {
                    allowed = allowed && snoozeCount < snoozeAllowed;
                }
                if(snoozeUntil != null) {
                    allowed = allowed && now.isBefore(snoozeUntil);
                }
                if(allowed) {
                    player.stop();
                    currentState = State.SNOOZING;                    
                    startedSnooze = now;
                    snoozeCount++;
                    System.out.println("now snoozing!");
                } else {                    
                    SoundPlayer naySayer = new SoundPlayer("efx_NO-Fabio_Farinelli-955789468.mp3");
                    naySayer.play();
                }
            }
        }
    }
    

}
