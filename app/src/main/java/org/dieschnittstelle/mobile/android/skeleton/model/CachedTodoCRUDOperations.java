package org.dieschnittstelle.mobile.android.skeleton.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CachedTodoCRUDOperations implements ITodoCRUDOperations{

    private Map<Long, Todo> todoMap = new HashMap<>();
    private ITodoCRUDOperations realCrudOperations;

    public CachedTodoCRUDOperations(ITodoCRUDOperations realCrudOperations){
        this.realCrudOperations = realCrudOperations;
    }

    @Override
    public Todo createTodo(Todo todo){
        Todo create = realCrudOperations.createTodo(todo);
        todoMap.put(create.getId(), create);
        return todo;
    }

    @Override
    public List<Todo> readAllTodos() {
        Log.i("CachedTodoCRUDOperations", "read all todos from cached");
     if(todoMap.size() == 0 ){
         realCrudOperations.readAllTodos().forEach(todo -> todoMap.put(todo.getId(), todo));
     }

     return new ArrayList<>(todoMap.values());
    }

    @Override
    public Todo readTodo(long id) {
      if(!todoMap.containsKey(id)){
          Todo todo = realCrudOperations.readTodo(id);
          if(todo != null){
              todoMap.put(todo.getId(), todo);
          }
          return todo;
      }
      return todoMap.get(id);
    }

    @Override
    public Todo updateTodo(Todo todoToBeUpdated) {
        Todo updated = this.realCrudOperations.updateTodo(todoToBeUpdated);
        todoMap.put(todoToBeUpdated.getId(), updated);
        return updated;
    }

    @Override
    public boolean deleteTodo(long id) {
        if(this.realCrudOperations.deleteTodo(id)){
            todoMap.remove(id);
            return true;
        }else{
            return true;
        }
    }
}
