package home.bangbanggoodgood.service;
import home.bangbanggoodgood.repository.InfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InfoService {

    private final InfoRepository repo;

    public List<String> findSido() {
        List<String> sidoList = repo.findAllSidoName();
        return sidoList;

    }

    public List<String> findGugun(String sido) {
        List<String> gugunList = repo.findGugunNameBySidoName(sido);
        return gugunList;
    }

    public List<String> findDong(String sido, String gugun) {
        List<String> dongList = repo.findDongNameBySidoNameAndGugunName(sido, gugun);
        return dongList;

    }
}
