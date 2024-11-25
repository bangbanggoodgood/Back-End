package home.bangbanggoodgood.repository;

import home.bangbanggoodgood.domain.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterRepository extends JpaRepository<Cluster, Long> {

}
