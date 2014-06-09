/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.besherman.salix.soundplayer;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author Richard
 */
public class SoundPlayer implements Runnable {
    private final ExecutorService executor;
    private final String file;
    private volatile FloatControl gainCtrl;
    private final AtomicBoolean bussy = new AtomicBoolean(false);
    private int volume = 100;
    private volatile boolean repeat = false;
    private volatile boolean abort = false;
    

    public SoundPlayer(String file) {
        this.file = file;
        this.executor = Executors.newSingleThreadExecutor();
    }
    
    public void play() {        
        if(bussy.compareAndSet(false, true)) {
            executor.submit(this);
        } else {
            throw new RuntimeException("Player is already playing");
        }
    }
    
    public boolean isPlaying() {
        return bussy.get();
    }
    
    public void stop() {
        abort = true;
    }
    
    public void setVolume(int level) {
        this.volume = level;
        if(gainCtrl != null) {
            float min = gainCtrl.getMinimum();
            float range = Math.abs(min);
            float normLevel = level / 100f;
            float value = min + range * normLevel;
            gainCtrl.setValue(value);
        }
    }
    
    public void setRepeat(boolean b) {
        this.repeat = b;
    }
    

    @Override
    public void run() {
        try {
            do {
                InputStream input = getClass().getResourceAsStream("/" + file);
                play(input);
            } while(repeat && !abort);
        } finally {
            bussy.set(false);
            abort = false;
        }
    }
    
    private void setGainControl(FloatControl ctrl) {
        gainCtrl = ctrl;
        setVolume(volume);
    }
    
    private void play(InputStream input) {
        setGainControl(null);
        
        // http://www.javalobby.org/java/forums/t18465.html
        try {
            // The first step is to ask for an audio input stream for the MP3 you want to play:
            AudioInputStream in = AudioSystem.getAudioInputStream(input);

            // Then, you need to create a javax.sound.sampled.AudioFormat object 
            // that you want to decode the audio to (what will be played through 
            // the user's sound system). We do this by getting the base audio 
            // input's format, and use it (along with some hints from Javazoom's 
            // site) to produce a decoded format compatible with the base format:
            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED, // Encoding to use
                    baseFormat.getSampleRate(), // sample rate (same as base format)
                    16, // sample size in bits (thx to Javazoom)
                    baseFormat.getChannels(), // # of Channels
                    baseFormat.getChannels() * 2, // Frame Size
                    baseFormat.getSampleRate(), // Frame Rate
                    false // Big Endian
            );

            // Then it is simply a matter of getting a new input stream that 
            // will decode our current inputstream for this new decoded format:
            try (AudioInputStream decodedIn = AudioSystem.getAudioInputStream(decodedFormat, in)) {
                // Next, we need to get a connection the user's audio out - 
                // we do this with the DataLine classes:
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                
                if (line != null) {
                    line.open(decodedFormat);

                    for (Control ctrl : line.getControls()) {
                        if (ctrl instanceof FloatControl) {
                            setGainControl((FloatControl)ctrl);
                        }
                    }

                    // Finally, once we have the line open, we have to implement a 
                    // buffering strategy for tunneling data from our inputstream to 
                    // our line-out.                 
                    line.open(decodedFormat);
                    byte[] data = new byte[4096];
                    // Start
                    line.start();

                    int nBytesRead;
                    while((nBytesRead = decodedIn.read(data, 0, data.length)) != -1 && !abort) {
                        line.write(data, 0, nBytesRead);
                    }
                    
                    // Stop
                    if(!abort) {
                        line.drain();
                    }
                    line.stop();
                    line.close();
                    decodedIn.close();
                }
            } 
        } catch(Exception ex) {
            Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, null, ex);
        } 
    } 

    
}
