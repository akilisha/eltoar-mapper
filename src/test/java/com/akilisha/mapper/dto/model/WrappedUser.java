package com.akilisha.mapper.dto.model;

import com.akilisha.mapper.wrapper.DataWrapper;

import java.math.BigDecimal;

public class WrappedUser extends DataWrapper<User> {

    public WrappedUser(User target) {
        super(target);
    }

    public Long getId() {
        return this.getThisTarget().getId();
    }

    public void setId(Long id) {
        this.getThisTarget().setId(id);
    }

    public String getNames() {
        return this.getThisTarget().getNames();
    }

    public void setNames(String names) {
        this.getThisTarget().setNames(names);
    }

    public BigDecimal getSalary() {
        return this.getThisTarget().getSalary();
    }

    public void setSalary(BigDecimal salary) {
        this.getThisTarget().setSalary(salary);
    }

    public boolean isAccepted() {
        return this.getThisTarget().isAccepted();
    }

    public void setAccepted(boolean accepted) {
        this.getThisTarget().setAccepted(accepted);
    }

    public short getCode() {
        return this.getThisTarget().getCode();
    }

    public void setCode(short code) {
        this.getThisTarget().setCode(code);
    }
}
