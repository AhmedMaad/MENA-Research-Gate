package com.maad.menaresearchgate.data;

import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

public interface FacebookHandler {

    void onSuccess(LoginResult loginResult);

    void onCancel();

    void onFailure(FacebookException exception);

}
