/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.exports;

import java.time.Duration;
import java.time.LocalTime;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Richard
 */
public class CfgUtil {
    public static LocalTime readTime(Element parent, String tag) {
        String str = readString(parent, tag);
        if(str != null) {
            return LocalTime.parse(str);
        }
        return null;
    }
    
    public static String readString(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        if(nl.getLength() == 0) {
            return null;
        }
        return nl.item(0).getTextContent();
    }
    
    public static Duration readDuration(Element parent, String tag) {
        String str = readString(parent, tag);
        if(str != null) {
            return Duration.parse(str);
        }
        return null;
    }
    
    public static Integer readInt(Element parent, String tag) {
        String str = readString(parent, tag);
        if(str != null) {
            return Integer.parseInt(str);
        }
        return null;
    }    
}
