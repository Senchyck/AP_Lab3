package logic;

import exceptions.ObjectIsNotArrayListException;
import exceptions.PaidReceiptModifyingException;
import exceptions.ProductIsNotAvailableException;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Market {


    final private String availableProductsPathname;
    final private String historyPathname;
    final private ArrayList<Product> availableProducts;
    final private ArrayList<Receipt> receiptsHistory;


    public Market(final String availableProductsPathname, final String historyPathname)
            throws IOException, ClassNotFoundException, ObjectIsNotArrayListException {
        this.availableProductsPathname = availableProductsPathname;
        this.historyPathname = historyPathname;
        this.availableProducts = FileService.readAsArrayList(this.availableProductsPathname, Product.class);
        this.receiptsHistory = FileService.readAsArrayList(this.historyPathname, Receipt.class);
    }


    public double getAvailableProductsAveragePrice() {
        return this.availableProducts.stream().mapToDouble(Product::price).average().orElse(0);
    }

    public double getProfitFromCustomer(final String customerName, final Calendar fromDate, final Calendar toDate) {
        return this.receiptsHistory.stream().filter(r -> r.getCustomerName().equals(customerName) &&
                        r.getIssueDate().after(fromDate) &&
                        r.getIssueDate().before(toDate))
                .mapToDouble(Receipt::getTotalPrice).sum();
    }

    public double getBestDailyProfit() {
        final Map<Calendar, Double> dailyProfitMap = this.receiptsHistory.stream().collect(
                Collectors.groupingBy(Receipt::getIssueDate, Collectors.summingDouble(Receipt::getTotalPrice)));
        final Entry<Calendar, Double> bestDailyProfitEntry =
                dailyProfitMap.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);

        return bestDailyProfitEntry != null
                ? bestDailyProfitEntry.getValue()
                : 0;
    }

    public Product getMostPopularProduct() {
        final Map<Product, Long> productCountMap = this.receiptsHistory.stream().flatMap(r -> r.getProducts().stream())
                .collect(Collectors.groupingBy(p -> p,
                        Collectors.counting()));
        final Entry<Product, Long> mostPopularEntry =
                productCountMap.entrySet().stream().max(Entry.comparingByValue()).orElse(null);

        return mostPopularEntry != null
                ? mostPopularEntry.getKey()
                : null;
    }

    public List<Product> filterAvailableProductsByPrice(final double minPrice, final double maxPrice,
                                                        final boolean sortDescending) {
        if (sortDescending) {
            return this.availableProducts.stream().filter(p -> p.price() >= minPrice && p.price() <= maxPrice)
                    .sorted(Comparator.comparingDouble(Product::price).reversed()).toList();
        } else {
            return this.availableProducts.stream().filter(p -> p.price() >= minPrice && p.price() <= maxPrice)
                    .sorted(Comparator.comparingDouble(Product::price)).toList();
        }
    }

    public Map<String, Long> getCustomerPurchasesMap(final String customerName) {
        return this.receiptsHistory.stream().filter(r -> r.getCustomerName().equals(customerName))
                .flatMap(r -> r.getProducts().stream())
                .collect(Collectors.groupingBy(Product::name, Collectors.counting()));
    }

    public void buyProduct(final Product product) throws IOException {
        this.availableProducts.add(product);

        this.updateAvailableProductsFile();
    }

    public void editProduct(final Product soughtProduct, final Product newProduct) throws IOException {
        this.availableProducts.replaceAll(p -> p.equals(soughtProduct)
                ? newProduct
                : p);

        this.updateAvailableProductsFile();
    }

    public void sellProduct(final Receipt receipt, final Product product)
            throws PaidReceiptModifyingException, ProductIsNotAvailableException, IOException {
        if (this.availableProducts.stream().anyMatch(p -> p.equals(product))) {
            if (product.type().equals(ProductType.FRUITS) || product.type().equals(ProductType.VEGETABLES)) {
                receipt.addProduct(new Product("Bag", 1, ProductType.GOODS));
            }

            receipt.addProduct(product);
        } else {
            throw new ProductIsNotAvailableException(product);
        }

        this.updateAvailableProductsFile();
    }

    public void payCheck(final Receipt receipt) throws PaidReceiptModifyingException, IOException {
        receipt.pay();
        this.receiptsHistory.add(receipt);

        this.updateReceiptsHistoryFile();
    }

    private void updateAvailableProductsFile() throws IOException {
        FileService.writeObject(this.availableProductsPathname, this.availableProducts);
    }

    private void updateReceiptsHistoryFile() throws IOException {
        FileService.writeObject(this.historyPathname, this.receiptsHistory);
    }


}
