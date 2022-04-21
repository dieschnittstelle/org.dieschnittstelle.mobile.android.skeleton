package org.dieschnittstelle.mobile.android.skeleton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.model.Todo;
import org.dieschnittstelle.mobile.android.skeleton.view.ToDoAdapter;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private ViewGroup viewRoot;
    private TextView welcomeText;
    private ViewGroup listView;
    private FloatingActionButton addNewItemButton;

    //private final List<Todo> todoList = new ArrayList<>();

    //private ListView listView;
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

        addNewItemButton.setOnClickListener(v -> {
            onAddNewItem();
        });

        Arrays.asList("lorem", "dopsum", "eler", "sed", "adispiscing").forEach(item -> {
            TextView listitemView = (TextView) getLayoutInflater().inflate(R.layout.activity_overview_listitem_view, null);
            listitemView.setText(item);
            listView.addView(listitemView);
            listitemView.setOnClickListener(v -> onListitemSelected(((TextView) v).getText().toString()));
        });
    }

    private void onListitemSelected(String item) {
        Intent detailviewIntent = new Intent(this, DetailviewActivity.class);
        detailviewIntent.putExtra(DetailviewActivity.ARG_ITEM, item);
        startActivity(detailviewIntent);
    }

    private void onAddNewItem(){
        Intent detailviewIntentForAddNewItem = new Intent(this, DetailviewActivity.class);
        startActivity(detailviewIntentForAddNewItem);
    }

    private void showMessage(String msg){
        Snackbar.make(viewRoot, msg, Snackbar.LENGTH_INDEFINITE).show();
    }
}
