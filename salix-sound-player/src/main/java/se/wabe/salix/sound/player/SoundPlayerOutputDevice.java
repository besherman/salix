/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.wabe.salix.sound.player;

import org.w3c.dom.Element;
import se.wabe.salix.exports.OutputDevice;
import se.wabe.salix.exports.OutputDeviceClient;

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
}
