package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.List;

public interface ITodoCRUDOperations {

    // C: create
    public Todo createTodo(Todo todo);

    // R: read
    public List<Todo> readAllTodos();
    public Todo readTodo(long id);

    // U: update
    public Todo updateTodo(Todo todo);

    // D: delete
    public boolean deleteTodo(long id);

    // introducing remote here feels utterly wrong bc the interface shouldn't know anything
    // about implementation
    public boolean deleteAllTodos(boolean remote);
}
