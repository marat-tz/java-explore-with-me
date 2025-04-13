package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.EndpointHitStatsProjection;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select e.app as app, e.uri as uri, count(e.id) as hits " +
            "from EndpointHit as e " +
            "where e.timestamp between :start and :end " +
            "group by e.app, e.uri " +
            "order by count(e.id) desc")
    List<EndpointHitStatsProjection> findAllNotUrisFalseUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select e.app as app, e.uri as uri, count(e.id) as hits " +
            "from EndpointHit as e " +
            "where e.timestamp between :start and :end and e.uri in :uris " +
            "group by e.app, e.uri " +
            "order by count(e.id) desc")
    List<EndpointHitStatsProjection> findAllWithUrisFalseUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                                                @Param("uris") List<String> uris);

    @Query("select e.app as app, e.uri as uri, count(distinct e.ip) as hits " +
            "from EndpointHit as e " +
            "where e.timestamp between :start and :end " +
            "group by e.app, e.uri " +
            "order by count(distinct e.ip) desc")
    List<EndpointHitStatsProjection> findAllNotUrisTrueUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select e.app as app, e.uri as uri, count(distinct e.ip) as hits " +
            "from EndpointHit as e " +
            "where e.timestamp between :start and :end and e.uri in :uris " +
            "group by e.app, e.uri " +
            "order by count(distinct e.ip) desc")
    List<EndpointHitStatsProjection> findAllWithUrisTrueUnique(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                                               @Param("uris") List<String> uris);

}