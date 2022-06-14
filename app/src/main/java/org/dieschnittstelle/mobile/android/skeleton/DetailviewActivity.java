package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.app.Application;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.ITodoCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RoomLocalTodoCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.SimpleTodoCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.Todo;
import org.dieschnittstelle.mobile.android.skeleton.util.MADAsyncOperationRunner;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingConversion;
import androidx.databinding.DataBindingUtil;

public class DetailviewActivity extends AppCompatActivity {

    private static final String LOGGER = "DetailViewActivity";

    public static final String ARG_ITEM_ID = "itemId";

    public static int STATUS_CREATED = 42;
    public static int STATUS_UPDATED = 43;


    //private EditText itemNameText;
    //private EditText itemDescriptionText;
    //private CheckBox itemCheckedCheckbox;
    //private FloatingActionButton saveItemButton;

    private Todo todo;
    private ActivityDetailviewBinding binding;

    private MADAsyncOperationRunner operationRunner;
    private ITodoCRUDOperations crudOperations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this,R.layout.activity_detailview);
        //this.crudOperations = SimpleTodoCRUDOperations.getInstance();
        ///this.crudOperations = new RoomLocalTodoCRUDOperations(this.getApplicationContext()); //70:00
        this.crudOperations = ((TodoApplication) getApplication()).getCrudOperations();

        this.operationRunner = new MADAsyncOperationRunner(this, null);

        long todoId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
        //this.todo = this.crudOperations.readTodo(todoId);

        if(todoId != -1){
            operationRunner.run(
                    //operation
                    () -> this.crudOperations.readTodo(todoId),
                    //onOperationResult
                    todo -> {
                        this.todo = todo;
                        //this.binding.setTodo(this.todo);
                        this.binding.setController(this);
                    });
            //this.todo = this.crudOperations.readTodo(todoId);
        }

        Log.i(LOGGER, "showing detailview for todo: " + todo );

        if(todo == null){
            this.todo = new Todo();
        }

        this.binding.setController(this);
        //this.binding.setTodo(this.todo); //necessary 2505 16:30 controller viewmodel?
    }

    public Todo getTodo(){
        return this.todo;
    }

    public void onSaveItem(){
        Intent returnIntent = new Intent();

        int resultCode = todo.getId() > 0 ? STATUS_UPDATED : STATUS_CREATED;

        operationRunner.run(() ->
               todo.getId() > 0 ? crudOperations.updateTodo(todo) : crudOperations.createTodo(todo),
               todo -> {
                     this.todo = todo;
                     returnIntent.putExtra(ARG_ITEM_ID, this.todo.getId());
                     setResult(resultCode, returnIntent);
                     finish();
               });
    }

    public void onExpirySelected(){
        Log.i(LOGGER, "Expiry selected");
        final Calendar calendar = Calendar.getInstance ();
        calendar.setTimeInMillis(todo.getExpiry());

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(Calendar.YEAR, year);
                newDate.set(Calendar.MONTH, month);
                newDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
                String r = df.format(new Date(newDate.getTimeInMillis()));

                todo.setExpiry(newDate.getTimeInMillis());
                ((TextInputEditText) binding.getRoot().findViewById(R.id.itemExpiry)).setText(r);
            }
        }, calendar.get(Calendar.YEAR) , calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
