package home.bangbanggoodgood.repository;

import home.bangbanggoodgood.domain.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    // category와 subCategory를 기준으로 통계를 조회
    List<Statistics> findByCategory(String category);

    // category와 subCategory를 기준으로 하나의 통계를 조회 (update 등에서 사용 가능)
    Optional<Statistics> findByCategoryAndSubCategory(String category, String subCategory);
}


