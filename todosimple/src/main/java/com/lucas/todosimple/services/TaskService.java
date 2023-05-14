package com.lucas.todosimple.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lucas.todosimple.models.Task;
import com.lucas.todosimple.models.User;
import com.lucas.todosimple.repositories.TaskRepository;
import com.lucas.todosimple.services.exceptions.DataBindingViolationException;
import com.lucas.todosimple.services.exceptions.ObjectNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository; 

    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new ObjectNotFoundException( "Task não encontrada! Id: " + id + ", tipo: " + Task.class.getName()));
    }

    @Transactional
    public Task create(Task obj){
        User user = this.userService.findById(obj.getUser().getId());
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    public Task update(Task obj){
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public void delete(Long id){
        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possivel excluir pois há uma entidades relacionada!");
        }
    }

    public List<Task> findAllByUserId(Long userId){
        List<Task> tasks = this.taskRepository.findByUser_id(userId);
        return tasks;
    }


}
