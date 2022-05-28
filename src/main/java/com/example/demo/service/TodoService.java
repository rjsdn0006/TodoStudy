package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.TodoEntity;
import com.example.demo.persistence.TodoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TodoService {
	
	@Autowired
	private TodoRepository repository;

	// 공통으로 사용할 검증 메서드 
	private void validate(final TodoEntity entity) {
		if(entity == null) {
			log.warn("Entity cannot be null.");
			throw new RuntimeException("Entity cannot be null.");
		}
		if(entity.getUserId()==null) {
			log.warn("Unknown user.");
			throw new RuntimeException("UnKnown user.");
		}
	}
	
	// CREATE 
	public List<TodoEntity> create(final TodoEntity entity){
		validate(entity); // 검증 
		repository.save(entity); // 저장 
		log.info("Entity Id : {} is saved.", entity.getId()); // 로그남기기 
		return repository.findByUserId(entity.getUserId());
	}
	
	// RETRIEVE 
	public List<TodoEntity> retrieve(final String userId){
		return repository.findByUserId(userId);
	}
	
	// UPDATE 
	public List<TodoEntity> update(final TodoEntity entity){
		validate(entity); // 검증 
		final Optional<TodoEntity> original = repository.findById(entity.getId());
		original.ifPresent(todo -> {
			todo.setTitle(entity.getTitle());
			todo.setDone(entity.isDone());
			
			repository.save(todo);
		});
		
		return retrieve(entity.getUserId());
	}
	
	// DELETE
	public List<TodoEntity> delete(final TodoEntity entity){
		validate(entity);
		
		try {
			repository.delete(entity);
		}catch(Exception e) {
			log.error("error deleting entity ",entity.getId(), e);
			throw new RuntimeException("error deleting entity"+ entity.getId());
		}
		
		return retrieve(entity.getUserId());
	}
	
}
