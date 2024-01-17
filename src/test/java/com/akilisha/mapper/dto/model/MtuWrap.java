package com.akilisha.mapper.dto.model;

import com.akilisha.mapper.wrapper.DataWrapper;

import java.math.BigDecimal;

public class MtuWrap extends DataWrapper<Mtu> {
    public MtuWrap(Mtu target) {
        super(target);
    }

    public long getId() {
        return this.getThisTarget().getId();
    }

    public void setId(long id) {
        this.getThisTarget().setId(id);
    }

    public String getMajina() {
        return this.getThisTarget().getMajina();
    }

    public void setMajina(String majina) {
        this.getThisTarget().setMajina(majina);
    }

    public BigDecimal getMshahara() {
        return this.getThisTarget().getMshahara();
    }

    public void setMshahara(BigDecimal mshahara) {
        this.getThisTarget().setMshahara(mshahara);
    }

    public boolean isPamoja() {
        return this.getThisTarget().isPamoja();
    }

    public void setPamoja(boolean pamoja) {
        this.getThisTarget().setPamoja(pamoja);
    }

    public short getSiri() {
        return this.getThisTarget().getSiri();
    }

    public void setSiri(short siri) {
        this.getThisTarget().setSiri(siri);
    }
}