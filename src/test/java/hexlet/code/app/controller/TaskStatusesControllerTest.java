package hexlet.code.app.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusesControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private TaskStatusRepository repository;

    @Test
    void getAllStatusesFailedUnauthorized() throws Exception {
        mockMvc.perform(get("/api/task_statuses")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getAllStatuses() throws Exception {
        var res = mockMvc.perform(get("/api/task_statuses")).andExpect(status().isOk()).andReturn().getResponse();
        List<TaskStatus> statuses = mapper.readValue(res.getContentAsString(), new TypeReference<>() {
        });
        var statusesDb = repository.findAll();
        assertThat(statusesDb.size()).isEqualTo(statuses.size());
    }

    @Test
    @WithMockUser
    void createStatus() throws Exception {
        TaskStatusDto dto = createStatusDto();
        var res = mockMvc.perform(post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))).andExpect(status().isCreated()).andReturn().getResponse();

        TaskStatus createdStatus = mapper.readValue(res.getContentAsString(), new TypeReference<>() {
        });
        var status = repository.findById(createdStatus.getId()).orElse(null);
        assertNotNull(status);
        Assertions.assertThat(status.getSlug()).isEqualTo(dto.getSlug());
    }

    @Test
    @WithMockUser
    void createStatusValidateSlugFailed() throws Exception {
        TaskStatusDto dto = createStatusDto();
        dto.setSlug("");
        mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    void createStatusValidateNameFailed() throws Exception {
        TaskStatusDto dto = createStatusDto();
        dto.setName("");
        mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    void updateStatus() throws Exception {
        TaskStatus status = repository.getTaskStatusBySlug("draft").get();
        TaskStatusDto dto = new TaskStatusDto();
        dto.setSlug("oneTwo");
        var res = mockMvc.perform(put("/api/task_statuses/{id}", status.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk()).andReturn().getResponse();
        TaskStatus result = mapper.readValue(res.getContentAsString(), new TypeReference<>() {
        });
        TaskStatus after = repository.findById(1L).orElseThrow();
        assertThat(after.getSlug()).isEqualTo("oneTwo");
        assertThat(result.getSlug()).isEqualTo(after.getSlug());
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser
    void deleteSuccess() throws Exception {
        TaskStatus status = repository.findById(1L).orElseThrow();
        mockMvc.perform(delete("/api/task_statuses/{id}", status.getId()))
                .andExpect(status().isOk());
    }

    private TaskStatusDto createStatusDto() {
        TaskStatusDto dto = new TaskStatusDto();
        dto.setName("name");
        dto.setSlug("slug");
        return dto;
    }
}
