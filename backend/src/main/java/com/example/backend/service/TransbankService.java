package com.example.backend.service;

import com.example.backend.model.Order;
import com.example.backend.model.PaymentTransaction;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.PaymentTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransbankService {

    @Autowired
    private PaymentTransactionRepository transactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${transbank.api.key.id:KEYID}")
    private String transbankKeyId;

    @Value("${transbank.api.key.secret:SECRET}")
    private String transbankSecret;

    @Value("${transbank.api.url:https://webpay3gint.transbank.cl}")
    private String transbankUrl;

    public PaymentTransaction createTransaction(String orderId, String userId, double amount, String returnUrl, String finalUrl) {
        PaymentTransaction tx = PaymentTransaction.builder()
                .token(generateToken())
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .status("PENDIENTE")
                .authorizationCode(null)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60 * 30)) // 30 minutos
                .build();
        return transactionRepository.save(tx);
    }

    public Optional<PaymentTransaction> confirmTransaction(String token) {
        Optional<PaymentTransaction> ot = transactionRepository.findByToken(token);
        if (ot.isEmpty()) return Optional.empty();
        PaymentTransaction tx = ot.get();
        if (tx.getExpiresAt().isBefore(Instant.now())) {
            tx.setStatus("RECHAZADO");
            transactionRepository.save(tx);
            return Optional.of(tx);
        }
        // Lógica simulada: 80% aprobado
        boolean approved = Math.random() < 0.8;
        if (approved) {
            tx.setStatus("APROBADO");
            tx.setAuthorizationCode(generateAuthCode());
            // actualizar orden asociada
            Optional<Order> oo = orderRepository.findById(tx.getOrderId());
            if (oo.isPresent()) {
                Order o = oo.get();
                o.setStatus("Completada");
                orderRepository.save(o);
            }
        } else {
            tx.setStatus("RECHAZADO");
        }
        transactionRepository.save(tx);
        return Optional.of(tx);
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String generateAuthCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}
