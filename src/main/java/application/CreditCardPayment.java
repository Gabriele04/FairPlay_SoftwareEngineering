package main.java.application;

public class CreditCardPayment implements PaymentStrategy {
    private final String cardNumber;
    private final String cvv;

    public CreditCardPayment(String cardNumber, String cvv) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }

    @Override
    public boolean pay(double amount) {
        return cardNumber != null && cardNumber.length() == 16 && cvv != null && cvv.length() == 3;
    }
}