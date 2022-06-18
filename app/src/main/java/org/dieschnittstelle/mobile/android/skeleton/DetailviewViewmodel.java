package org.dieschnittstelle.mobile.android.skeleton;

import android.view.View;

import org.dieschnittstelle.mobile.android.skeleton.model.Todo;

public interface DetailviewViewmodel {

    public Todo getTodo();
    public void onSaveItem();

    public boolean checkFieldInputCompleted(View view, int actionId, boolean hasFocus, boolean isCalledFromFocusChange);

    public String getErrorStatus();

    public boolean onNameFieldInputChanged();
}
