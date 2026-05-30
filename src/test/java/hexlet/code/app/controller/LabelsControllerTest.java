package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LabelsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private Label label;

    @BeforeEach
    void setUp() {
        labelRepository.deleteAll();

        label = new Label();
        label.setName("feature");
        label.setCreatedAt(LocalDate.now());
        label = labelRepository.save(label);
    }

    @Test
    @WithMockUser
    void getLabelsShouldReturnAllLabels() throws Exception {
        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(label.getId()))
                .andExpect(jsonPath("$[0].name").value("feature"))
                .andExpect(jsonPath("$[0].createdAt").exists());
    }

    @Test
    @WithMockUser
    void getLabelsShouldReturnEmptyListWhenNoLabels() throws Exception {
        labelRepository.deleteAll();

        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser
    void getLabelByIdShouldReturnLabel() throws Exception {
        mockMvc.perform(get("/api/labels/{id}", label.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(label.getId()))
                .andExpect(jsonPath("$.name").value("feature"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser
    void createLabelShouldPersistAndReturnLabel() throws Exception {
        LabelDto newLabel = new LabelDto();
        newLabel.setName("bug");

        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLabel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("bug"))
                .andExpect(jsonPath("$.createdAt").exists());

        assertThat(labelRepository.findAll()).hasSize(2);

        Label savedLabel = labelRepository.findAll().get(1);
        assertThat(savedLabel.getName()).isEqualTo("bug");
        assertThat(savedLabel.getCreatedAt()).isNotNull();
    }

    @Test
    @WithMockUser
    void createLabelShouldReturn400WhenNameIsNull() throws Exception {
        LabelDto invalidLabel = new LabelDto();
        invalidLabel.setName(null);

        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLabel)))
                .andExpect(status().isBadRequest());

        assertThat(labelRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser
    void createLabelShouldReturn400WhenNameIsEmpty() throws Exception {
        LabelDto invalidLabel = new LabelDto();
        invalidLabel.setName("");

        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLabel)))
                .andExpect(status().isBadRequest());

        assertThat(labelRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser
    void updateLabelShouldModifyExistingLabel() throws Exception {
        LabelDto updateDto = new LabelDto();
        updateDto.setName("updated-feature");

        mockMvc.perform(put("/api/labels/{id}", label.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(label.getId()))
                .andExpect(jsonPath("$.name").value("updated-feature"))
                .andExpect(jsonPath("$.createdAt").exists());

        Label updated = labelRepository.findById(label.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("updated-feature");
        assertThat(updated.getCreatedAt()).isEqualTo(label.getCreatedAt());
    }

    @Test
    @WithMockUser
    void updateLabelShouldReturn400WhenNameIsNull() throws Exception {
        LabelDto invalidDto = new LabelDto();
        invalidDto.setName(null);

        mockMvc.perform(put("/api/labels/{id}", label.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        Label unchanged = labelRepository.findById(label.getId()).orElseThrow();
        assertThat(unchanged.getName()).isEqualTo("feature");
    }

    @Test
    @WithMockUser
    void deleteLabelShouldRemoveLabel() throws Exception {
        assertThat(labelRepository.findById(label.getId())).isPresent();

        mockMvc.perform(delete("/api/labels/{id}", label.getId()))
                .andExpect(status().isOk());

        assertThat(labelRepository.findById(label.getId())).isEmpty();
        assertThat(labelRepository.findAll()).hasSize(0);
    }

    @Test
    @WithMockUser
    void deleteLabelShouldNotDeleteOtherLabels() throws Exception {
        Label secondLabel = new Label();
        secondLabel.setName("bug");
        secondLabel.setCreatedAt(LocalDate.now());
        secondLabel = labelRepository.save(secondLabel);

        assertThat(labelRepository.findAll()).hasSize(2);

        mockMvc.perform(delete("/api/labels/{id}", label.getId()))
                .andExpect(status().isOk());

        assertThat(labelRepository.findById(label.getId())).isEmpty();
        assertThat(labelRepository.findById(secondLabel.getId())).isPresent();
        assertThat(labelRepository.findAll()).hasSize(1);
    }
}
