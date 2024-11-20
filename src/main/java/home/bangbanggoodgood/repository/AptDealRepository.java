package home.bangbanggoodgood.repository;

import home.bangbanggoodgood.domain.AptDeals;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AptDealRepository extends JpaRepository<AptDeals, Long> {
    @Query("SELECT CONCAT(CAST(ad.dealYear AS string), LPAD(CAST(ad.dealMonth AS string), 2, '0')) AS yearMonth, "
            + "AVG(CAST(REPLACE(ad.dealAmount, ',', '') AS double)) AS avgDealAmount "
            + "FROM AptDeals ad "
            + "WHERE ad.dealYear >= :startYear AND ad.dealYear <= :endYear "
            + "AND (ad.dealYear < :endYear OR ad.dealMonth <= :currentMonth) "
            + "AND ad.aptSeq = :aptSeq "
            + "GROUP BY ad.dealYear, ad.dealMonth "
            + "ORDER BY ad.dealYear DESC, ad.dealMonth DESC")
    List<Tuple> findAvgDealAmountByMonth(
            @Param("startYear") int startYear,
            @Param("endYear") int endYear,
            @Param("currentMonth") int currentMonth,
            @Param("aptSeq") String aptSeq);


    @Query("SELECT CONCAT(CAST(ad.dealYear AS string), LPAD(CAST(ad.dealMonth AS string), 2, '0')), "
            + "CAST(REPLACE(ad.dealAmount, ',', '') AS integer), "
            + "ad.excluUseAr, "
            + "ad.floor "
            + "FROM AptDeals ad "
            + "WHERE ad.aptSeq = :aptSeq "
            + "ORDER BY ad.dealYear DESC, ad.dealMonth DESC")
    List<Tuple> findDealsTable(@Param("aptSeq") String aptSeq);


}
