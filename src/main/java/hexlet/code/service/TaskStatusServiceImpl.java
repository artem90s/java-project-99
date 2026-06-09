package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.TaskStatusDtoForUpdate;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {
    private final TaskStatusRepository repository;

    /**
     * Возвращает статус задачи по ID.
     *
     * @param id идентификатор статуса
     * @return статус задачи
     * @throws ResourceNotFoundException если статус не найден
     */
    public TaskStatus getStatusById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("not found status by id: " + id));
    }

    /**
     * Возвращает список всех статусов задач.
     *
     * @return список всех статусов
     */
    public List<TaskStatus> getStatuses() {
        return repository.findAll();
    }

    /**
     * Создает новый статус задачи.
     *
     * @param dto данные для создания статуса
     * @return созданный статус
     */
    @Transactional
    public TaskStatus createStatus(TaskStatusDto dto) {
        TaskStatus status = new TaskStatus();
        status.setName(dto.getName());
        status.setSlug(dto.getSlug());
        status.setCreatedAt(LocalDate.now());
        return repository.save(status);
    }

    /**
     * Обновляет существующий статус задачи.
     *
     * @param id  идентификатор обновляемого статуса
     * @param dto новые данные статуса (может содержать name или slug)
     * @return обновленный статус
     * @throws ResourceNotFoundException если статус не найден
     */
    @Transactional
    public TaskStatus updateStatus(Long id, TaskStatusDtoForUpdate dto) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("not found status by id: " + id));
        if (dto.getName() != null) {
            status.setName(dto.getName());
        }
        if (dto.getSlug() != null) {
            status.setSlug(dto.getSlug());
        }
        return repository.save(status);
    }

    /**
     * Удаляет статус задачи по ID.
     *
     * @param id идентификатор удаляемого статуса
     */
    @Transactional
    public void deleteStatus(Long id) {
        repository.deleteById(id);
    }
}
