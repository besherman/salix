/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se.wabe.salix.exports;

import java.util.Map;

/**
 *
 * @author Richard
 */
public interface Event {
    String getTopic();
    Map<String, Object> getProperties();
}
