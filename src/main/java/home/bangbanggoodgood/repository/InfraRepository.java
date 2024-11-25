package home.bangbanggoodgood.repository;

import home.bangbanggoodgood.domain.Infras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InfraRepository extends JpaRepository<Infras, Long> {

    // dongCode를 Long으로 변환하여 조회
    @Query("SELECT i FROM Infras i WHERE i.id IN :dongCodes")
    List<Infras> findByDongCodeIn(@Param("dongCodes") List<Long> dongCodes);

    // 하나의 dongCode에 대해 인프라 정보를 조회
    @Query("SELECT i FROM Infras i WHERE i.id = :dongCode")
    Infras findByDongCode(@Param("dongCode") Long dongCode);

}
