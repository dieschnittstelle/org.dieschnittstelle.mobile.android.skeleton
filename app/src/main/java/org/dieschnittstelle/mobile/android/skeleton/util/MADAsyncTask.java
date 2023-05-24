package org.dieschnittstelle.mobile.android.skeleton.util;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*
 * simplistic reconstruction of the basic AsyncTask functionality, which (unfortunately
 * from the point of view of the MAD course) will be deprecated in API 30
 *
 * based on https://medium.com/@bhojwaniravi/rxifying-asynctask-appetizer-for-rxandroid-beginners-bd483df40e78
 *
 * declares the three type parameters from AsyncTask to allow seamless refactoring,
 * but does not implement update functionality while the task is running (i.e. the Progress
 * type parameter is not used at all)
 *
 * given the MAD demos from S20, all references to AsyncTask can be replaced by references to this class
 * without losing functionality
 */
public abstract class MADAsyncTask<Params, Progress, Result> {

    protected static String logger = "MADAsyncTask";

    private static boolean verbose = true;

    /*
     * might be overridden by subclasses, e.g. for displaying a progress widget
     * (instances need to be run on the UI thread in this case)
     */
    protected void onPreExecute() {

    }

    /*
     * must be implemented by subclasses for doing something in the background (otherwise
     * it wouldn't make sense to use an async task at all...)
     */
    protected abstract Result doInBackground(Params... input);

    /*
     * should be overridden by subclasses, e.g. for providing a reaction to
     * the result of doInBackground()
     */
    protected void onPostExecute(Result result) {

    }

    /*
     * coordinates the background and foreground processes using rxjava / rxandroid
     */
    public void execute(Params... input) {
        if (verbose) {
            Log.d(logger, "execute(): calling onPreExecute() on thread: " + Thread.currentThread());
        }
        this.onPreExecute();

        Observable.create(
                        // this will allow that doInBackground() results will be passed to observers for reaction
                        new ObservableOnSubscribe<Result>() {
                            @Override
                            public void subscribe(ObservableEmitter<Result> emitter) {
                                if (!emitter.isDisposed()) {
                                    if (verbose) {
                                        Log.d(logger, "execute(): calling doInBackground() on thread: " + Thread.currentThread());
                                    }
                                    try {
                                        Result result = MADAsyncTask.this.doInBackground(input);
                                        if (verbose) {
                                            Log.d(logger, "execute(): obtained result from doInBackground(): " + result);
                                        }
                                        emitter.onNext(result);
                                    } catch (Exception e) {
                                        String msg = "execute(): got exception running doInBackground(): " + e;
                                        Log.e(logger, msg);
                                        throw new MADAsyncTaskException(msg, e);
                                    }
                                }
                            }
                        })
                // this results in that doInBackground() will be run on a javarx scheduler that uses background threads for io operations, see: http://reactivex.io/RxJava/javadoc/io/reactivex/schedulers/Schedulers.html
                .subscribeOn(Schedulers.io())
                // this will make sure that that the observer that reacts to doInBackground() results (see below) will be run on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                // this implements the reaction to receiving a doInBackground() result as a call to onPostExecute()
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    /*
                     * this method will receive the result from the background execution
                     */
                    @Override
                    public void onNext(Result result) {
                        if (verbose) {
                            Log.d(logger, "execute(): pass result to onPostExecute() on thread: " + Thread.currentThread());
                        }
                        MADAsyncTask.this.onPostExecute(result);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*
     * a runtime exception that will be thrown from within execute()
     */
    protected static class MADAsyncTaskException extends RuntimeException {
        public MADAsyncTaskException(String msg) {
            super(msg);
        }

        public MADAsyncTaskException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }


}
