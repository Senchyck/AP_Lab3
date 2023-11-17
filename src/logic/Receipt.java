package logic;

import exceptions.PaidReceiptModifyingException;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Receipt implements Serializable {


    private boolean isPaid;
    private final String customerName;
    private final Date issueDate;
    private final ArrayList<Product> products;



    public String getCustomerName() {
        return this.customerName;
    }

    public Calendar getIssueDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(this.issueDate);

        return c;
    }

    public ArrayList<Product> getProducts() {
        return this.products;
    }


    public Receipt(final String customerName, final Calendar issueDate) {
        this.products = new ArrayList<>();
        this.isPaid = false;
        this.customerName = customerName;
        this.issueDate = issueDate.getTime();
    }



    public double getTotalPrice() {
        return this.products.stream().mapToDouble(Product::price).sum();
    }

    public void pay() throws PaidReceiptModifyingException {
        this.ensureIsNotPaid();
        this.isPaid = true;
    }

    public void addProduct(final Product product) throws PaidReceiptModifyingException {
        this.ensureIsNotPaid();
        this.products.add(product);
    }

    public void generate(final String pathname) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        sb.append("""
                          Issued date: %s
                          This receipt belongs to %s%n
                          """.formatted(sdf.format(this.issueDate.getTime()), this.customerName));

        this.products.forEach(p -> sb.append("%-70s || %.2f%n".formatted(p.name(), p.price())));

        if (this.products.stream()
                .anyMatch(p -> p.type().equals(ProductType.MEAT) || p.type().equals(ProductType.FISH))) {
            sb.append("\nDo not forget to store these products in the refrigerator: ");

            this.products.stream().filter(p -> p.type().equals(ProductType.MEAT) || p.type().equals(ProductType.FISH))
                    .forEach(product -> sb.append("\"%s\", ".formatted(product.name())));

            sb.delete(sb.length() - 2, sb.length());
        }

        sb.append("\nTotal price: %.2f%s".formatted(this.getTotalPrice(), (this.isPaid
                ? "\nThis receipt is already paid."
                : "")));

        FileService.writeText(pathname, sb.toString());
    }

    private void ensureIsNotPaid() throws PaidReceiptModifyingException {
        if (this.isPaid) {
            throw new PaidReceiptModifyingException();
        }
    }


}
