/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.lifx;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import lifx.java.android.client.LFXClient;
import lifx.java.android.entities.LFXHSBKColor;
import lifx.java.android.entities.LFXTypes;
import lifx.java.android.light.LFXLight;
import lifx.java.android.light.LFXLightCollection;
import lifx.java.android.network_context.LFXNetworkContext;

/**
 *
 * @author Richard
 */
public class LightManager {
    private MgrThread thread;
    private final CollectionListener colListener;
    private final LightStore store;
    
    public static void main(String[] args) throws Exception {
    }

    public LightManager() {
        store = new LightStore();
        colListener = new CollectionListener(store);
    }
    
    public void start() {
        thread = new MgrThread(colListener);
        thread.start();
    }
    
    public void stop() {
        thread.shutdown();
    }
    
    private static class MgrThread extends Thread {
        private final Object runningLock = new Object();
        private final LFXLightCollection.LFXLightCollectionListener listener;
        
        public MgrThread(LFXLightCollection.LFXLightCollectionListener listener) {
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
                Logger.getLogger(MgrThread.class.getName()).log(Level.INFO, "LightManager thread was interrupted");
            }            
        }        
    }
    
    private static class CollectionListener implements LFXLightCollection.LFXLightCollectionListener {

        private final LightStore store;

        public CollectionListener(LightStore store) {
            this.store = store;
        }
        
        
        @Override
        public void lightCollectionDidAddLight(LFXLightCollection lightCollection, LFXLight light) {
            store.addLight(light);
        }

        @Override
        public void lightCollectionDidRemoveLight(LFXLightCollection lightCollection, LFXLight light) {
            store.removeLight(light);
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
    
    private static class LightStore {
        private final Map<String, LightView> lights = new HashMap<>();
        
        public LightView getLightByDeviceID(String deviceID) {
            return lights.get(deviceID);
        }
        
        public Optional<LightView> getLightByLabel(String label) {
            // TODO: rewrite functionally
            for(LightView view: lights.values()) {
                if(view.getLabel().isPresent()) {
                    if(view.getLabel().get().equals(label)) {
                        return Optional.of(view);
                    }
                }
            }
            return Optional.empty();
        }
        
        public void addLight(LFXLight light) {
            LightView view = lights.get(light.getDeviceID());
            if(view != null) {
                throw new RuntimeException("Adding already existing light");
            } else {
                lights.put(light.getDeviceID(), new LightView(light));
            }
        }
        
        public void removeLight(LFXLight light) {
            LightView view = lights.get(light.getDeviceID());
            if(view != null) {
                view.dispose();
                lights.remove(light.getDeviceID());
            }
        }
    }

}
