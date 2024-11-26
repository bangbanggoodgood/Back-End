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
            + "CONCAT(:sido,' ',:gugun,' ',hi.roadNm,' ', hi.roadNmBonbun), "
            + "MAX(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)), "
            + "MIN(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)) "
            + "FROM AptDeals hd "
            + "JOIN AptInfos hi ON hd.aptSeq = hi.aptSeq "
            + "WHERE CONCAT(hi.sggCd, hi.umdCd) = :dongCode "
            + "AND hi.aptNm LIKE %:aptName% "
            + "GROUP BY hi.aptSeq, hi.aptNm, hi.buildYear, CONCAT(hi.sggCd, hi.umdCd) "
            + "HAVING MIN(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)) >= :minPrice "
            + "AND MAX(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)) <= :maxPrice")
    List<Tuple> findByDongAndAptName(@Param("sido") String sido,
                                     @Param("gugun") String gugun,
                                     @Param("dongCode") String dongCode,
                                     @Param("aptName") String aptName,
                                     @Param("minPrice") int minPrice,
                                     @Param("maxPrice") int maxPrice);



    @Query("SELECT hi.aptSeq, "
            + "hi.aptNm, "
            + "hi.buildYear, "
            + "MAX(hd.excluUseAr), "
            + "MIN(hd.excluUseAr), "
            + "CONCAT(:sido,' ',:gugun ,' ',hi.roadNm,' ', hi.roadNmBonbun), "
            + "MAX(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)), "
            + "MIN(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)) "
            + "FROM AptDeals hd "
            + "JOIN AptInfos hi ON hd.aptSeq = hi.aptSeq "
            + "WHERE CONCAT(hi.sggCd, hi.umdCd) = :dongCode "
            + "GROUP BY hi.aptSeq, hi.aptNm, hi.buildYear, CONCAT(hi.sggCd, hi.umdCd) "
            + "HAVING MIN(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)) >= :minPrice "
            + "AND MAX(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)) <= :maxPrice")
    List<Tuple> findByDong(@Param("sido") String sido,
                           @Param("gugun") String gugun,
                           @Param("dongCode") String dongCode,
                           @Param("minPrice") int minPrice,
                           @Param("maxPrice") int maxPrice);

    // 이거만 수정
    @Query("SELECT hi.aptSeq, "
            + "hi.aptNm, "
            + "hi.buildYear, "
            + "MAX(hd.excluUseAr), "
            + "MIN(hd.excluUseAr), "
            + "CONCAT(:sido,' ',:gugun, ' ' ,hi.roadNm,' ',hi.roadNmBonbun), "
            + "MAX(CAST(REPLACE(hd.dealAmount, ',', '') AS integer )),"
            + "MIN(CAST(REPLACE(hd.dealAmount, ',', '') AS integer )) "
            + "FROM AptDeals hd "
            + "JOIN AptInfos hi ON hd.aptSeq = hi.aptSeq "
            + "WHERE CONCAT(hi.sggCd, hi.umdCd) IN :dongCodes "
            + "GROUP BY hi.aptSeq, hi.aptNm, hi.buildYear, CONCAT(hi.sggCd, hi.umdCd) "
            + "HAVING MIN(CAST(REPLACE(hd.dealAmount, ',', '') AS integer )) >= :minPrice "
            + "AND MAX(CAST(REPLACE(hd.dealAmount, ',', '') AS integer )) <= :maxPrice")
    List<Tuple> findBySidoAndGugun(@Param("sido") String sido,
                                   @Param("gugun") String gugun,
                                   @Param("dongCodes") List<String> dongCodes,
                                   @Param("minPrice") int minPrice,
                                   @Param("maxPrice") int maxPrice);



    @Query("SELECT "
            + "hi.aptSeq, "
            + "hi.aptNm, "
            + "hi.buildYear, "
            + "MAX(hd.excluUseAr), "
            + "MIN(hd.excluUseAr), "
            + "CONCAT(:sido,' ',:gugun, ' ', hi.roadNm,' ', hi.roadNmBonbun), "
            + "MAX(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)), "
            + "MIN(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)) "
            + "FROM AptDeals hd "
            + "JOIN AptInfos hi ON hd.aptSeq = hi.aptSeq "
            + "WHERE CONCAT(hi.sggCd, hi.umdCd) IN :dongCodes "
            + "AND hi.aptNm LIKE %:aptName% "
            + "GROUP BY hi.aptSeq, hi.aptNm, hi.buildYear, CONCAT(hi.sggCd, hi.umdCd) "
            + "HAVING MIN(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)) >= :minPrice "
            + "AND MAX(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)) <= :maxPrice")
    List<Tuple> findBySidoAndGugunAndAptName(@Param("sido") String sido,
                                             @Param("gugun") String gugun,
                                             @Param("dongCodes") List<String> dongCodes,
                                             @Param("aptName") String aptName,
                                             @Param("minPrice") int minPrice,
                                             @Param("maxPrice") int maxPrice);


    AptInfos findByAptSeq(String aptSeq);


    @Query("SELECT "
            + "CONCAT(d.sidoName, ' ', d.gugunName, ' ', d.dongName)"
            + "FROM DongCode d "
            + "JOIN AptInfos hi ON hi.aptSeq = :aptSeq "
            + "WHERE d.dongCode = CONCAT(hi.sggCd, hi.umdCd)"
            )
    String findDongNameByAptSeq(@Param("aptSeq") String aptSeq);

    @Query("SELECT "
            + "d.dongCode "
            + "FROM DongCode d "
            + "JOIN AptInfos hi ON hi.aptSeq = :aptSeq "
            + "WHERE d.dongCode = CONCAT(hi.sggCd, hi.umdCd)"
    )
    String findDongCodeByAptSeq(@Param("aptSeq") String aptSeq);



    @Query("SELECT a.buildYear FROM AptInfos a WHERE a.aptSeq = :aptSeq")
    Integer findBuildYearByAptSeq(@Param("aptSeq") String aptSeq);

    @Query("SELECT a.comment FROM AptInfos a WHERE a.aptSeq = :aptSeq")
    String findCommentByAptSeq(@Param("aptSeq") String aptSeq);

    // comment가 null이 아닌 AptInfos 조회
    @Query("SELECT a FROM AptInfos a WHERE a.comment IS NOT NULL")
    List<AptInfos> findByCommentIsNotNull();

    @Query("SELECT i.cluster FROM Infras i JOIN AptInfos hi ON CONCAT(hi.sggCd, hi.umdCd) = CAST(i.id AS string) WHERE CONCAT(hi.sggCd, hi.umdCd) = :dongCode")
    Long findClusterNumByDongCode(@Param("dongCode") String dongCode);

    // 아파트의 최소 가격과 최대 가격을 조회하는 메서드
    @Query("SELECT "
            + "MIN(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)), "
            + "MAX(CAST(REPLACE(hd.dealAmount, ',', '') AS integer)) "
            + "FROM AptDeals hd "
            + "WHERE hd.aptSeq = :aptSeq")
    Tuple findPriceRangeByAptSeq(@Param("aptSeq") String aptSeq);

}