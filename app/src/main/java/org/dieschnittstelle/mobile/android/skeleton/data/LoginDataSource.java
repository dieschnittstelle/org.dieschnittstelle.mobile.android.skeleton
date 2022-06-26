package org.dieschnittstelle.mobile.android.skeleton.data;

import android.app.AuthenticationRequiredException;
import android.util.Log;

import com.google.firebase.firestore.auth.User;

import org.dieschnittstelle.mobile.android.skeleton.data.model.LoggedInUser;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public interface UsersWebAPI{
        @PUT("/api/users/auth")
        public Call<Boolean> authenticate(@Body User user);
    }

    public class User implements Serializable {
        String pwd;
        String email;

        public User(String email, String pwd) {
            this.email = email;
            this.pwd = pwd;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public Result<LoggedInUser> login(String username, String password) {
        Log.i(LoginDataSource.class.getName(), "Auth Login: " + username + "/" + password);

        try {
            // TODO: handle loggedInUser authentication

            Future<Boolean> check = checkFromApi(username, password);

            if(check.get().booleanValue()) {
                LoggedInUser fakeUser =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                "Jane Doe");

                return new Result.Success<>(fakeUser);
            }else{
                return null;
            }
        } catch (Exception e) {
            Log.i(LoginDataSource.class.getName(), "ERROR AUTH" + Arrays.toString(e.getStackTrace()));

        }
        return new Result.Error(new IOException("Login Error logging in"));
    }

    public void logout() {
        // TODO: revoke authentication
    }

    private Future<Boolean> checkFromApi(String username, String password){
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new Thread(() -> {
            try {
                Retrofit apibase = new Retrofit.Builder()
                        .baseUrl("http://10.0.2.2:8080")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                UsersWebAPI webAPI = apibase.create(UsersWebAPI.class);
                var authenticate = webAPI.authenticate(new User(username, password));

                Boolean body = authenticate.execute().body();

                Thread.sleep(2000);
                if(body.booleanValue()){
                    Log.i(LoginDataSource.class.getName(), "Authenticated: ");
                    result.complete(true);
                }else{
                    Log.i(LoginDataSource.class.getName(), "AUTH :not " );
                    //return new Result.Error(new IOException("Login Error logging in"));
                    result.complete(false);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        return result;
    }
}