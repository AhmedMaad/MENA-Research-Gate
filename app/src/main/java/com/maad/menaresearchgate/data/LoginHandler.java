package com.maad.menaresearchgate.data;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public interface LoginHandler {

    <T> void onSuccess(Task<T> task);

    void onFailure(Exception e);

}
