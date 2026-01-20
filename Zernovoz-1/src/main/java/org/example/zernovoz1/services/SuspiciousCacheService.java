package org.example.zernovoz1.services;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SuspiciousCacheService {

    public static class SuspiciousData {
        public String station;
        public String lastUpdated;
        public String speedKmh;
        public String identificationStatus;
        public String video;
    }

    private final Map<String, SuspiciousData> cache = new ConcurrentHashMap<>();

    public void put(String suspiciousNumber, SuspiciousData data) {
        cache.put(suspiciousNumber, data);
    }

    public SuspiciousData get(String suspiciousNumber) {
        return cache.get(suspiciousNumber);
    }

    public void remove(String suspiciousNumber) {
        cache.remove(suspiciousNumber);
    }
}