package logic;

import exceptions.ObjectIsNotArrayListException;
import exceptions.PaidReceiptModifyingException;
import exceptions.ProductIsNotAvailableException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Main {
    public static void main(String[] args)
            throws IOException, PaidReceiptModifyingException, ProductIsNotAvailableException, ClassNotFoundException,
            ObjectIsNotArrayListException {

        final String pathToAvailableProductsFile = "C:\\Other\\products";
        final String pathToPurchaseHistory = "C:\\Other\\history";

        final ArrayList<Product> products = getProducts();

        FileService.writeObject(pathToAvailableProductsFile, products);

        final Market market = new Market(pathToAvailableProductsFile, pathToPurchaseHistory);

        final Product product1 = new Product("Eidelklass, Paper 400", 65, ProductType.GOODS);
        final Product product2 = new Product("Eidelklass, Paper 400", 30, ProductType.GOODS);

        System.out.println("I. " + market.getAvailableProductsAveragePrice());         // Must be 49.285...

        market.buyProduct(product1);
        System.out.println("II. " + market.getAvailableProductsAveragePrice());        // Must be 51.25

        market.editProduct(product1, product2);
        System.out.println("III. " + market.getAvailableProductsAveragePrice());       // Must be 46.875

        market.editProduct(product2, product1);
        System.out.println("IV. " + market.getAvailableProductsAveragePrice());        // Must be 51.25

        System.out.println("V.");

        List<Product> filteredProducts = market.filterAvailableProductsByPrice(50, 85, false);
        filteredProducts.forEach(System.out::println);

        final Calendar date1 = Calendar.getInstance();
        date1.add(Calendar.DAY_OF_MONTH, -1);

        final Calendar date2 = Calendar.getInstance();
        date2.add(Calendar.DAY_OF_MONTH, 2);

        final Calendar date3 = Calendar.getInstance();
        date3.add(Calendar.DAY_OF_MONTH, 10);

        final Calendar date4 = Calendar.getInstance();
        date4.add(Calendar.DAY_OF_MONTH, 1);

        final Calendar date5 = Calendar.getInstance();
        date5.add(Calendar.DAY_OF_MONTH, 11);

        final Receipt receipt1 = new Receipt("Ihor Matskiv", date1);
        market.sellProduct(receipt1, products.get(0));
        market.sellProduct(receipt1, products.get(1));
        market.payCheck(receipt1);

        final Receipt receipt2 = new Receipt("Ihor Matskiv", date2);
        market.sellProduct(receipt2, products.get(2));
        market.sellProduct(receipt2, products.get(3));
        market.payCheck(receipt2);

        final Receipt receipt3 = new Receipt("Ihor Matskiv", date3);
        market.sellProduct(receipt3, products.get(4));
        market.sellProduct(receipt3, products.get(5));
        market.payCheck(receipt3);

        System.out.println("VI. " + market.getProfitFromCustomer("Ihor Matskiv", date4, date5));

        final Receipt receipt4 = new Receipt("Alla Kulik", date1);
        market.sellProduct(receipt4, products.get(1));
        market.sellProduct(receipt4, products.get(1));
        market.sellProduct(receipt4, products.get(3));
        market.payCheck(receipt4);

        final Receipt receipt5 = new Receipt("Alla Kulik", date2);
        market.sellProduct(receipt5, products.get(4));
        market.payCheck(receipt5);

        System.out.println("VII.");
        market.getCustomerPurchasesMap("Alla Kulik").forEach((k, v) -> System.out.println(k + "\t" + v));

        System.out.println("VIII. " + market.getMostPopularProduct());

        System.out.println("IX. " + market.getBestDailyProfit());

        final Market otherMarket = new Market(pathToAvailableProductsFile, pathToPurchaseHistory);
        List<Product> otherFilteredProducts = otherMarket.filterAvailableProductsByPrice(50, 85, false);

        System.out.println("X.");
        otherFilteredProducts.forEach(System.out::println);

        receipt1.generate("C:\\Other\\receipt.txt");
    }

    private static ArrayList<Product> getProducts() {
        final ArrayList<Product> products = new ArrayList<>();

        products.add(new Product("Organic Farms, Turkey Breast 750g", 60, ProductType.MEAT));                /
        products.add(new Product("Homemade Meatballs, Special Blend 500g", 70, ProductType.MEAT));
        products.add(new Product("Free-Range Chicken, Whole 1.5kg", 120, ProductType.MEAT));
        products.add(new Product("Deep Blue Sea, Salmon Fillet 400g", 50, ProductType.FISH));
        products.add(new Product("Fresh Harvest, Organic Carrots 2kg", 120, ProductType.VEGETABLES));
        products.add(new Product("Local Orchards, Juicy Mangoes 1.5kg", 80, ProductType.FRUITS));
        products.add(new Product("Stationery Haven, Premium Pen Set", 15, ProductType.GOODS));


        return products;
    }
}
