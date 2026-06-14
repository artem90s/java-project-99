package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.LabelResponse;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
    private static final Long SIZE = 3L;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LabelMapper labelMapper;

    @Test
    @WithMockUser
    void getLabelsShouldReturnAllLabels() throws Exception {
        var labelsFromBd = labelRepository.findAll();
        var res = mockMvc.perform(get("/api/labels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("feature"))
                .andExpect(jsonPath("$[0].createdAt").exists()).andReturn().getResponse();
        List<LabelResponse> labels = objectMapper.readValue(res.getContentAsString(), new TypeReference<>() {
        });
        assertThat(labelsFromBd.size()).isEqualTo(labels.size());
        var actual = labelsFromBd.stream().map(labelMapper::toResponse).toList();
        Assertions.assertThat(labels).containsExactlyInAnyOrderElementsOf(actual);
    }

    @Test
    @WithMockUser
    void getLabelByIdShouldReturnLabel() throws Exception {
        mockMvc.perform(get("/api/labels/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("feature"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser
    void createLabelShouldPersistAndReturnLabel() throws Exception {
        LabelDto newLabel = new LabelDto();
        newLabel.setName("bugTest");

        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLabel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("bugTest"))
                .andExpect(jsonPath("$.createdAt").exists());
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
    }

    @Test
    @WithMockUser
    void updateLabelShouldModifyExistingLabel() throws Exception {
        LabelDto updateDto = new LabelDto();
        updateDto.setName("updated-feature");

        mockMvc.perform(put("/api/labels/{id}", SIZE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SIZE))
                .andExpect(jsonPath("$.name").value("updated-feature"))
                .andExpect(jsonPath("$.createdAt").exists());

        Label updated = labelRepository.findById(SIZE).orElseThrow();
        assertThat(updated.getName()).isEqualTo("updated-feature");
    }

    @Test
    @WithMockUser
    void deleteLabelShouldRemoveLabel() throws Exception {
        var labelForDelete = new LabelDto();
        labelForDelete.setName("for_delete");
        var res = mockMvc.perform(post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(labelForDelete)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(res).get("id").asLong();
        Assertions.assertThat(labelRepository.findById(id)).isPresent();

        mockMvc.perform(delete("/api/labels/{id}", id))
                .andExpect(status().isNoContent());
    }
}
