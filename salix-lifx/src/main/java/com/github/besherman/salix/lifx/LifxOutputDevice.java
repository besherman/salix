/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.lifx;

import android.content.Context;
import com.github.besherman.salix.exports.OutputDevice;
import com.github.besherman.salix.exports.OutputDeviceClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import lifx.java.android.client.LFXClient;
import lifx.java.android.entities.LFXHSBKColor;
import lifx.java.android.entities.LFXTypes;
import lifx.java.android.light.LFXLight;
import lifx.java.android.light.LFXLightCollection;
import lifx.java.android.network_context.LFXNetworkContext;
import org.w3c.dom.Element;

/**
 *
 * @author Richard
 */
public class LifxOutputDevice implements OutputDevice {
    private LifxThread thread;
    private final LifxLampTracker tracker;
    
    public static void main(String[] args) throws Exception {
        LifxOutputDevice dev = new LifxOutputDevice();
        dev.initialize();
        
        Thread.sleep(2_000);
        System.out.println("\n\n\n\n\n");
        System.out.println("setting color...");
        java.awt.Toolkit.getDefaultToolkit().beep();
        long time = 10 * 1000000;
        //dev.tracker.lights.get(0).setColorOverDuration(LFXHSBKColor.getColor(240, 1f, 1f, 0), time);
        dev.tracker.lights.get(0).setColorOverDuration(LFXHSBKColor.getColor(0, 1f, 0.02f, 0), 0);
        System.out.println("\n\n\n");
        
        Thread.sleep(3_000);
        dev.dispose();
        System.exit(0);
    }

    public LifxOutputDevice() {
        this.tracker = new LifxLampTracker();        
    }
    
    @Override
    public String getID() {
        return "lifx";
    }

    @Override
    public OutputDeviceClient createService(Element cfg) {
        return new LifxDeviceClient(this, cfg);
    }

    @Override
    public void initialize() {
        if(thread != null) {
            throw new RuntimeException("LifxOutputDevice is already initialized");
        }
        
        thread = new LifxThread(tracker);        
        thread.start();
    }

    @Override
    public void dispose() {        
        thread.shutdown();
        thread = null;     
        tracker.removeAllLights();
    }
    
    public Optional<LFXLight> getLightByLabel(String label) {
        return tracker.getLightByLabel(label);
    }
    
    
    private static class LifxLampTracker implements LFXLightCollection.LFXLightCollectionListener {
        private final List<LFXLight> lights = new ArrayList<>();
        
        public Optional<LFXLight> getLightByLabel(String label) {
            return lights.stream().filter(l -> l.getLabel().equals(label)).findFirst();
        }
        
        public void removeAllLights() {
            lights.clear();
        }
        
        @Override
        public void lightCollectionDidAddLight(LFXLightCollection lightCollection, LFXLight light) {
            lights.add(light);
        }

        @Override
        public void lightCollectionDidRemoveLight(LFXLightCollection lightCollection, LFXLight light) {
            lights.remove(light);
        }

        @Override
        public void lightCollectionDidChangeLabel(LFXLightCollection lightCollection, String label) {
        }

        @Override
        public void lightCollectionDidChangeColor(LFXLightCollection lightCollection, LFXHSBKColor color) {
        }

        @Override
        public void lightCollectionDidChangeFuzzyPowerState(LFXLightCollection lightCollection, LFXTypes.LFXFuzzyPowerState fuzzyPowerState) {
        }
        
    }
    
    private static class LifxThread extends Thread {
        private final Object runningLock = new Object();
        private final LFXLightCollection.LFXLightCollectionListener listener;
        
        public LifxThread(LFXLightCollection.LFXLightCollectionListener listener) {
            this.listener = listener;
        }
        
        public void shutdown() {
            synchronized(runningLock) {
                runningLock.notify();
            }
        }
        
        @Override
        public void run() {
            try {
                LFXNetworkContext localNetworkContext = LFXClient.getSharedInstance(new Context()).getLocalNetworkContext();                
                localNetworkContext.connect();        
                localNetworkContext.getAllLightsCollection().addLightCollectionListener(listener);
                synchronized(runningLock) {
                    runningLock.wait();
                }
                localNetworkContext.disconnect();
                Thread.sleep(500);
            } catch(InterruptedException ex) {
                Logger.getLogger(LifxThread.class.getName()).log(Level.INFO, "LightManager thread was interrupted");
            }            
        }        
    }
}
