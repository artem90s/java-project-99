package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskResponse;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TasksControllerTest {
    private static final int SIZE = 4;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository statusRepository;
    @Autowired
    private LabelRepository labelRepository;
    private TaskStatus testStatus;
    private User testUser;
    private Task testTask;
    private Task secondTask;
    private Task thirdTask;
    private Label label;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();

        testStatus = statusRepository.getTaskStatusBySlug("draft")
                .orElseThrow(() -> new RuntimeException("Status not found"));

        testUser = userRepository.findByEmail("hexlet@example.com")
                .orElseThrow(() -> new RuntimeException("User not found"));

        label = labelRepository.findByName("feature")
                .orElseThrow(() -> new RuntimeException("Label not found"));

        // Получаем другие статусы для тестов
        TaskStatus toReviewStatus = statusRepository.getTaskStatusBySlug("to_review")
                .orElseThrow(() -> new RuntimeException("Status to_review not found"));

        TaskStatus toBeFixedStatus = statusRepository.getTaskStatusBySlug("to_be_fixed")
                .orElseThrow(() -> new RuntimeException("Status to_be_fixed not found"));

        // Получаем другие метки
        Label bugLabel = labelRepository.findByName("bug")
                .orElseThrow(() -> new RuntimeException("Label bug not found"));

        // Создаем тестовую задачу для каждого теста
        testTask = new Task();
        testTask.setName("create new feature");
        testTask.setIndex(1);
        testTask.setDescription("Create new feature implementation");
        testTask.setTaskStatus(testStatus);
        testTask.setAssignee(testUser);
        testTask.setLabels(Set.of(label));
        testTask.setCreatedAt(LocalDate.now());
        testTask = taskRepository.save(testTask);

        // Создаем вторую задачу для тестов фильтрации
        secondTask = new Task();
        secondTask.setName("create documentation");
        secondTask.setIndex(2);
        secondTask.setDescription("Write API documentation");
        secondTask.setTaskStatus(toReviewStatus);
        secondTask.setAssignee(testUser);
        secondTask.setLabels(Set.of(bugLabel));
        secondTask.setCreatedAt(LocalDate.now());
        secondTask = taskRepository.save(secondTask);

        // Создаем третью задачу (не должна попадать в фильтры)
        thirdTask = new Task();
        thirdTask.setName("update existing feature");
        thirdTask.setDescription("Update feature implementation");
        thirdTask.setTaskStatus(toBeFixedStatus);
        thirdTask.setAssignee(null); // Без исполнителя
        thirdTask.setLabels(Set.of(bugLabel));
        thirdTask.setCreatedAt(LocalDate.now());
        thirdTask = taskRepository.save(thirdTask);
    }

    @Test
    @WithMockUser
    void testGetTasksWithAllFilters() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks")
                        .param("titleCont", "create")
                        .param("assigneeId", testUser.getId().toString())
                        .param("status", "draft")
                        .param("labelId", label.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        TaskResponse[] tasks = objectMapper.readValue(result.getResponse().getContentAsString(), TaskResponse[].class);

        assertThat(tasks).hasSize(1);
        assertThat(tasks[0].getTitle()).isEqualTo("create new feature");
        assertThat(tasks[0].getStatus()).isEqualTo("draft");
        assertThat(tasks[0].getAssigneeId()).isEqualTo(testUser.getId());
        assertThat(tasks[0].getTaskLabelIds()).contains(label.getId());
    }

    @Test
    @WithMockUser
    void getTaskByIdShouldReturnTaskWhenTaskExists() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("create new feature"))
                .andExpect(jsonPath("$.content").value("Create new feature implementation"))
                .andExpect(jsonPath("$.index").value(1))
                .andExpect(jsonPath("$.status").value("draft"));
    }

    @Test
    @WithMockUser
    void getAllTasksShouldReturnAllTasks() throws Exception {

        Task anotherTask = new Task();
        anotherTask.setName("Another Task");
        anotherTask.setIndex(2);
        anotherTask.setTaskStatus(testStatus);
        anotherTask.setCreatedAt(LocalDate.now());
        taskRepository.save(anotherTask);

        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(SIZE))
                .andExpect(jsonPath("$[0].title").exists());
    }

    @Test
    @WithMockUser
    void getAllTasksShouldReturnEmptyListWhenNoTasks() throws Exception {
        taskRepository.deleteAll();

        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "0"))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    void createTaskShouldCreateAndReturnTask() throws Exception {
        TaskDto newTask = new TaskDto();
        newTask.setTitle("New Task");
        newTask.setContent("New Description");
        newTask.setStatus("draft");
        newTask.setAssigneeId(testUser.getId());

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.content").value("New Description"))
                .andExpect(jsonPath("$.status").value("draft"));

        assertThat(taskRepository.count()).isEqualTo(SIZE);
    }

    @Test
    @WithMockUser
    void createTaskShouldCreateTaskWithDifferentStatus() throws Exception {
        TaskDto newTask = new TaskDto();
        newTask.setTitle("Task with Review Status");
        newTask.setContent("Review description");
        newTask.setStatus("to_review");
        newTask.setAssigneeId(testUser.getId());

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("to_review"));
    }

    @Test
    @WithMockUser
    void createTaskShouldCreateTaskWithoutAssignee() throws Exception {
        TaskDto newTask = new TaskDto();
        newTask.setTitle("Task Without Assignee");
        newTask.setContent("No assignee");
        newTask.setStatus("draft");
        newTask.setAssigneeId(null);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Task Without Assignee"));
    }

    @Test
    @WithMockUser
    void updateTaskShouldUpdateExistingTask() throws Exception {
        TaskDto updateDto = new TaskDto();
        updateDto.setTitle("Updated Task Name");
        updateDto.setContent("Updated Description");
        updateDto.setStatus("to_publish");
        updateDto.setAssigneeId(testUser.getId());

        mockMvc.perform(put("/api/tasks/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task Name"))
                .andExpect(jsonPath("$.content").value("Updated Description"))
                .andExpect(jsonPath("$.status").value("to_publish"));

        Task updatedTask = taskRepository.findById(testTask.getId()).orElseThrow();
        assertThat(updatedTask.getName()).isEqualTo("Updated Task Name");
        assertThat(updatedTask.getTaskStatus().getSlug()).isEqualTo("to_publish");
    }

    @Test
    @WithMockUser
    void updateTaskShouldUpdateOnlyProvidedFields() throws Exception {
        TaskDto partialUpdate = new TaskDto();
        partialUpdate.setTitle("Only Name Updated");
        partialUpdate.setStatus("to_review");

        mockMvc.perform(put("/api/tasks/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Only Name Updated"))
                .andExpect(jsonPath("$.content").value("Create new feature implementation"))
                .andExpect(jsonPath("$.status").value("to_review"));
    }

    @Test
    @WithMockUser
    void updateTaskShouldRemoveAssigneeWhenAssigneeIdIsNull() throws Exception {
        assertThat(testTask.getAssignee()).isNotNull();

        TaskDto updateDto = new TaskDto();
        updateDto.setTitle("New title");
        updateDto.setStatus("draft");
        updateDto.setAssigneeId(null);

        mockMvc.perform(put("/api/tasks/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        Task updatedTask = taskRepository.findById(testTask.getId()).orElseThrow();
        assertThat(updatedTask.getName()).isEqualTo(updateDto.getTitle());
    }

    @Test
    @WithMockUser
    void updateTaskShouldReturnBadRequestWhenStatusDoesNotExist() throws Exception {
        TaskDto updateDto = new TaskDto();
        updateDto.setStatus("draft");
        updateDto.setStatus("non-existent-status");

        mockMvc.perform(put("/api/tasks/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteTaskShouldDeleteExistingTask() throws Exception {
        assertThat(taskRepository.existsById(testTask.getId())).isTrue();

        mockMvc.perform(delete("/api/tasks/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(testTask.getId())).isFalse();
    }
}
