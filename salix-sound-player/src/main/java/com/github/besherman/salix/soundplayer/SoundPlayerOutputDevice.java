/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.besherman.salix.soundplayer;

import org.w3c.dom.Element;
import com.github.besherman.salix.exports.OutputDevice;
import com.github.besherman.salix.exports.OutputDeviceClient;

/**
 *
 * @author Richard
 */
public class SoundPlayerOutputDevice implements OutputDevice {

    @Override
    public String getID() {
        return "sound-player";
    }

    @Override
    public OutputDeviceClient createService(Element cfg) {
        return new SoundDeviceClient(cfg);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void dispose() {
    }
}
