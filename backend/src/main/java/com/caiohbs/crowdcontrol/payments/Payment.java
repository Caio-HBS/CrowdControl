package com.caiohbs.crowdcontrol.payments;

import com.caiohbs.crowdcontrol.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long paymentId;
    @NotNull
    @Positive
    private double paymentAmount;
    @ManyToOne(fetch=FetchType.LAZY)
    @JsonIgnore
    private User user;

    public Payment() {
    }

    public Payment(Long paymentId, User user, double paymentAmount) {
        this.paymentId = paymentId;
        this.user = user;
        this.paymentAmount = paymentAmount;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    @Override
    public String toString() {
        return "Payment{" +
               "paymentId=" + paymentId +
               ", user=" + user +
               ", paymentAmount=" + paymentAmount +
               '}';
    }

}
