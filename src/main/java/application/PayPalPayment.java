package main.java.application;

public class PayPalPayment implements PaymentStrategy {
    private final String email;

    public PayPalPayment(String email, String password) {
        this.email = email;
    }

    //metodo fantoccio per simulare un conto paypal 'pseudo' vera
    @Override
    public boolean pay(double amount) {
        return email != null && email.contains("@") ;
    }
}
