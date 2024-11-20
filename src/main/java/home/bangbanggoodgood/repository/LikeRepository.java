package home.bangbanggoodgood.repository;

import home.bangbanggoodgood.domain.Likes;
import home.bangbanggoodgood.domain.Members;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByMemberAndAptInfo_AptSeq(Members member, String aptSeq);

    @Query("SELECT hi.aptSeq, "
            + "hi.aptNm, "
            + "hi.buildYear, "
            + "MAX(hd.excluUseAr), "
            + "MIN(hd.excluUseAr), "
            + "CONCAT(hi.roadNm,' ', hi.roadNmBonbun), "
            + "MAX(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)), "
            + "MIN(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)) "
            + "FROM AptDeals hd "
            + "JOIN AptInfos hi ON hd.aptSeq = hi.aptSeq "
            + "JOIN Likes l ON l.aptInfo = hi "
            + "WHERE l.member.id = :memberId "
            + "GROUP BY hi.aptSeq, hi.aptNm, hi.buildYear, CONCAT(hi.sggCd, hi.umdCd) ")
    List<Tuple> findAptInfosByMemberId(@Param("memberId") Long memberId);
}