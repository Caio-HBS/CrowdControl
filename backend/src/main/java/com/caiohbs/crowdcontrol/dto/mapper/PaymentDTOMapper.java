package com.caiohbs.crowdcontrol.dto.mapper;

import com.caiohbs.crowdcontrol.dto.PaymentDTO;
import com.caiohbs.crowdcontrol.model.Payment;

import java.util.function.Function;

public class PaymentDTOMapper implements Function<Payment, PaymentDTO> {

    @Override
    public PaymentDTO apply(Payment payment) {
        return new PaymentDTO(
                payment.getUser().getUserId(),
                payment.getPaymentId(),
                payment.getPaymentAmount(),
                payment.getPaymentDate()
        );
    }
}
