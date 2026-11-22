package com.team1.conpick.data.collector;

import com.team1.conpick.youtube.YoutubeAccount;
import com.team1.conpick.youtube.YoutubeAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

//시계가 부르는 입구
//@Scheduled 메서드는 반환 타입 void, 매개 변수 없음 -> 시계가 부르는데 넘겨줄 값이 없으니까!
@Slf4j
@Component
@RequiredArgsConstructor
public class YoutubeCollectScheduler {

    private final YoutubeAccountRepository youtubeAccountRepository;
    private final YoutubeCollector youtubeCollector;

    @Scheduled(cron = "0 0 */12 * * *", zone = "Asia/Seoul")
    public void collectAll(){
        // 1. 모든 계정 가져오기 (힌트: JpaRepository가 기본으로 주는 메서드)
        List<YoutubeAccount> youtubeAccounts = youtubeAccountRepository.findAll();

        // 2. 성공/실패 개수 셀 변수 (선언만 하지 말고 0으로 초기화!)
        int success = 0;
        int failed = 0;

        // 3. for문: 계정마다 try { collect } catch (Exception e) { log.warn(...) }
        for (YoutubeAccount account : youtubeAccounts) {
            try{
                youtubeCollector.collect(account);
                success++;
            } catch (Exception e){
                failed++;
                log.warn("자동 수집 실패 : {}", account.getId(), e);
            }
        }
        // 4. 끝나면 log.info로 "자동 수집 완료 success=?, failed=?" 출력
        log.info("자동 수집 완료 success = {}, failed = {}", success, failed);
    }
}
