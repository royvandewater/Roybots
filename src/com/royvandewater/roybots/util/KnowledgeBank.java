package com.royvandewater.roybots.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import robocode.ScannedRobotEvent;

public class KnowledgeBank {

    private HashMap<String, ArrayList<Sighting>> bank = new HashMap<>();

    public void addSighting(ScannedRobotEvent event) {
        String name = event.getName();
        if (!bank.containsKey(name))
            bank.put(name, new ArrayList<Sighting>());

        Sighting sighting = Sighting.fromScannedRobotEvent(event);
        bank.get(name).add(sighting);
    }

    public void getFiringSolution(ScannedRobotEvent target) {
        List<Sighting> pattern = findPattern(target);
    }

    private List<Sighting> findPattern(ScannedRobotEvent target) {

        List<Sighting> sightings = bank.get(target.getName());
        
        double smallestDifference = Double.POSITIVE_INFINITY;
        int startIndex = 0;
        
        if (sightings != null && sightings.size() > 7) {
            List<Sighting> lastSeven = sightings.subList(sightings.size() - 8, sightings.size() - 1);

            for (int i = 0; i < sightings.size() - 15; i++) {
                double difference = 0;
                
                for (int j = 0; j < 7; j++) {
                    Sighting newSighting = lastSeven.get(j);
                    Sighting oldSighting = sightings.get(i + j);
                    
                    difference += newSighting.minus(oldSighting);
                }
                
                if(difference < smallestDifference) {
                    smallestDifference = difference;
                    startIndex = i;
                }
            }
            
            return sightings.subList(startIndex, sightings.size() - 1);
        }
        return null;
    }
}
