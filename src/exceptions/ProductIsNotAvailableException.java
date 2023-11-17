package exceptions;

import logic.Product;

public class ProductIsNotAvailableException extends Exception {
    public ProductIsNotAvailableException(final Product product) {
        super("Product is not available: %s, %f, %s".formatted(product.name(), product.price(), product.type()));
    }
}
