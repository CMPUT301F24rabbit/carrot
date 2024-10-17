package com.example.goldencarrot.data.model.user;

public class UserImpl implements User{
    private  String email;
    private  String userType;
    private String username;
    private UserUtils userUtils;

    public UserImpl(){}

    public UserImpl(final String email, final String userType, final String username) throws Exception{
        //validateUserType(userType);
        this.email = email;
        this.userType = userType;
        this.username = username;

    }

    public String getEmail() {
        return this.email;
    }

    public String getUserType() {
        return this.userType;
    }

    public String getUsername() {
        return this.username;
    }

    private void validateUserType(String userType) throws Exception {
        if (!UserUtils.validUserTypes.contains(userType)){
            throw UserUtils.invalidUserTypeException;
        }
    }
}
