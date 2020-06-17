package com.maad.menaresearchgate.data;

import com.google.android.gms.tasks.Task;

public interface GeneralHandler {

    <T> void onSuccess(Task<T> task);

    void onFailure(Exception e);
}
