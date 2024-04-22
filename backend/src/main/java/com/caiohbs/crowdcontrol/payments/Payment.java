package com.caiohbs.crowdcontrol.payments;

import com.caiohbs.crowdcontrol.users.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Payment {

    @Id
    @GeneratedValue
    private Long paymentId;
    @OneToOne
    private User user;
    private double paymentAmount;

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
