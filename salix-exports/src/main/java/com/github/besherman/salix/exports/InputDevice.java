/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.exports;

/**
 *
 * @author Richard
 */
public interface InputDevice {
    void addEventListener(EventListener hub);
    void removeEventListener(EventListener hub);
    void initialize();
    void dispose();
}
