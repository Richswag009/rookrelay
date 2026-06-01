package com.richcodes.hookrelay.repository;

import com.richcodes.hookrelay.domain.Event;
import com.richcodes.hookrelay.domain.Merchant;
import com.richcodes.hookrelay.response.EventResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {

    @Query("""
    SELECT e FROM Event e
    WHERE e.merchant = :merchant
""")
    List<Event> fetchByMerchant(@Param("merchant") Merchant merchant);

}
