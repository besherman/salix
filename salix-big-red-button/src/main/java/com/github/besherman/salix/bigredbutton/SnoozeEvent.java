/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.bigredbutton;

import java.util.HashMap;
import java.util.Map;
import com.github.besherman.salix.exports.Event;

/**
 *
 * @author Richard
 */
public class SnoozeEvent implements Event {

    @Override
    public String getTopic() {
        return "Snooze";
    }

    @Override
    public Map<String, Object> getProperties() {
        return new HashMap();
    }
    
}
