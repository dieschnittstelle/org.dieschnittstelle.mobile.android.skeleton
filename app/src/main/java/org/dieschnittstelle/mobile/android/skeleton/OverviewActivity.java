package org.dieschnittstelle.mobile.android.skeleton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.model.Todo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private static final String LOGGER = "OverviewActivity";
    private ViewGroup viewRoot;
    private TextView welcomeText;
    private FloatingActionButton addNewItemButton;

    private ListView listView;
    private ArrayAdapter<Todo> listViewAdapter;
    private List<Todo> listViewItems = new ArrayList<>();

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

        Arrays.asList("lorem", "dopsum", "eler", "sed", "adipiscing").stream()
                .map(name -> new Todo(name))
                .forEach(item ->addListitemView(item));
    }

    private ArrayAdapter<Todo> initializeListViewAdapter() {
        return new ArrayAdapter<>(this, R.layout.activity_overview_listitem_view, listViewItems) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // 1. take the date to be shown
                Todo todo = super.getItem(position);
                // 2. create the view to show the data
                ViewGroup itemView = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_overview_listitem_view, null);
                //2.2 read out the single view elements that will be used to show the data
                TextView itemNameText = itemView.findViewById(R.id.itemName);
                CheckBox itemCheckedCheckbox = itemView.findViewById(R.id.itemChecked);
                //3. bind the data to the view elements
                itemNameText.setText(todo.getName());
                itemCheckedCheckbox.setChecked(todo.isDone());

                return itemView;
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
                        Todo item = (Todo) result.getData().getSerializableExtra(DetailviewActivity.ARG_ITEM);
                        addListitemView(item);
                    }
                });
    }

    private void addListitemView(Todo item){
        //TextView listitemView = (TextView) getLayoutInflater().inflate(R.layout.activity_overview_listitem_view);
        //listitemView.setText(item);
        //listView.addView(listitemView);
        //listitemView.setOnClickListener(v -> onListitemSelected(((TextView)v).getText().toString()));
        listViewAdapter.add(item);
    }

    private void onListitemSelected(Todo item) {
        Intent detailviewIntent = new Intent(this, DetailviewActivity.class);
        detailviewIntent.putExtra(DetailviewActivity.ARG_ITEM, item);
        startActivity(detailviewIntent);
    }

    private static int CALL_DETAILVIEW_FOR_NEW_ITEM = 1;

    private void onAddNewItem(){
        Intent detailviewIntentForAddNewItem = new Intent(this, DetailviewActivity.class);
        //startActivityForResult(detailviewIntentForAddNewItem, CALL_DETAILVIEW_FOR_NEW_ITEM);
        detailviewForNewItemActivityLauncher.launch(detailviewIntentForAddNewItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CALL_DETAILVIEW_FOR_NEW_ITEM){
            if(resultCode == Activity.RESULT_OK){
                Todo name = (Todo) data.getSerializableExtra(DetailviewActivity.ARG_ITEM);
                //showMessage("received: " + name);
                addListitemView(name);
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showMessage(String msg){
        Snackbar.make(viewRoot, msg, Snackbar.LENGTH_INDEFINITE).show();
    }
}
