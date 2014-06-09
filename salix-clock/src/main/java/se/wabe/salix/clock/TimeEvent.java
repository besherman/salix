/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.wabe.salix.clock;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import se.wabe.salix.exports.Event;

/**
 *
 * @author Richard
 */
public class TimeEvent implements Event {    
    private final Map<String, Object> props;

    public TimeEvent(LocalTime now) {
        Map<String, Object> map = new HashMap<>();
        map.put("time", now);
        this.props = Collections.unmodifiableMap(map);
    }
    
    @Override
    public String getTopic() {
        return "Time";
    }

    @Override
    public Map<String, Object> getProperties() {
        return props;
    }

    @Override
    public String toString() {
        return "TimeEvent{" + "props=" + props + '}';
    }
    
    
}
