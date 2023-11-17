package exceptions;



public class PaidReceiptModifyingException extends Exception {
    public PaidReceiptModifyingException() {
        super("Paid receipt cannot be modified.");
    }
}
