package com.richcodes.hookrelay.repository;

import com.richcodes.hookrelay.domain.Delivery;
import com.richcodes.hookrelay.domain.Endpoint;
import com.richcodes.hookrelay.domain.Event;
import com.richcodes.hookrelay.domain.Merchant;
import com.richcodes.hookrelay.enums.DeliveryStatus;
import com.richcodes.hookrelay.enums.EndpointStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery,Long> {

    @Query("""
    SELECT d FROM Delivery d
    LEFT JOIN FETCH d.event
    LEFT JOIN FETCH d.endpoint e
    WHERE d.id = :deliveryId
""")
    Optional<Delivery> findByIdWithDetails(@Param("deliveryId") String deliveryId);

    @Query("""
    SELECT d FROM Delivery d
    JOIN FETCH d.event
    JOIN FETCH d.endpoint e
    LEFT JOIN FETCH e.subscribedEvents
""")
    List<Delivery> findAllWithRelations();

    List<Delivery> findByStatusAndNextRetryAtBefore(
            DeliveryStatus status,
            LocalDateTime now
    );



    @Query("""
    SELECT d FROM Delivery d
    LEFT JOIN FETCH d.deliveryAttempts
    LEFT JOIN FETCH d.endpoint
    WHERE d.event.id = :eventId
    AND d.event.merchant = :merchant
""")
    List<Delivery> findDeliveriesByEventIdAndMerchant(
            @Param("eventId") String eventId,
            @Param("merchant") Merchant merchant
    );

    @Query("""
    SELECT d FROM Delivery d
    JOIN FETCH d.event
    JOIN FETCH d.endpoint e
    LEFT JOIN FETCH e.subscribedEvents
    WHERE d.status  = :status
""")
    List<Delivery> findAllDeadLettersWithRelations(DeliveryStatus status);

    @Query("""
    SELECT d FROM Delivery d
    JOIN FETCH d.event
    JOIN FETCH d.endpoint e
    LEFT JOIN FETCH e.subscribedEvents
    WHERE d.id = :id AND
    d.status  = :status
    
""")
    Delivery findDeadLettersByIdWithRelations(DeliveryStatus status,String id);

    List<Delivery> findByEvent(Event event);



}
