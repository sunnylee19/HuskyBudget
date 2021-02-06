package com.slee_hdworak.huskybudget.data;

import android.app.Activity;

import androidx.room.Room;

import com.slee_hdworak.huskybudget.data.daos.RepoDatabase;
import com.slee_hdworak.huskybudget.data.model.User;

import org.signal.argon2.Argon2;
import org.signal.argon2.MemoryCost;
import org.signal.argon2.Type;
import org.signal.argon2.Version;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    Activity activity;

    public LoginDataSource(Activity _activity) {
        activity = _activity;
    }

    public Result<User> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            RepoDatabase db = Room.databaseBuilder(activity,
                    RepoDatabase.class, "HuskyBudget").allowMainThreadQueries().build(); //TODO: remove allowMainThreadQueries()
            User user;

            user = db.getUserDao().findUserByUsername(username);
            if(!Argon2.verify(user.passwordHash, password.getBytes(StandardCharsets.UTF_8))) {
                throw new Exception("Password incorrect");
            }
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public Result<User> register(String username, String firstName, String lastName, String password) {

        try {
            // TODO: handle loggedInUser authentication
            RepoDatabase db = Room.databaseBuilder(activity,
                    RepoDatabase.class, "HuskyBudget").allowMainThreadQueries().build(); //TODO: remove allowMainThreadQueries()
            User user;
            int userCount = db.getUserDao().getUserCountByUsername(username);

            if(userCount == 0) {
                Argon2 argon2 = new Argon2.Builder(Version.V13)
                        .type(Type.Argon2id)
                        .memoryCost(MemoryCost.MiB(32))
                        .parallelism(1)
                        .iterations(3)
                        .build();

                SecureRandom random = new SecureRandom();
                byte[] salt = new byte[20];
                random.nextBytes(salt);
                Argon2.Result result = argon2.hash(password.getBytes(StandardCharsets.UTF_8), salt);
                String encodedPasswordHash = result.getEncoded();

                user = new User(firstName, lastName, username, encodedPasswordHash);
                db.getUserDao().insert(user);
                user = db.getUserDao().findUserByUsername(username);
            } else {
                throw new Exception("User already exists");
            }
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error registering", e));
        }
    }

    public Result<User> edit(String username, String firstName, String lastName, String password) {

        try {
            // TODO: handle loggedInUser authentication
            RepoDatabase db = Room.databaseBuilder(activity,
                    RepoDatabase.class, "HuskyBudget").allowMainThreadQueries().build(); //TODO: remove allowMainThreadQueries()
            User user = db.getUserDao().findUserByUsername(username);

            Argon2 argon2 = new Argon2.Builder(Version.V13)
                    .type(Type.Argon2id)
                    .memoryCost(MemoryCost.MiB(32))
                    .parallelism(1)
                    .iterations(3)
                    .build();

            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[20];
            random.nextBytes(salt);
            Argon2.Result result = argon2.hash(password.getBytes(StandardCharsets.UTF_8), salt);
            String encodedPasswordHash = result.getEncoded();

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPasswordHash(encodedPasswordHash);

            db.getUserDao().update(user);
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error editing", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}