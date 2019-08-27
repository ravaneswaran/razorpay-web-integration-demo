package com.demo.razorpay.services;

import java.util.Date;

import com.demo.razorpay.models.PaymentTransaction;
import com.demo.razorpay.properties.RazorPayProperties;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

public class PaymentTransactionService {

	public static void createPaymentTransaction(String paymentId) throws RazorpayException {
		PaymentTransaction paymentTransaction = PaymentTransactionService.fetchPaymentTransaction(paymentId);
	}

	public static PaymentTransaction fetchPaymentTransaction(String paymentId) throws RazorpayException {
		RazorpayClient razorpayClient = new RazorpayClient(RazorPayProperties.getKeyId(),
				RazorPayProperties.getKeySecret());
		
		Payment payment = razorpayClient.Payments.fetch(paymentId);
		
		PaymentTransaction paymentTransaction = new PaymentTransaction();
		
		paymentTransaction.setId(""+payment.get("id"));
		paymentTransaction.setEntity(""+payment.get("entity"));
		paymentTransaction.setAmount(Integer.valueOf("" + payment.get("amount")));
		paymentTransaction.setCurrency(""+payment.get("currency"));
		paymentTransaction.setStatus(""+payment.get("status"));
		paymentTransaction.setMethod(""+payment.get("method"));
		paymentTransaction.setOrderId(""+payment.get("order_id"));
		paymentTransaction.setDescription(""+payment.get("description"));
		paymentTransaction.setAmountRefunded(Integer.valueOf("" + payment.get("amount_refunded")));
		paymentTransaction.setRefundStatus(""+payment.get("refund_status"));
		paymentTransaction.setEmail(""+payment.get("email"));
		paymentTransaction.setNotes(payment.get("notes"));
		paymentTransaction.setFee(Integer.valueOf("" + payment.get("fee")));
		paymentTransaction.setTax(Integer.valueOf("" + payment.get("tax")));
		paymentTransaction.setErrorCode(""+payment.get("error_code"));
		paymentTransaction.setErrorDescription(""+payment.get("error_description"));
		paymentTransaction.setCreatedAt(((Date)payment.get("created_at")).getTime());
		
		return paymentTransaction;
	}

}
