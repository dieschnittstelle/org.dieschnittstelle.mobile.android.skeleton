package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.Todo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class DetailviewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";

    private EditText itemNameText;
    private EditText itemDescriptionText;
    private CheckBox itemCheckedCheckbox;
    private FloatingActionButton saveItemButton;

    private Todo item;
    private ActivityDetailviewBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this,R.layout.activity_detailview);

        this.item = (Todo) getIntent().getSerializableExtra(ARG_ITEM);
        if(item == null){
            this.item = new Todo();
        }

        //setViewModel generiert von variable aus layout
        this.binding.setViewmodel(this);
    }

    public Todo getItem(){
        return this.item;
    }

    public void onSaveItem(){
        Intent returnIntent = new Intent();

        returnIntent.putExtra(ARG_ITEM, item);

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
