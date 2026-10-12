package com.team1.conpick.youtube;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class YoutubeStateStore {

    private final StringRedisTemplate redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(10);

    private String key(String state){
        return "youtube:state:" + state;
    }

    public void save(String state, Long userId){
        redisTemplate.opsForValue().set(key(state), String.valueOf(userId), TTL);
    }

    //redis에서 state값에 해당하는 userId 값 찾기.
    public Optional<Long> consume(String state){
        String value = redisTemplate.opsForValue().getAndDelete(key(state));
        //**value가 null일 수도 있으니 Optional로 감싸고, 값이 있으면 Long으로 바꿔라.
        return Optional.ofNullable(value).map(Long::valueOf);
    }
}
