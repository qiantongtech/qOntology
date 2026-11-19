package tech.qiantong.qontology.module.om.service.ai.aiModeling;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/** AI 建模专用执行器，隔离长耗时模型调用与系统公共线程池。 */
@Configuration
@EnableConfigurationProperties(OmAiModelingProperties.class)
public class OmAiModelingExecutorConfig {

    @Bean(name = "aiModelingTaskExecutor")
    public ThreadPoolTaskExecutor aiModelingTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        // 保留一个消费线程提交位，覆盖上一线程刚退出时的新任务入队竞态。
        executor.setQueueCapacity(1);
        executor.setThreadNamePrefix("ai-modeling-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }

    @Bean(name = "aiModelingStreamExecutor")
    public ThreadPoolTaskExecutor aiModelingStreamExecutor(OmAiModelingProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(properties.getStreamMaxConcurrency());
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("ai-modeling-stream-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(false);
        return executor;
    }
}
