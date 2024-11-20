package home.bangbanggoodgood.repository;

import home.bangbanggoodgood.domain.Likes;
import home.bangbanggoodgood.domain.Members;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByMemberAndAptInfo_AptSeq(Members member, String aptSeq);
}
