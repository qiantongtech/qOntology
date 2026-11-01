package tech.qiantong.qontology.module.om.service.ai.aiModeling;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiStage.OmAiStageDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTask.OmAiTaskDO;

/** AI辅助建模解析事件发布器，解析线程直接向已连接页面推送事件。 */
@Component
public class OmAiModelingStreamPublisher {

    private final Map<Long, CopyOnWriteArrayList<Consumer<Map<String, Object>>>> listeners =
            new ConcurrentHashMap<>();

    /** 订阅指定任务事件，并返回取消订阅操作。 */
    public Runnable subscribe(Long taskId, Consumer<Map<String, Object>> listener) {
        listeners.computeIfAbsent(taskId, ignored -> new CopyOnWriteArrayList<>()).add(listener);
        return () -> unsubscribe(taskId, listener);
    }

    /** 推送任务状态变化。 */
    public void publishTask(OmAiTaskDO task) {
        publish(task.getId(), Map.of("eventType", "task", "task", task));
    }

    /** 推送单个阶段的状态、进度或模型流式内容变化。 */
    public void publishStage(OmAiStageDO stage) {
        publish(stage.getTaskId(), Map.of("eventType", "stage", "stage", stage));
    }

    private void publish(Long taskId, Map<String, Object> event) {
        CopyOnWriteArrayList<Consumer<Map<String, Object>>> taskListeners = listeners.get(taskId);
        if (taskListeners == null) {
            return;
        }
        for (Consumer<Map<String, Object>> listener : taskListeners) {
            try {
                listener.accept(event);
            } catch (RuntimeException ignored) {
                unsubscribe(taskId, listener);
            }
        }
    }

    private void unsubscribe(Long taskId, Consumer<Map<String, Object>> listener) {
        CopyOnWriteArrayList<Consumer<Map<String, Object>>> taskListeners = listeners.get(taskId);
        if (taskListeners == null) {
            return;
        }
        taskListeners.remove(listener);
        if (taskListeners.isEmpty()) {
            listeners.remove(taskId, taskListeners);
        }
    }
}
