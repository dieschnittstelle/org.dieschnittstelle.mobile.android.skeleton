package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.TimeFormat;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.ITodoCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.Todo;
import org.dieschnittstelle.mobile.android.skeleton.util.MADAsyncOperationRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class DetailviewActivity extends AppCompatActivity implements DetailviewViewmodel{

    private static final String LOGGER = "DetailViewActivity";

    public static final String ARG_ITEM_ID = "itemId";

    public static int STATUS_CREATED = 42;
    public static int STATUS_UPDATED = 43;


    String errorStatus = null;
    //private EditText itemNameText;
    //private EditText itemDescriptionText;
    //private CheckBox itemCheckedCheckbox;
    //private FloatingActionButton saveItemButton;

    private Todo todo;
    private ActivityDetailviewBinding binding;

    private MADAsyncOperationRunner operationRunner;
    private ITodoCRUDOperations crudOperations;

    private ActivityResultLauncher<Intent> selectContactLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this,R.layout.activity_detailview);

        this.selectContactLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        onContactSelected(result.getData());
                    }
                }
        );
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

        //MaterialDatePicker.Builder.datePicker().setTitleText("sdf").build().getDialog().show();

        /*TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newTime = Calendar.getInstance();
                newTime.setTimeInMillis(todo.getExpiry());
                newTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                newTime.set(Calendar.MINUTE, minute);

                todo.setExpiry(newTime.getTimeInMillis());
                String r = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, TimeFormat.CLOCK_24H).format(newTime);
                ((TextInputEditText) binding.getRoot().findViewById(R.id.itemExpiry)).setText(r);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);*/

        //TODO trennen von expiry in transient date und time...
        //timePickerDialog.show();
        //DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
        //android.text.format.DateUtils.formatDateTime(this, newDate.getTimeInMillis(), );
        //String r = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, TimeFormat.CLOCK_24H).format(newDate);
        //String r = df.format(new Date(newDate.getTimeInMillis()));

        //todo.setExpiry(newDate.getTimeInMillis());
        //((TextInputEditText) binding.getRoot().findViewById(R.id.itemExpiry)).setText(r);
    }

    @Override
    public boolean checkFieldInputCompleted(View v, int actionId, boolean hasFocus, boolean isCalledOnFocusChange){
        Log.i(LOGGER, "checkFieldInputCompleted" + v + ", ");
        if(isCalledOnFocusChange ? !hasFocus
                : (actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_NEXT)){
            if(todo.getName().length() < 5) {
                errorStatus = "Name to short!";
                this.binding.setController(this);
            }
        }
        return false;
    }

    public String getErrorStatus(){
        return errorStatus;
    }

    @Override
    public boolean onNameFieldInputChanged(){
        Log.i(LOGGER, "onNameFieldInput(): error status is currently: " + this.errorStatus);
        if(this.errorStatus != null) {
            this.errorStatus = null;
            this.binding.setController(this);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.detailview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.selectContact){
            selectContact();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    public void selectContact(){
        Log.i(LOGGER, "selectContact");
        Intent selectContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        this.selectContactLauncher.launch(selectContactIntent);
    }

    public void onContactSelected(Intent resultData){
        Log.i(LOGGER, "onContactSelected(): " + resultData);
        //showContactDetails(resultData.getData());
    }
}
