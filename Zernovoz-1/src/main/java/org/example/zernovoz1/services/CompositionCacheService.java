package org.example.zernovoz1.services;

import org.example.zernovoz1.models.WagonModel;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CompositionCacheService {
    private final Map<String, List<WagonModel>> cache = new ConcurrentHashMap<>();

    public void addWagon(String station, WagonModel wagon) {
        cache.computeIfAbsent(station, k -> new ArrayList<>()).add(wagon);
    }

    public List<WagonModel> getWagonsByStation(String station) {
        return cache.getOrDefault(station, Collections.emptyList());
    }

    public boolean hasWagons(String station) {
        return cache.containsKey(station) && !cache.get(station).isEmpty();
    }

    public void clearStation(String station) {
        cache.remove(station);
    }
}