package com.demo.database.controller;

import com.demo.database.entity.Site;
import com.demo.database.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/sites")
@CrossOrigin(origins = "*")
public class SiteController {

    private final SiteService siteService;

    @Autowired
    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping("/write-to-redis")
    public String writeSitesToRedis() {
        siteService.writeSitesToRedis();
        return "Data written to Redis successfully";
    }

    @GetMapping("/get-from-redis/{siteId}")
    public Optional<Site> getSiteFromRedis(@PathVariable Long siteId) {
        return siteService.getSiteFromRedis(siteId);
    }
    @GetMapping("/get-all-from-redis")
    public Map<String, Site> getAllSitesFromRedis() {
        return siteService.getAllSitesFromRedis();
    }

    @GetMapping("/db/getAllLatLng")
    public List<Site> getAll() {
        return siteService.getValidSites();
    }
}
