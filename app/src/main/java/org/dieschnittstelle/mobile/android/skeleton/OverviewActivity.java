package org.dieschnittstelle.mobile.android.skeleton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityOverviewListitemViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.ITodoCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.SimpleTodoCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.Todo;
import org.dieschnittstelle.mobile.android.skeleton.util.MADAsyncOperationRunner;
import org.dieschnittstelle.mobile.android.skeleton.util.MADAsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private static final String LOGGER = "OverviewActivity";
    private ViewGroup viewRoot;
    private FloatingActionButton addNewItemButton;
    private ProgressBar progressBar;
    private MADAsyncOperationRunner operationRunner;

    private ListView listView;
    private ArrayAdapter<Todo> listViewAdapter;
    private List<Todo> listViewItems = new ArrayList<>();

    private ITodoCRUDOperations crudOperations;

    private ActivityResultLauncher<Intent> detailviewForNewItemActivityLauncher;

    //private final List<Todo> todoList = new ArrayList<>();

    //private ListAdapter<Todo> listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_overview);


      /*  listView = findViewById(R.id.listView);

        listViewAdapter = new ToDoAdapter();
        listView.setAdapter(listViewAdapter);*/

        viewRoot = findViewById(R.id.viewRoot);
        listView = findViewById(R.id.listView);

        addNewItemButton = findViewById(R.id.fab);
        progressBar = findViewById(R.id.progressBar);
        operationRunner = new MADAsyncOperationRunner(this,progressBar);

        listViewAdapter = initializeListViewAdapter();
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener((AdapterView<?> adapterView, View view, int position, long id) -> {
                Todo selectedItem = listViewAdapter.getItem(position);
                this.onListitemSelected(selectedItem);
            });

        initializeActivityResultLaunchers();

        addNewItemButton.setOnClickListener(v -> {
            onAddNewItem();
        });

        crudOperations = SimpleTodoCRUDOperations.getInstance();

        operationRunner.run(
                // run the readAllTodos operation
                () -> crudOperations.readAllTodos(),
                // once the operation is done, process the items returned from it
                todos -> {
                    todos.forEach(todo -> this.addListitemView(todo));
                });

    }

    private ArrayAdapter<Todo> initializeListViewAdapter() {
        return new ArrayAdapter<>(this, R.layout.activity_overview_listitem_view, listViewItems) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View existingTodoView, @NonNull ViewGroup parent) {
                Log.i(LOGGER, "getView() for position" + position + ", where exisitingTodoView: " + existingTodoView);

                // 1. take the date to be shown
                Todo todo = super.getItem(position);

                // the data binding object to show the data
                ActivityOverviewListitemViewBinding todoBinding = existingTodoView != null
                        ? (ActivityOverviewListitemViewBinding) existingTodoView.getTag()
                        : DataBindingUtil.inflate(getLayoutInflater(),R.layout.activity_overview_listitem_view, null, false);

                // 2. get or create the view to show the data
//                ViewGroup itemView = (ViewGroup) (existingTodoView != null
//                        ? existingTodoView
//                        :  getLayoutInflater().inflate(R.layout.activity_overview_listitem_view, null));
                //2.2 read out the single view elements that will be used to show the data
//                TextView itemNameText = itemView.findViewById(R.id.itemName);
//                CheckBox itemCheckedCheckbox = itemView.findViewById(R.id.itemChecked);
                //3. bind the data to the view elements
//                itemNameText.setText(todo.getName());
//                itemCheckedCheckbox.setChecked(todo.isDone());
                todoBinding.setTodo(todo);

                //the view in which data is shown
                View todoView = todoBinding.getRoot();
                todoView.setTag(todoBinding);

                return todoView;
            }
        };
    }

    private void initializeActivityResultLaunchers(){
        detailviewForNewItemActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (result) -> {
                    Log.i(LOGGER, "resultCode: " + result.getResultCode());
                    Log.i(LOGGER, "data: " + result.getData());
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        long itemId = result.getData().getLongExtra(DetailviewActivity.ARG_ITEM_ID, -1);
                        this.operationRunner.run(
                                // call operation
                                () -> crudOperations.readTodo(itemId),
                                // use operation result
                                todo -> this.addListitemView(todo)
                        );
                    }
                });
    }

    private void addListitemView(Todo todo){
        //TextView listitemView = (TextView) getLayoutInflater().inflate(R.layout.activity_overview_listitem_view);
        //listitemView.setText(item);
        //listView.addView(listitemView);
        //listitemView.setOnClickListener(v -> onListitemSelected(((TextView)v).getText().toString()));
        listViewAdapter.add(todo);
        listView.setSelection(listViewAdapter.getPosition(todo));
    }

    private void onListitemSelected(Todo todo) {
        Intent detailviewIntent = new Intent(this, DetailviewActivity.class);
        detailviewIntent.putExtra(DetailviewActivity.ARG_ITEM_ID, todo.getId());
        Log.i(LOGGER, "calling detailview vor todo: " + todo );
        startActivity(detailviewIntent);
    }

    private static int CALL_DETAILVIEW_FOR_NEW_ITEM = 1;

    private void onAddNewItem(){
        Intent detailviewIntentForAddNewItem = new Intent(this, DetailviewActivity.class);
        detailviewForNewItemActivityLauncher.launch(detailviewIntentForAddNewItem);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CALL_DETAILVIEW_FOR_NEW_ITEM){
            if(resultCode == Activity.RESULT_OK){
                long todoId = data.getLongExtra(DetailviewActivity.ARG_ITEM_ID, -1);
                //showMessage("received: " + name);
                addListitemView(name);
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/

    private void showMessage(String msg){
        Snackbar.make(viewRoot, msg, Snackbar.LENGTH_INDEFINITE).show();
    }
}
