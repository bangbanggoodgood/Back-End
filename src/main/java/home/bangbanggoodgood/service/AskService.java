package home.bangbanggoodgood.service;

import home.bangbanggoodgood.domain.AptInfos;
import home.bangbanggoodgood.repository.AptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AskService {

    private final AptRepository aptRepository;
    private final IntroduceService introduceService;

    @Scheduled(cron = "0 0 0 * * 0", zone = "Asia/Seoul")
    private void getComment() {
        System.out.println("스케쥴러가 실행 되었습니다 !" + LocalDateTime.now());
        List<AptInfos> aptInfoList = aptRepository.findByCommentIsNotNull();
        for(AptInfos info : aptInfoList) {
            String newComment = introduceService.sendToGpt(info.getAptSeq());
            info.updateComment(newComment);
            aptRepository.save(info);
        }
    }
}
