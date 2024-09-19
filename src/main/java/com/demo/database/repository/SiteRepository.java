package com.demo.database.repository;

import com.demo.database.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    @Query(nativeQuery = true,
            value = "SELECT DISTINCT id, latitude, longitude FROM public.site where (latitude != '' and longitude != '' )  and ( latitude != '-' and longitude!='-');")
    List<Site> findValidSites();
}
