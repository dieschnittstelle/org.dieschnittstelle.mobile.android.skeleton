package org.dieschnittstelle.mobile.android.skeleton.model;

import android.content.Context;
import android.util.Log;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

public class RoomLocalTodoCRUDOperations implements ITodoCRUDOperations{

    @Dao
    public interface TodoDao{

        @Query("select * from todo")
        public List<Todo> readAll();

        @Query("select * from todo WHERE id == (:id)")
        public Todo readById(long id);

        @Insert
        public long create(Todo todo);

        @Update
        public void update(Todo todo);

        @Delete
        public void delete(Todo todo);
    }

    @Database(entities = {Todo.class}, version = 1)
    public static abstract class TodoDatabase extends RoomDatabase{
        public abstract TodoDao getDao();
    }

    private TodoDatabase db;

    public RoomLocalTodoCRUDOperations(Context context){
        db = Room.databaseBuilder(context, TodoDatabase.class, "todo").build();
        Log.i("RoomLocalTodoCRUDOperations", "db: " + db + ", of class: " + db.getClass());
        Log.i("RoomLocalTodoCRUDOperations", "dao: " + db.getDao());
    }

    @Override
    public Todo createTodo(Todo todo) {
        long id = db.getDao().create(todo);
        todo.setId(id);
        return todo;
    }

    @Override
    public List<Todo> readAllTodos() {
        return db.getDao().readAll();
    }

    @Override
    public Todo readTodo(long id) {
        return db.getDao().readById(id);
    }

    @Override
    public Todo updateTodo(Todo todoToBeUpdated) {
        db.getDao().update(todoToBeUpdated);
        return todoToBeUpdated;
    }

    @Override
    public boolean deleteTodo(long id) {
        db.getDao().delete(readTodo(id));
        return true;
    }
}
