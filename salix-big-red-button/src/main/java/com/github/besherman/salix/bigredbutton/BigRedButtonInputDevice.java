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
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

/**
 *
 * @author Richard
 */
public class BigRedButtonInputDevice implements InputDevice {
    private static final byte LID_CLOSED  = 21;
    private static final byte BUTTON_PRESSED = 22;
    private static final byte LID_OPEN = 23;
    
    public static void main(String[] args) throws Exception {
        byte[] buf = new byte[8];
        Path path = Paths.get("/dev/hidraw0");
        
        Instant whenButtonDown = Instant.now();
        int lid = 0;
        int prior = 0;        
        try(RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw")) {
            while(true) {
                Arrays.fill(buf, (byte)0);
                buf[0] = 0x08;
                buf[7] = 0x02;
                file.write(buf);
                
                int read = file.read(buf);
                if(read > 0) {
                    int state = buf[0];
                    if(state != prior) {
                        if(state == BUTTON_PRESSED) {
                            System.out.println("BUTTON_DOWN");
                            whenButtonDown = Instant.now();
                        } else {
                            if(prior == BUTTON_PRESSED) {
                                System.out.println("BUTTON_UP");
                                Duration time = Duration.between(whenButtonDown, Instant.now());
                                System.out.println(time);
                            }
                            
                            if(lid != state) {
                                if(state == LID_OPEN) {
                                    System.out.println("ARMED");
                                    lid = state;
                                } else if(state == LID_CLOSED) {
                                    System.out.println("DISARMED");
                                    lid = state;
                                }
                            }
                            
                        }
                    }
                    prior = state;
                }
                
                // go any lower and it starts to freeze up
                Thread.sleep(200);
            }
        }
        
        
    }
    
    
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
