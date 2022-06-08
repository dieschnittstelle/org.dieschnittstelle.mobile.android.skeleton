package org.dieschnittstelle.mobile.android.skeleton.model;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class RetrofitRemoteTodoCRUDOperations implements ITodoCRUDOperations{

    public interface TodosWebAPI{

        @POST("/api/todos")
        public Call<Todo> createTodo(@Body Todo todo);

        @GET("/api/todos")
        public Call<List<Todo>> readAllTodos();

        @GET("/api/todos/{todoId}")
        public Call<Todo> readTodo(@Path("todoId") long id);

        @PUT("/api/todos/{todoId}")
        public Call<Todo> updateTodo(@Path("todoId") long id, @Body Todo todo);

        @DELETE("/api/todos/{todoId}")
        public Call<Boolean> deleteTodo(@Path("todoId") long id);
    }

    private TodosWebAPI webAPI;

    public RetrofitRemoteTodoCRUDOperations(){
        Retrofit apibase = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webAPI = apibase.create(TodosWebAPI.class);
    }

    @Override
    public Todo createTodo(Todo todo) {
        try {
            return webAPI.createTodo(todo).execute().body();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Todo> readAllTodos() {
        try {
            return webAPI.readAllTodos().execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Todo readTodo(long id) {
        try {
            return webAPI.readTodo(id).execute().body();
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public Todo updateTodo(Todo todo) {
        try{
            return webAPI.updateTodo(todo.getId(), todo).execute().body();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteTodo(long id) {
        try{
            return webAPI.deleteTodo(id).execute().body();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
