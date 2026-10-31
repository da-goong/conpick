package com.team1.conpick.data.collector;

import com.team1.conpick.youtube.YoutubeAccount;
import com.team1.conpick.youtube.YoutubeAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube")
public class YoutubeCollectController {

    private final YoutubeCollector youtubeCollector;
    private final YoutubeAccountRepository youtubeAccountRepository;

    @PostMapping("/collect")
    public ResponseEntity<Map<String, Integer>> collect(@AuthenticationPrincipal Long userId){

        List<YoutubeAccount> youtubeAccountList = youtubeAccountRepository.findByUserId(userId);

        int success = 0;
        int fail = 0;

        for (YoutubeAccount youtubeAccount : youtubeAccountList) {
            try{
                youtubeCollector.collect(youtubeAccount);
                success++;
            } catch (Exception e){
                log.warn("수집 실패 accountId={}", youtubeAccount.getId(), e);
                fail++;
            }
        }

        return ResponseEntity.ok(Map.of("success", success, "failed", fail));
    }
}
