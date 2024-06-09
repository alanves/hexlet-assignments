package exercise.controller;

import java.util.List;

import exercise.dto.TaskCreateDTO;
import exercise.dto.TaskDTO;
import exercise.dto.TaskUpdateDTO;
import exercise.mapper.TaskMapper;
import exercise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import exercise.exception.ResourceNotFoundException;
import exercise.repository.TaskRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
public class TasksController {
    // BEGIN
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskMapper taskMapper;

    @GetMapping(path = "")
    public List<TaskDTO> index() {
        var tasks = taskRepository.findAll();
        return tasks.stream()
                .map(p -> taskMapper.map(p))
                .toList();
    }

    @GetMapping(path = "/{id}")
    public TaskDTO show(@PathVariable long id) {

        var task =  taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        var taskDTO = taskMapper.map(task);
        return taskDTO;
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@RequestBody TaskCreateDTO taskData) {
        var task = taskMapper.map(taskData);
        taskRepository.save(task);
        var taskDto = taskMapper.map(task);
        return taskDto;
    }

    @PutMapping(path = "/{id}")
    public TaskDTO update(@PathVariable long id, @Valid @RequestBody TaskUpdateDTO taskData) {
        var assigneeId = taskData.getAssigneeId();

        var task =  taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));

        taskMapper.update(taskData, task);

        var assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        task.setAssignee(assignee);
        taskRepository.save(task);

        return taskMapper.map(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
    // END
}
