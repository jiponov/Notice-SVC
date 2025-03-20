package app.repository;

import app.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, UUID> {

    Optional<Notice> findByGameIdAndUserId(UUID gameId, UUID userId);

    List<Notice> findByUserId(UUID userId);


}