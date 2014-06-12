/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.exports;

import org.w3c.dom.Element;

/**
 *
 * @author Richard
 */
public interface OutputDevice {
    String getID();
    OutputDeviceClient createService(Element cfg);
    void initialize();
    void dispose();    
}
