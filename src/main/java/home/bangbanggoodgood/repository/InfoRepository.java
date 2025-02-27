package home.bangbanggoodgood.repository;

import home.bangbanggoodgood.domain.DongCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfoRepository extends JpaRepository<DongCode, String> {
    @Query("SELECT DISTINCT d.sidoName FROM DongCode d")
    List<String> findAllSidoName();

    @Query("SELECT DISTINCT d.gugunName FROM DongCode d WHERE d.sidoName = :sido")
    List<String> findGugunNameBySidoName(@Param("sido") String sido);

    @Query("SELECT d.dongName FROM DongCode d WHERE d.sidoName = :sido AND d.gugunName = :gugun")
    List<String> findDongNameBySidoNameAndGugunName(@Param("sido") String sido, @Param("gugun") String gugun);

    @Query("SELECT d.dongCode FROM DongCode d WHERE d.sidoName = :sido AND d.gugunName = :gugun AND d.dongName = :dong")
    String findIdBySidoNameAndGugunName(@Param("sido") String sidoName, @Param("gugun") String gugunName, @Param("dong") String dongName);

    @Query("SELECT d.dongCode FROM DongCode d WHERE d.sidoName = :sido And d.gugunName = :gugun")
    List<String> findDongCodesBySidoNameAndGugunName(@Param("sido") String sido, @Param("gugun") String gugun);

    @Query("SELECT d.sidoName From DongCode d WHERE d.dongCode = :dongCode ")
    String findSidoNameByDongCode(@Param("dongCode") String dongCode);

    @Query("SELECT d.gugunName FROM DongCode d WHERE d.sidoName = :sidoName AND d.dongCode = :dongCode")
    String findGugunNameByDongCodeAndSidoName(@Param("sidoName") String sidoName, @Param("dongCode") String dongCode);

}
