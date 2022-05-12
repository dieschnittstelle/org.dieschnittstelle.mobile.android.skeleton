package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.ITodoCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.SimpleTodoCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.Todo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class DetailviewActivity extends AppCompatActivity {

    private static final String LOGGER = "DetailViewActivity";

    public static final String ARG_ITEM_ID = "itemId";

    private EditText itemNameText;
    private EditText itemDescriptionText;
    private CheckBox itemCheckedCheckbox;
    private FloatingActionButton saveItemButton;

    private Todo todo;
    private ActivityDetailviewBinding binding;

    private ITodoCRUDOperations crudOperations;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this,R.layout.activity_detailview);
        this.crudOperations = SimpleTodoCRUDOperations.getInstance();

        long todoId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
        this.todo = this.crudOperations.readTodo(todoId);

        if(todoId != -1){
            this.todo = this.crudOperations.readTodo(todoId);
        }

        Log.i(LOGGER, "showing detailview for todo: " + todo );

        if(todo == null){
            this.todo = new Todo();
        }

        //setViewModel generiert von variable aus layout
        this.binding.setViewmodel(this);
    }

    public Todo getItem(){
        return this.todo;
    }

    public void onSaveItem(){
        Intent returnIntent = new Intent();

        if(todo.getId() > 0 ){
            this.todo = crudOperations.updateTodo(this.todo );
        }else{
            this.todo = crudOperations.createTodo(this.todo);
        }

        returnIntent.putExtra(ARG_ITEM_ID, this.todo.getId());

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
