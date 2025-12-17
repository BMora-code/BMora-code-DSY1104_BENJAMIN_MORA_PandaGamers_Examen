package com.example.backend.controller;

import com.example.backend.dto.ConfirmPaymentResponse;
import com.example.backend.dto.PaymentResponse;
import com.example.backend.model.PaymentTransaction;
import com.example.backend.model.Order;
import com.example.backend.repository.OrderRepository;
import com.example.backend.service.TransbankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/pay")
public class TransbankController {

    @Autowired
    private TransbankService transbankService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestParam String orderId, @RequestParam(required = false) String userId, @RequestParam(required = false) Double amount, @RequestParam(required = false) String returnUrl, @RequestParam(required = false) String finalUrl) {
        // Si no hay userId o amount, buscar la orden para obtener estos datos
        if ((userId == null || amount == null) && orderId != null) {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                if (userId == null) userId = order.getUserId();
                if (amount == null) amount = order.getTotal();
            }
        }

        PaymentTransaction tx = transbankService.createTransaction(orderId, userId, amount != null ? amount : 0, returnUrl, finalUrl);
        PaymentResponse resp = new PaymentResponse();
        resp.setToken(tx.getToken());
        String url = "https://sandbox.transbank.cl/mockpay?token=" + tx.getToken();
        resp.setUrl(url);
        resp.setOrderId(orderId);
        resp.setAmount(amount != null ? amount : 0);
        resp.setMessage("Transacción creada");
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/confirm/{token}")
    public ResponseEntity<ConfirmPaymentResponse> confirm(@PathVariable String token) {
        Optional<PaymentTransaction> ot = transbankService.confirmTransaction(token);
        if (ot.isEmpty()) return ResponseEntity.notFound().build();
        PaymentTransaction tx = ot.get();
        ConfirmPaymentResponse resp = new ConfirmPaymentResponse();
        resp.setOrderId(tx.getOrderId());
        resp.setStatus(tx.getStatus());
        resp.setAuthorizationCode(tx.getAuthorizationCode());
        resp.setMessage("Resultado de la transacción");
        return ResponseEntity.ok(resp);
    }
}
