package com.akilisha.mapper.perf.data.destination;

import java.util.Objects;

public class User {

    private String username;
    private String email;
    private AccountStatus userAccountStatus;

    public User(String username, String email, AccountStatus userAccountStatus) {
        this.username = username;
        this.email = email;
        this.userAccountStatus = userAccountStatus;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountStatus getUserAccountStatus() {
        return userAccountStatus;
    }

    public void setUserAccountStatus(AccountStatus userAccountStatus) {
        this.userAccountStatus = userAccountStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o.getClass() == User.class) {
            User user =
                    (User) o;
            return Objects.equals(username, user.getUsername()) &&
                    Objects.equals(email, user.getEmail()) &&
                    userAccountStatus.ordinal() == user.getUserAccountStatus().ordinal();
        }
        if (o.getClass() != getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                userAccountStatus == user.userAccountStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, userAccountStatus);
    }

    public AccountStatus conversion(AccountStatus status) {
        AccountStatus accountStatus = null;
        switch (status) {
            case ACTIVE:
                accountStatus = AccountStatus.ACTIVE;
                break;
            case NOT_ACTIVE:
                accountStatus = AccountStatus.NOT_ACTIVE;
                break;

            case BANNED:
                accountStatus = AccountStatus.BANNED;
                break;
        }
        return accountStatus;
    }
}
