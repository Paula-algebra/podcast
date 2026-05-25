package hr.algebra.podcast.repository;

import hr.algebra.podcast.entity.Episode;
import hr.algebra.podcast.enums.ListeningStatus;
import hr.algebra.podcast.enums.PodcastCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    @Query("""
        SELECT e FROM Episode e
        WHERE (:query IS NULL OR LOWER(e.title)     LIKE LOWER(CONCAT('%', :query, '%'))
                              OR LOWER(e.showName)  LIKE LOWER(CONCAT('%', :query, '%'))
                              OR LOWER(e.hosts)     LIKE LOWER(CONCAT('%', :query, '%'))
                              OR LOWER(e.guests)    LIKE LOWER(CONCAT('%', :query, '%'))
                              OR LOWER(e.mainTopic) LIKE LOWER(CONCAT('%', :query, '%')))
          AND (:category IS NULL OR e.category = :category)
          AND (:status IS NULL OR e.status = :status)
          AND (:subscribedOnly = false OR e.subscribed = true)
        ORDER BY e.status ASC, e.releaseDate DESC, e.showName ASC
        """)
    List<Episode> search(
        @Param("query") String query,
        @Param("category") PodcastCategory category,
        @Param("status") ListeningStatus status,
        @Param("subscribedOnly") boolean subscribedOnly
    );

    List<Episode> findAllByOrderByStatusAscReleaseDateDescShowNameAsc();
}
