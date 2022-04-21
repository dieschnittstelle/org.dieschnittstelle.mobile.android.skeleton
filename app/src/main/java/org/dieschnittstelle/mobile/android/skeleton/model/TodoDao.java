package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface TodoDao {

    @Insert
    void insert(Todo todo);

    @Delete
    void delete(Todo todo);

    @Query("SELECT * FROM Todo")
    List<Todo> getAll();

    @Query("SELECT * FROM Todo WHERE name LIKE :name")
    List<Todo> findTodosByName(String name);
    
}
