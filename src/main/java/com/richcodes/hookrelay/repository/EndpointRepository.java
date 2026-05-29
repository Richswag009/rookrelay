package com.richcodes.hookrelay.repository;

import com.richcodes.hookrelay.entities.Endpoint;
import com.richcodes.hookrelay.entities.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EndpointRepository extends JpaRepository<Endpoint,Long> {

    @Query("""
    SELECT e FROM Endpoint e
    LEFT JOIN FETCH e.subscribedEvents
    WHERE e.merchant = :merchant
""")
    List<Endpoint> findByMerchant(Merchant merchant);
}
