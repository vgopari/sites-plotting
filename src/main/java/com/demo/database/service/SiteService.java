package com.demo.database.service;

import com.demo.database.entity.Site;
import com.demo.database.repository.SiteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Service
public class SiteService {

    private static final Logger logger = Logger.getLogger(SiteService.class.getName());

    private final SiteRepository siteRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public SiteService(SiteRepository siteRepository, RedisTemplate<String, Object> redisTemplate) {
        this.siteRepository = siteRepository;
        this.redisTemplate = redisTemplate;
    }


    public List<Site> getValidSites() {
        return siteRepository.findValidSites();
    }

    public void writeSitesToRedis() {
        List<Site> sites = getValidSites();
        for (Site site : sites) {
            String key = "site:" + site.getId();
            logger.info(key);
            try {
                String json = objectMapper.writeValueAsString(site);
                redisTemplate.opsForValue().set(key, json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public Optional<Site> getSiteFromRedis(Long siteId) {
        String key = "site:" + siteId;
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                Site site = objectMapper.readValue(json, Site.class);
                return Optional.of(site);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

//    public Map<String, IvisSite> getAllSitesFromRedis() {
//        Map<String, IvisSite> allSites = new HashMap<>();
//        Set<String> keys = redisTemplate.keys("site:*");
//        assert keys != null;
//        for (String key : keys) {
//            String json = (String) redisTemplate.opsForValue().get(key);
//            try {
//                IvisSite site = objectMapper.readValue(json, IvisSite.class);
//                allSites.put(key, site);
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        }
//        return allSites;
//    }

    public Map<String, Site> getAllSitesFromRedis() {
        Map<String, Site> allSites = new ConcurrentHashMap<>();
        Set<String> keys = redisTemplate.keys("site:*");
        if (keys != null) {
            ExecutorService executor = Executors.newFixedThreadPool(3);
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            // Submit tasks for each key
            for (String key : keys) {
                CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                    String json = (String) redisTemplate.opsForValue().get(key);
                    try {
                        Site site = objectMapper.readValue(json, Site.class);
                        if (site != null) {
                            allSites.put(key, site);
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }, executor);
                futures.add(future);
            }

            // Wait for all tasks to complete
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            try {
                allFutures.get(); // This blocks until all tasks are complete
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Shutdown executor after all tasks are submitted
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return allSites;
    }

}
