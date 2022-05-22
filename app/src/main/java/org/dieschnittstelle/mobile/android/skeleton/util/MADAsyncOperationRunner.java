package org.dieschnittstelle.mobile.android.skeleton.util;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;

import java.util.function.Supplier;
import java.util.function.Consumer;

public class MADAsyncOperationRunner {

    private Activity owner;
    private ProgressBar progressBar;

    public MADAsyncOperationRunner(Activity owner, ProgressBar progressBar){
        this.owner = owner;
        this.progressBar = progressBar;
    }

    public <T> void run(Supplier<T> operation, Consumer<T> onOperationResult){
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }
        new Thread(() -> {
            T operationResult = operation.get();
            owner.runOnUiThread(() -> {
                onOperationResult.accept(operationResult);
                if(progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }).start();
    }
}
