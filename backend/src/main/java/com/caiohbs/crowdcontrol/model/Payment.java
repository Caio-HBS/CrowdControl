package com.caiohbs.crowdcontrol.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long paymentId;
    @NotNull
    @Positive
    private double paymentAmount;
    private LocalDate paymentDate;
    @JsonIgnore
    @ManyToOne(fetch=FetchType.EAGER)
    private User user;

    public Payment() {
    }
    public Payment(User user, double paymentAmount) {
        this.user = user;
        this.paymentAmount = paymentAmount;
        this.paymentDate = LocalDate.now();
    }

    public Payment(
            Long paymentId, User user,
            double paymentAmount
    ) {
        this.paymentId = paymentId;
        this.user = user;
        this.paymentAmount = paymentAmount;
        this.paymentDate = LocalDate.now();
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

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
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
