package org.dieschnittstelle.mobile.android.skeleton.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.dieschnittstelle.mobile.android.skeleton.R;
import org.dieschnittstelle.mobile.android.skeleton.model.Todo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<Todo> localTodoList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ListView listView;

        public ViewHolder(View view) {
            super(view);
            listView = (ListView) view.findViewById(R.id.listView);
        }

        public ListView getListView() {
            return listView;
        }
    }

    public ToDoAdapter(List<Todo> todoList){
        localTodoList = todoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                //.inflate(R.layout.todo_row_item, parent, false);
        .inflate(R.layout.activity_overview_listitem_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getListView().getAdapter();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

}
