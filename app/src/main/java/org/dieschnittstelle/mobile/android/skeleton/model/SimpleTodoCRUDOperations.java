package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleTodoCRUDOperations implements ITodoCRUDOperations{

    private static SimpleTodoCRUDOperations instance;

    private Map<Long, Todo> todoMap = new HashMap<>();
    private long idcount = 0;

    public static SimpleTodoCRUDOperations getInstance(){
        if(instance == null){
            instance = new SimpleTodoCRUDOperations();
        }

        return instance;
    }

    private SimpleTodoCRUDOperations(){
        Arrays.asList("lorem", "dopsum", "eler", "sed", "adipiscing").stream()
                .forEach(name -> this.createTodo(new Todo(name)));
    }

    @Override
    public Todo createTodo(Todo todo){
        todo.setId(++idcount);
        todoMap.put(todo.getId(), todo);
        return todo;
    }

    @Override
    public List<Todo> readAllTodos() {
        //TODO remove sleep
        try{
            Thread.sleep(1500);
        }catch (Exception e){

        }
        return new ArrayList<>(todoMap.values());
    }

    @Override
    public Todo readTodo(long id) {
        //TODO remove sleep
        try{
            Thread.sleep(1500);
        }catch (Exception e){

        }
        return todoMap.get(id);
    }

    @Override
    public Todo updateTodo(Todo todo) {
        return todoMap.put(todo.getId(), todo);
    }

    @Override
    public boolean deleteTodo(long id) {
        todoMap.remove(id);
        return true;
    }
}
