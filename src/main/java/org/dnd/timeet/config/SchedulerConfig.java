package org.dnd.timeet.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerConfig {

    private final int poolSize = 10;

    // 스프링 컨테이너가 종료될 때 스케줄러 작업 종료 및 스레드 풀 정리
    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService taskScheduler() {
        return Executors.newScheduledThreadPool(poolSize);
    }
}

