package com.richcodes.hookrelay.repository;

import com.richcodes.hookrelay.domain.Endpoint;
import com.richcodes.hookrelay.domain.Merchant;
import com.richcodes.hookrelay.enums.EndpointStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EndpointRepository extends JpaRepository<Endpoint, UUID> {

    @Query("""
    SELECT e FROM Endpoint e
    LEFT JOIN FETCH e.subscribedEvents
    WHERE e.merchant = :merchant
""")
    List<Endpoint> findByMerchant(Merchant merchant);

    @Query("""
    SELECT DISTINCT e
    FROM Endpoint e
    JOIN e.subscribedEvents ev
    WHERE e.merchant = :merchant
    AND ev = :eventType
    AND e.status = :status
""")
    List<Endpoint> findByMerchantAndSubscribedEvent(
            @Param("merchant") Merchant merchant,
            @Param("eventType") String eventType,
            @Param("status")EndpointStatus status
    );

    @Query("""
    SELECT e
    FROM Endpoint e
    LEFT JOIN FETCH e.subscribedEvents
    WHERE e.merchant = :merchant
    AND e.id = :id
""")
    Optional<Endpoint> findByIdAndMerchant(
            @Param("merchant") Merchant merchant,
            @Param("id") String id
    );

    @Query("""
    SELECT e FROM Endpoint e
    LEFT JOIN FETCH e.subscribedEvents
    """)
    List<Endpoint> findAllWithSubscribedEvents();
}
