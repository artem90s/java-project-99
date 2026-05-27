package hexlet.code.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.UserDto;
import hexlet.code.app.dto.UserResponse;
import hexlet.code.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository repository;

    @Test
    @WithMockUser
    void getAllTestSuccess() throws Exception {
        var res = mockMvc.perform(get("/api/users")).andExpect(status().isOk()).andReturn().getResponse();
        List<UserResponse> users = mapper.readValue(res.getContentAsString(), new TypeReference<>() {
        });
        var usersFromDB = repository.findAll();
        assertThat(users.size()).isEqualTo(usersFromDB.size());
    }

    @Test
    @WithMockUser
    void createUserSuccess() throws Exception {
        var dto = createUser();
        var res = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))).andExpect(status().isCreated()).andReturn().getResponse();
        UserResponse response = mapper.readValue(res.getContentAsString(), new TypeReference<>() {
        });
        var user = repository.findById(response.getId()).orElse(null);
        assertNotNull(user);
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    @WithMockUser
    void createNewUserEmailException() throws Exception {
        var dto = createUser();
        dto.setEmail("not_email.ru");
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    void createNewUserPasswordException() throws Exception {
        var dto = createUser();
        dto.setPassword("q");
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    void updateUserSuccess() throws Exception {
        var dto = new UserDto();
        dto.setEmail("test@test.ru");
        dto.setPassword("123");
        dto.setFirstName("Test");
        var entity = repository.findById(1L).orElse(null);
        assertThat(entity.getEmail()).isEqualTo("hexlet@example.com");
        assertTrue(passwordEncoder.matches("qwerty", entity.getPassword()));
        var res = mockMvc.perform(put("/api/users/{id}", entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto))).andExpect(status().isOk())
                .andReturn().getResponse();
        UserResponse userResponse = mapper.readValue(res.getContentAsString(), new TypeReference<>() {
        });
        var entityAfter = repository.findById(1L).orElse(null);
        assertThat(userResponse.getEmail()).isEqualTo("test@test.ru");
        assertThat(entityAfter.getEmail()).isEqualTo(userResponse.getEmail());
        assertTrue(passwordEncoder.matches("123", entityAfter.getPassword()));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser
    void deleteUserSuccess() throws Exception {
        assertNotNull(repository.findById(1L).orElse(null));
        mockMvc.perform(delete("/api/users/{id}", 1L)
        ).andExpect(status().isOk());
        assertNull(repository.findById(1L).orElse(null));
    }

    private UserDto createUser() {
        UserDto dto = new UserDto();
        dto.setEmail("test@test.ru");
        dto.setPassword("qwerty");
        dto.setFirstName("Test");
        dto.setLastName("Ivanovich");
        return dto;
    }
}
