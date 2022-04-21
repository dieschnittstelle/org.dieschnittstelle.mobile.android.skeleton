package org.dieschnittstelle.mobile.android;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.dieschnittstelle.mobile.android.skeleton.model.AppDatabase;
import org.dieschnittstelle.mobile.android.skeleton.model.Todo;
import org.dieschnittstelle.mobile.android.skeleton.model.TodoDao;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SimpleTodoReadWriteTest {

    private TodoDao todoDao;
    private AppDatabase db;

    @Before
    public void createDb() {
	Context context = ApplicationProvider.getApplicationContext();
	db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
	todoDao = db.getTodoDao();
    }

    @After
    public void closeDb() throws IOException {
	db.close();
    }


    @Test
    public void writeTodoAndReadInList() {
	Todo todo = new Todo();
	todo.setName("dgf");
	todoDao.insert(todo);
	List<Todo> byName = todoDao.findTodosByName("dfg");
	assertEquals(byName.get(0).getName(), "dfg");
	//assertThat(byName.get(0), equalTo(todo));
    }    
}
