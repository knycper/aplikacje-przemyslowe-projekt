package com.example.demo.controller.view;

import com.example.demo.domain.dto.Task.TaskReceiveDTO;
import com.example.demo.domain.dto.Task.TaskResponseDTO;
import com.example.demo.domain.enums.Status;
import com.example.demo.service.CategoryService;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    private final TaskService taskService;
    private final CategoryService categoryService;

    public TaskViewController(TaskService taskService,
                              CategoryService categoryService) {
        this.taskService = taskService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String tasksPage(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) UUID categoryId,
            Model model) {

        Page<TaskResponseDTO> page = taskService.getTasks(
                null,
                status,
                categoryId,
                null,
                Pageable.unpaged()
        );

        model.addAttribute("tasks", page.getContent());
        model.addAttribute("categories", categoryService.getCategories());
        model.addAttribute("statuses", Status.values());

        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCategory", categoryId);

        return "tasks";
    }

    @GetMapping("/add")
    public String addTaskForm(Model model) {
        model.addAttribute("task", new TaskReceiveDTO());
        model.addAttribute("categories", categoryService.getCategories());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("edit", false);
        return "task-form";
    }

    @PostMapping("/add")
    public String addTask(
            @Valid @ModelAttribute("task") TaskReceiveDTO task,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getCategories());
            model.addAttribute("statuses", Status.values());
            model.addAttribute("edit", false);
            return "task-form";
        }

        taskService.addTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String editTaskForm(@PathVariable UUID id, Model model) {

        TaskResponseDTO task = taskService.getTaskById(id);

        TaskReceiveDTO dto = new TaskReceiveDTO();
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setDueDate(task.getDueDate());
        dto.setCategoryId(
                task.getCategory() != null ? task.getCategory().getId() : null
        );

        model.addAttribute("task", dto);
        model.addAttribute("categories", categoryService.getCategories());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("taskId", id);
        model.addAttribute("edit", true);

        return "task-form";
    }

    @PostMapping("/edit/{id}")
    public String updateTask(
            @PathVariable UUID id,
            @Valid @ModelAttribute("task") TaskReceiveDTO task,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getCategories());
            model.addAttribute("statuses", Status.values());
            model.addAttribute("taskId", id);
            model.addAttribute("edit", true);
            return "task-form";
        }

        taskService.updateTask(task, id);
        return "redirect:/tasks";
    }
}
