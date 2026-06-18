package com.AuraMoon.auramoon.billing.repository;

import com.AuraMoon.auramoon.billing.entity.FolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FolioItemRepository extends JpaRepository<FolioItem, Integer> {
    List<FolioItem> findByGuestFolioId(Integer folioId);

    List<FolioItem> findByGuestFolioIdIn(List<Integer> folioIds);

    boolean existsByGuestFolioIdAndStatusIn(Integer folioId, List<String> statuses);

    @Modifying
    @Query(value = "INSERT INTO FOLIO_ITEM (folio_id, service_category, reference_id, description, amount, status, create_at, create_by) " +
            "SELECT :folioId, 'F_AND_B', mo.meal_order_id, 'Phát sinh dịch vụ F&B (Order #' + CAST(mo.meal_order_id AS VARCHAR) + ')', " +
            "       (SELECT ISNULL(SUM(moi.quantity * moi.price), 0) FROM MEAL_ORDER_ITEM moi WHERE moi.meal_order_id = mo.meal_order_id), " +
            "       'UNPAID', GETDATE(), :actorId " +
            "FROM MEAL_ORDER mo " +
            "WHERE mo.booking_id = :bookingId AND mo.order_status = 'DELIVERED' " +
            "AND NOT EXISTS (SELECT 1 FROM FOLIO_ITEM fi WHERE fi.reference_id = mo.meal_order_id AND fi.service_category = 'F_AND_B')", nativeQuery = true)
    int consolidateFnbCharges(@Param("bookingId") Integer bookingId, @Param("folioId") Integer folioId, @Param("actorId") Integer actorId);

    @Modifying
    @Query(value = "INSERT INTO FOLIO_ITEM (folio_id, service_category, reference_id, description, amount, status, create_at, create_by) " +
            "SELECT :folioId, 'SPA', tb.treatment_id, 'Phát sinh dịch vụ Spa (Booking #' + CAST(tb.treatment_id AS VARCHAR) + ')', " +
            "       ISNULL((SELECT ts.price FROM TREATMENT_SERVICE ts WHERE ts.service_id = tb.service_id), 0), " +
            "       'UNPAID', GETDATE(), :actorId " +
            "FROM TREATMENT_BOOKING tb " +
            "WHERE tb.booking_id = :bookingId AND tb.status = 'COMPLETED' " +
            "AND NOT EXISTS (SELECT 1 FROM FOLIO_ITEM fi WHERE fi.reference_id = tb.treatment_id AND fi.service_category = 'SPA')", nativeQuery = true)
    int consolidateSpaCharges(@Param("bookingId") Integer bookingId, @Param("folioId") Integer folioId, @Param("actorId") Integer actorId);

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FolioItem f WHERE f.guestFolio.id = :folioId AND f.serviceCategory = :category AND CAST(f.createAt AS date) = CURRENT_DATE")
    BigDecimal sumChargesByFolioAndCategoryToday(@Param("folioId") Integer folioId, @Param("category") String category);
}
