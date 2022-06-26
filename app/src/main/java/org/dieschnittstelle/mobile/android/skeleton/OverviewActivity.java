package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityOverviewListitemViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.ITodoCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.Todo;
import org.dieschnittstelle.mobile.android.skeleton.ui.login.LoginActivity;
import org.dieschnittstelle.mobile.android.skeleton.util.MADAsyncOperationRunner;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingConversion;
import androidx.databinding.DataBindingUtil;

public class OverviewActivity extends AppCompatActivity {

    private static final String LOGGER = "OverviewActivity";

    public static  final Comparator<Todo> NAME_COMPARATOR = Comparator.comparing(Todo::getName);
    public static  final Comparator<Todo> CHECKED_AND_NAME_COMPARATOR = Comparator.comparing(Todo::isDone).reversed().thenComparing(Todo::getName); //TODO die erledigten sollen nach unten


    private ViewGroup viewRoot;
    private FloatingActionButton addNewItemButton;
    private ProgressBar progressBar;
    private MADAsyncOperationRunner operationRunner;

    private ListView listView;
    private ArrayAdapter<Todo> listViewAdapter;
    private List<Todo> listViewItems = new ArrayList<>();

    private ITodoCRUDOperations crudOperations;

    private ActivityResultLauncher<Intent> detailviewActivityLauncher;
    private ActivityResultLauncher<Intent> loginActivityLauncher;

    //private final List<Todo> todoList = new ArrayList<>();

    //private ListAdapter<Todo> listViewAdapter;

    private Comparator<Todo> currentComparator = NAME_COMPARATOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(((TodoApplication) getApplication()).isOnline()){
            this.loginActivityLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Log.i(LOGGER, "auth loa");
                        }
                    }
            );
            Intent loginIntent = new Intent(this, LoginActivity.class);

            loginActivityLauncher.launch(loginIntent);

        }

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

        addNewItemButton.setOnClickListener(v -> onAddNewItem());

        //crudOperations = SimpleTodoCRUDOperations.getInstance();
        //crudOperations = new RoomLocalTodoCRUDOperations(this.getApplicationContext()); //70:00
        //crudOperations = new RetrofitRemoteTodoCRUDOperations();
        crudOperations = ((TodoApplication) getApplication()).getCrudOperations();
        //TODO Retrofit aufrufen bzw. nach REQ implementieren (gleiches fuer Detail)


        syncAllTodos();

    }

    private void syncAllTodos() {
        Log.i("SYNC ", "syncAllTodos");
        operationRunner.run(
                // run the readAllTodos operation
                () -> crudOperations.readAllTodos(),
                // once the operation is done, process the items returned from it
                todos -> {
                    todos.forEach(this::addListitemView);
                    sortTodos(); //hier knallts wenn "Name" leer, validation greift noch nicht
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
                todoBinding.setController(OverviewActivity.this);

                //the view in which data is shown
                View todoView = todoBinding.getRoot();
                todoView.setTag(todoBinding);

                return todoView;
            }
        };
    }

    private void initializeActivityResultLaunchers(){
        detailviewActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (result) -> {
                    Log.i(LOGGER, "resultCode: " + result.getResultCode());
                    Log.i(LOGGER, "data: " + result.getData());
                    if(result.getResultCode() == DetailviewActivity.STATUS_CREATED || result.getResultCode() == DetailviewActivity.STATUS_UPDATED) {
                        long itemId = result.getData().getLongExtra(DetailviewActivity.ARG_ITEM_ID, -1);
                        this.operationRunner.run(
                                // call operation
                                () -> crudOperations.readTodo(itemId),
                                // use operation result
                                todo -> {
                                    if(result.getResultCode() == DetailviewActivity.STATUS_CREATED) {
                                        onTodoCreated(todo);
                                    }else if (result.getResultCode() == DetailviewActivity.STATUS_UPDATED){
                                        onTodoUpdated(todo);
                                    }
                                }
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
       //startActivity(detailviewIntent);
        detailviewActivityLauncher.launch(detailviewIntent);
    }

    //private static int CALL_DETAILVIEW_FOR_NEW_ITEM = 1;

    private void onAddNewItem(){
        Intent detailviewIntentForAddNewItem = new Intent(this, DetailviewActivity.class);
        detailviewActivityLauncher.launch(detailviewIntentForAddNewItem);
    }

    private void onTodoCreated(Todo todo){
        this.addListitemView(todo);
        sortTodos();
    }

    private void onTodoUpdated(Todo todo){
        Todo todoToBeUpdated= this.listViewAdapter.getItem(this.listViewAdapter.getPosition(todo));
        todoToBeUpdated.setName(todo.getName());
        todoToBeUpdated.setDescription(todo.getDescription());
        todoToBeUpdated.setDone(todo.isDone());
        todoToBeUpdated.setFavourite(todo.isFavourite());
        todoToBeUpdated.setExpiry(todo.getExpiry());
        //.... alle
        //this.listViewAdapter.notifyDataSetChanged();
        sortTodos();
    }

    private void showMessage(String msg){
        Snackbar.make(viewRoot, msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.sortList) {
            this.currentComparator = CHECKED_AND_NAME_COMPARATOR;
            sortTodos();
            return true;
        }else if(item.getItemId()==R.id.deleteAllItemsLocally) {
            deleteAll(false);
            return true;
        }else if (item.getItemId()==R.id.deleteAllItemsRemote) {
            deleteAll(true);
            return true;
        }else if(item.getItemId() == R.id.syncAllItems) {
            listViewItems.clear();
            syncAllTodos();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    public void sortTodos(){
        this.listViewItems.sort(this.currentComparator);
        this.listViewAdapter.notifyDataSetChanged();
    }

    public void onCheckedChangedInListView(Todo todo){
        this.operationRunner.run(
                () -> crudOperations.updateTodo(todo),
                updateditem -> {
                    onTodoUpdated(updateditem);
                    showMessage("Updated: " + updateditem.getName());
                }
        );
    }

    @BindingConversion
    public static String convertLongToFormattedDateString(long expiry){
        return expiry > 0
                ? DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(new Date(expiry))
                : null;
    }

    private void deleteAll(boolean remote){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Wirklich Löschen")
                .setNeutralButton("Cancel", null)
                .setPositiveButton("OK", (dialog, which) -> operationRunner.run(
                        () -> crudOperations.deleteAllTodos(remote),
                        success -> {
                            if(!remote){
                              listViewItems.clear();
                              listViewAdapter.notifyDataSetInvalidated();
                            }

                            showMessage(remote ? "Alle entfernten Todos gelöscht" : "Alle lokalen Todos gelöscht");
                        }
                ));
        builder.show();

    }
}
