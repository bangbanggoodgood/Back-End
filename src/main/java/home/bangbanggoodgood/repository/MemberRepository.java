package home.bangbanggoodgood.repository;

import home.bangbanggoodgood.domain.Members;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Members, Long> {
    Members findMemberById(Long id);

    Members findBySocialId(Long socialId);
}
