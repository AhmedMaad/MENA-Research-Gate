package com.maad.menaresearchgate.data;

import com.google.android.gms.tasks.Task;

public interface GeneralUserHandler {

    <T> void onSuccess(Task<T> task);

    void onFailure();
}
