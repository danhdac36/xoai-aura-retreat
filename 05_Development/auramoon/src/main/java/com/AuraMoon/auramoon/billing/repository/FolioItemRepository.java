package com.AuraMoon.auramoon.billing.repository;

import com.AuraMoon.auramoon.billing.entity.FolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolioItemRepository extends JpaRepository<FolioItem, Integer> {
    List<FolioItem> findByGuestFolioId(Integer folioId);
    List<FolioItem> findByGuestFolioIdIn(List<Integer> folioIds);
    boolean existsByGuestFolioIdAndStatusIn(Integer folioId, List<String> statuses);
}
