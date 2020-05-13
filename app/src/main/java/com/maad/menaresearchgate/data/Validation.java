package com.maad.menaresearchgate.data;

public class Validation {

    /**
     * @return 0: if all the fields are empty or don't meet validity requirements
     * 1: if all the fields meet validity requirements
     */
    public int validateFields(String firstName, String lastName, String username, String email
            , String password) {
        if (firstName.equals("") || lastName.equals("") || username.equals("") || email.equals("")
                || username.length() < 4 || username.length() > 20 || password.equals(""))
            return 0;
        else
            return 1;
    }

    /**
     * @return 0: if all the fields are empty or don't meet validity requirements
     * 1: if all the fields meet validity requirements
     */
    public int validateFields(String email, String password) {
        if (email.equals("") || password.equals(""))
            return 0;
        else
            return 1;
    }


}
