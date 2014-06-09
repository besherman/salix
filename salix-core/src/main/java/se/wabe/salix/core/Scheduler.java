/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.wabe.salix.core;

import java.io.InputStream;
import se.wabe.salix.exports.EventListener;
import se.wabe.salix.exports.InputDevice;
import se.wabe.salix.exports.Event;
import se.wabe.salix.exports.ConfigException;
import se.wabe.salix.exports.OutputDevice;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author Richard
 */
public class Scheduler {    
    private final EventHub evtHub = new EventHub();
    private final Map<String, OutputDevice> outputDevices = new HashMap<>();
    private List<Alarm> alarms = new ArrayList<>();
    private final Object alarmsLock = new Object();
    
    
    public void addInputDevice(InputDevice dev) {        
        dev.addEventListener(evtHub);        
        dev.initialize();
    }
    
    public void removeInputDevice(InputDevice dev) {
        dev.removeEventListener(evtHub);        
        dev.dispose();
    }
    
    public void addOutputDevice(OutputDevice dev) {
        synchronized(outputDevices) {
            if(outputDevices.containsKey(dev.getID())) {
                OutputDevice other = outputDevices.get(dev.getID());
                throw new RuntimeException(String.format("Failed to add OutputDevice with class %s: id='%s' already exists. It is registered by class %s",
                        dev.getClass().getName(), dev.getID(), other.getClass().getName()));
            }
            outputDevices.put(dev.getID(), dev);
        }
    }
    
    public void removeOutputDevice(OutputDevice dev) {
        synchronized(outputDevices) {
            outputDevices.remove(dev.getID());
        }
    }
    
    public void load(InputStream input) throws ConfigException {
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(input));        
            synchronized(alarmsLock) {
                alarms = XPather.evaluate("/schedule/alarms/alarm", doc).map(e -> new Alarm(e, outputDevices)).collect(Collectors.toList());
            }
        } catch(Exception ex) {
            throw new ConfigException("Failed to read config file", ex);
        } 
    }
    
    
    private class EventHub implements EventListener {
        @Override
        public void onEvent(Event evt) {
            System.out.println("Distributing event " + evt);
            synchronized(alarmsLock) {
                // we need to figure out how to handle days
                alarms.stream().forEach((a) -> { a.onEvent(evt); });
            }
            
        }
    }
}
