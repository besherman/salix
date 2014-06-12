/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.besherman.salix.lifx;

import java.time.Duration;
import java.time.LocalTime;
import static java.time.LocalTime.now;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lifx.java.android.entities.LFXHSBKColor;
import lifx.java.android.light.LFXLight;

/**
 *
 * @author Richard
 */
public class ColorInterpolator {

    private final List<LFXHSBKColor> colors = new ArrayList<>();
    private final LocalTime start;
    private final LocalTime stop;
    
    public ColorInterpolator(LocalTime start, LocalTime stop) {
        this.start = start;
        this.stop = stop;
     
        Duration duration = Duration.between(start, stop);
                
        int count = duration.toMinutes() > 1
                ? (int)Duration.between(start, stop).toMinutes()
                : (int)Duration.between(start, stop).getSeconds();
        
        int hueStart = 10,
            hueEnd = 60;
        
        for(int i = 0; i < count; i++) {
            int hue = hueStart + i * ((hueEnd - hueStart) / count);
            float brightness = i / (float)(count - 1);
            float saturation = 1f - brightness;
            
            LFXHSBKColor c = LFXHSBKColor.getColor(hue, saturation, brightness, 0);            
            colors.add(c);
        }
    }
    
    private int lastStep = -1;
    
    public void setColor(LFXLight light, LocalTime current) {
        double stepFracWidth = 1d / colors.size();
        
        Duration duration = Duration.between(start, stop);
        Duration stepLength = duration.dividedBy(colors.size());
        
        Duration time = Duration.between(start, current);
        double frac = time.get(ChronoUnit.SECONDS) / 
                (double)duration.get(ChronoUnit.SECONDS);
        
        int currentStep = (int)(frac / stepFracWidth);
        
        if(currentStep != lastStep) {  
            LFXHSBKColor c = colors.get(currentStep);
            System.out.println("setting new color: " + c);
            light.setColorOverDuration(c, stepLength.get(ChronoUnit.SECONDS) * 1000000);
        }
        
        lastStep = currentStep;
    }    
}
