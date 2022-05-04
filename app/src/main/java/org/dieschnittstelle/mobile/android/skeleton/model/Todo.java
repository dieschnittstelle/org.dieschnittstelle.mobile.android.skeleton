package org.dieschnittstelle.mobile.android.skeleton.model;

import java.io.Serializable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// TODO get model from web app (sample web api)

@Entity
public class Todo implements Serializable{

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    private String description;

    private long expiry;

    private boolean done;

    private boolean favourite;

    public long getId(){
	return id;
    }

    public void setId(long id){
	this.id = id;
    }

    public String getName(){
	return name;
    }

    public void setName(String name){
	this.name = name;
    }

    public String getDescription(){
	return description;
    }

    public void setDescription(String description){
	this.description = description;
    }

    public long getExpiry(){
	return expiry;
    }

    public void setExpiry(long expiry){
	this.expiry = expiry;
    }

    public boolean isDone(){
	return done;
    }

    public void setDone(boolean done){
	this.done = done;
    }

    public boolean isFavourite(){
	return favourite;
    }

    public void setFavourite(boolean favourite){
	this.favourite = favourite;
    }

    public Todo(String name){
        this.name = name;
    }
}
