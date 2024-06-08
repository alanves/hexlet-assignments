package exercise.controller;

import org.junit.jupiter.api.Test;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import org.instancio.Instancio;
import org.instancio.Select;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import exercise.repository.TaskRepository;
import exercise.model.Task;

// BEGIN
import org.junit.jupiter.api.BeforeEach;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
// END
class ApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskRepository taskRepository;


    @Test
    public void testWelcomePage() throws Exception {
        var result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThat(body).contains("Welcome to Spring!");
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }


    // BEGIN
    private Task testTask;

    @BeforeEach
    public void setUp() {
        testTask = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .create();
        taskRepository.save(testTask);
    }

    @Test
    public void testShow() throws Exception {
        var result = mockMvc.perform(get("/tasks/{id}", testTask.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(testTask)))
                .andReturn();

        assertThatJson(result.getResponse().getContentAsString()).and(
                a -> a.node("title").isEqualTo(testTask.getTitle()),
                a -> a.node("description").isEqualTo(testTask.getDescription())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = new HashMap<>();
        var title = faker.lorem().word();
        var description = faker.lorem().paragraph();
        data.put("title", title);
        data.put("description", description);

        var request = post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }


    @Test
    public void testUpdate() throws Exception {
        var data = new HashMap<>();
        var title = faker.lorem().word();
        var description = faker.lorem().paragraph();
        data.put("title", title);
        data.put("description", description);

        var request = put("/tasks/{id}", testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var actualTask = taskRepository.findById(testTask.getId()).get();

        assertThat(actualTask.getTitle()).isEqualTo(title);
        assertThat(actualTask.getDescription()).isEqualTo(description);
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", testTask.getId()))
                .andExpect(status().isOk());

        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
    }
    // END
}
