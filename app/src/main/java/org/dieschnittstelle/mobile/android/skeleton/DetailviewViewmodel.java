package org.dieschnittstelle.mobile.android.skeleton;

import android.view.View;

import org.dieschnittstelle.mobile.android.skeleton.model.Todo;

public interface DetailviewViewmodel {

    Todo getTodo();
    void onSaveItem();

    boolean checkFieldInputCompleted(View view, int actionId, boolean hasFocus, boolean isCalledFromFocusChange);

    String getErrorStatus();

    boolean onNameFieldInputChanged();
}
