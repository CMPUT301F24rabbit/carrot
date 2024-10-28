package com.example.goldencarrot.data.model.user;

import java.util.Optional;

public class UserImpl implements User{
    private  String email;
    private  String userType;
    private String name;
    private Optional<String> phoneNumber;

    public UserImpl(){}

    public UserImpl(final String email,
                    final String userType,
                    final String name,
                    final Optional<String> phoneNumber) throws Exception{
        validateUserType(userType);
        this.email = email;
        this.userType = userType;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public String getUserType() {
        return this.userType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setPhoneNumber(final Optional<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Optional<String> getPhoneNumber() {
        return this.phoneNumber;
    }

    private void validateUserType(String userType) throws Exception {
        if (!UserUtils.validUserTypes.contains(userType)){
            throw UserUtils.invalidUserTypeException;
        }
    }
}
