package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.List;

public class SyncedTodoCRUDOperations implements ITodoCRUDOperations{

    private ITodoCRUDOperations localOperations;
    private ITodoCRUDOperations remoteOperations;

    public SyncedTodoCRUDOperations(ITodoCRUDOperations localOperations, ITodoCRUDOperations remoteOperations){
        this.localOperations = localOperations;
        this.remoteOperations = remoteOperations;
    }

    @Override
    public Todo createTodo(Todo todo) {
        Todo created = localOperations.createTodo(todo);
        remoteOperations.createTodo(created);
        return created;
    }

    @Override
    public List<Todo> readAllTodos() {
        List<Todo> todos = localOperations.readAllTodos();
        if(todos.size()==0){
            todos = remoteOperations.readAllTodos();
            todos.forEach(todo -> localOperations.createTodo(todo));
        }else{
            remoteOperations.deleteAllTodos(true);
            todos.forEach(todo -> remoteOperations.createTodo(todo));
        }
        return todos;
    }

    @Override
    public Todo readTodo(long id) {
        return localOperations.readTodo(id);
    }

    @Override
    public Todo updateTodo(Todo todoToBeUpdated) {
        Todo updated = localOperations.updateTodo(todoToBeUpdated);
        remoteOperations.updateTodo(updated);
        return updated;
    }

    @Override
    public boolean deleteTodo(long id) {
        if(localOperations.deleteTodo(id)){
            return remoteOperations.deleteTodo(id);
        }
        else{
            return false;
        }
    }

    @Override
    public boolean deleteAllTodos(boolean remote){
        if(!remote){
            return localOperations.deleteAllTodos(remote);
        }else{
            return remoteOperations.deleteAllTodos(remote);
        }
    }
}
