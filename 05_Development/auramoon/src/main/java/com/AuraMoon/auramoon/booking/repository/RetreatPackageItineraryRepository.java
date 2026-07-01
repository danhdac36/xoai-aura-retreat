package com.AuraMoon.auramoon.booking.repository;

import com.AuraMoon.auramoon.booking.entity.RetreatPackageItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetreatPackageItineraryRepository extends JpaRepository<RetreatPackageItinerary, Integer> {
    List<RetreatPackageItinerary> findByRetreatPackageIdOrderByDayNumberAsc(Integer packageId);
}
