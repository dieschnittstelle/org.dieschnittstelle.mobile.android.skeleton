package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.dieschnittstelle.mobile.android.skeleton.model.Todo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetailviewActivity extends AppCompatActivity {

    public static final String ARG_ITEM = "item";

    private EditText itemNameText;
    private EditText itemDescriptionText;
    private FloatingActionButton saveItemButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailview);

        itemNameText = findViewById(R.id.itemName);
        itemDescriptionText = findViewById(R.id.itemDescription);
        saveItemButton = findViewById(R.id.fab);

        saveItemButton.setOnClickListener(v -> onSaveItem());

        Todo item = (Todo) getIntent().getSerializableExtra(ARG_ITEM);

        if(item != null){
            itemNameText.setText(item.getName());
            itemDescriptionText.setText(item.getDescription());
        }
    }

    private void onSaveItem(){
        Intent returnIntent = new Intent();
        String name = itemNameText.getText().toString();
        String description = itemDescriptionText.getText().toString();
        Todo item = new Todo(name);
        item.setDescription(description);

        returnIntent.putExtra(ARG_ITEM, item);

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
