package home.bangbanggoodgood.repository;

import home.bangbanggoodgood.domain.AptInfos;
import home.bangbanggoodgood.dto.AptResponseDto;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AptRepository extends JpaRepository<AptInfos, String> {

    @Query("SELECT "
            + "hi.aptSeq, "
            + "hi.aptNm, "
            + "hi.buildYear, "
            + "MAX(hd.excluUseAr), "
            + "MIN(hd.excluUseAr), "
            + "CONCAT(hi.roadNm,'', hi.roadNmBonbun), "
            + "MAX(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)),"
            + "MIN(CAST(REPLACE(hd.dealAmount, ',', '') AS integer))"
           // + "null,  "           // infra가 아직 처리되지 않았을 경우 빈 문자열 또는 null 처리
           // + "'No Comment', "  // comment 필드는 처리되지 않았을 경우 기본값
           // + "0"          // likeCount 필드는 기본값
            + "FROM AptDeals hd "
            + "JOIN AptInfos hi ON hd.aptSeq = hi.aptSeq "
            + "WHERE CONCAT(hi.sggCd, hi.umdCd) = :dongCode "
            + "AND hi.aptNm LIKE %:aptName% "
            + "GROUP BY hi.aptSeq, hi.aptNm, hi.buildYear, CONCAT(hi.sggCd, hi.umdCd)")
    List<Tuple> findByDongAndAptName(@Param("dongCode") String dongCode, @Param("aptName") String aptName);


    @Query("SELECT hi.aptSeq, "
            + "hi.aptNm, "
            + "hi.buildYear, "
            + "MAX(hd.excluUseAr), "
            + "MIN(hd.excluUseAr), "
            + "CONCAT(hi.roadNm,'', hi.roadNmBonbun), "
            + "MAX(CAST(REPLACE(hd.dealAmount, ',', '') AS integer )),"
            + "MIN(CAST(REPLACE(hd.dealAmount, ',', '') AS integer ))"
            //+ "null,  "           // infra가 아직 처리되지 않았을 경우 빈 문자열 또는 null 처리
           // + "'No Comment', "  // comment 필드는 처리되지 않았을 경우 기본값
          //  + "0"          // likeCount 필드는 기본값
            + "FROM AptDeals hd "
            + "JOIN AptInfos hi ON hd.aptSeq = hi.aptSeq "
            + "WHERE CONCAT(hi.sggCd, hi.umdCd) = :dongCode "
            + "GROUP BY hi.aptSeq, hi.aptNm, hi.buildYear, CONCAT(hi.sggCd, hi.umdCd)")
    List<Tuple> findByDong(@Param("dongCode") String dongCode);


}
