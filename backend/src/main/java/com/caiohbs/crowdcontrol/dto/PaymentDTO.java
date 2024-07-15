package com.caiohbs.crowdcontrol.dto;

import java.time.LocalDate;

public record PaymentDTO(
        Long userId,
        Long paymentId,
        double paymentAmount,
        LocalDate paymentDate
) {
}
