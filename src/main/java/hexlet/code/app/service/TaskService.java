package hexlet.code.app.service;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.dto.TaskParams;
import hexlet.code.app.dto.TaskResponse;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskStatusRepository statusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final TaskMapper mapper;

    /**
     * Возвращает задачу по ID.
     *
     * @param id идентификатор задачи
     * @return DTO задачи
     * @throws ResourceNotFoundException если задача не найдена
     */
    public TaskResponse getTaskById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("not found task by id: " + id));
        return mapper.toDto(task);
    }

    /**
     * Возвращает список задач с применением фильтрации.
     *
     * @param params параметры фильтрации (название, исполнитель, статус, метка)
     * @return список DTO задач, соответствующих фильтрам
     */
    public List<TaskResponse> getAllTasks(TaskParams params) {
        Specification<Task> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (params.getLabelId() != null) {
                Join<Task, Label> labels = root.join("labels");
                predicates.add(cb.equal(
                        labels.get("id"),
                        params.getLabelId()));
                query.distinct(true);
            }
            if (params.getAssigneeId() != null) {
                predicates.add(cb.equal(
                        root.get("assignee").get("id"),
                        params.getAssigneeId()
                ));
            }
            if (params.getStatus() != null) {
                predicates.add(cb.equal(
                        root.get("taskStatus").get("slug"),
                        params.getStatus()
                ));
            }
            if (params.getTitleCont() != null) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + params.getTitleCont().toLowerCase() + "%"
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        List<Task> tasks = taskRepository.findAll(spec);
        return tasks.stream().map(mapper::toDto).toList();
    }

    /**
     * Создает новую задачу.
     *
     * @param dto данные для создания задачи
     * @return DTO созданной задачи
     * @throws ResourceNotFoundException если статус, исполнитель или метки не найдены
     */
    @Transactional
    public TaskResponse createTask(TaskDto dto) {
        Task task = mapper.toEntity(dto);
        TaskStatus status = statusRepository.getTaskStatusBySlug(dto.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException("not found status : " + dto.getStatus()));
        task.setTaskStatus(status);
        if (dto.getAssigneeId() != null) {
            User user = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("not found id: " + dto.getAssigneeId()));
            task.setAssignee(user);
        }
        if (dto.getTaskLabelIds() != null && !dto.getTaskLabelIds().isEmpty()) {
            List<Label> labels = labelRepository.findAllById(dto.getTaskLabelIds());
            task.setLabels(labels);
        }
        task.setCreatedAt(LocalDate.now());
        taskRepository.save(task);
        return mapper.toDto(task);
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param id  идентификатор обновляемой задачи
     * @param dto новые данные задачи
     * @return DTO обновленной задачи
     * @throws ResourceNotFoundException если задача, статус или исполнитель не найдены
     */
    @Transactional
    public TaskResponse updateTask(Long id, TaskDto dto) throws ResourceNotFoundException {
        Task fromDb = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("not found task by id: " + id));
        Task update = mapper.toEntity(dto);
        TaskStatus status = statusRepository.getTaskStatusBySlug(dto.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException("not found status: " + dto.getStatus()));
        fromDb.setTaskStatus(status);
        if (dto.getAssigneeId() != null) {
            var user = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("not found id: " + dto.getAssigneeId()));
            fromDb.setAssignee(user);
        } else {
            fromDb.setAssignee(null);
        }
        if (dto.getTaskLabelIds() != null && !dto.getTaskLabelIds().isEmpty()) {
            List<Label> labels = labelRepository.findAllById(dto.getTaskLabelIds());
            fromDb.setLabels(labels);
        } else {
            fromDb.setLabels(null);
        }
        if (dto.getTitle() != null) {
            fromDb.setName(update.getName());
        }
        if (dto.getIndex() != null) {
            fromDb.setIndex(update.getIndex());
        }
        if (dto.getContent() != null) {
            fromDb.setDescription(update.getDescription());
        }
        taskRepository.save(fromDb);
        return mapper.toDto(fromDb);
    }

    /**
     * Удаляет задачу по ID.
     *
     * @param id идентификатор удаляемой задачи
     */
    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
