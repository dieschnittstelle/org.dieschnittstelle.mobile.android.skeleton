package org.dieschnittstelle.mobile.android.skeleton.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;   
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Database(entities = {Todo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TodoDao getTodoDao();
    
}
