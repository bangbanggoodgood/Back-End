package home.bangbanggoodgood.repository;

import home.bangbanggoodgood.domain.HashTag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashTagsRepository extends JpaRepository<HashTag, Long> {

    @Query("SELECT h.hashtag FROM HashTag h WHERE h.cluster.id = :clusterId")
    List<String> findHashTagsById(@Param("clusterId") Long clusterId);

}

