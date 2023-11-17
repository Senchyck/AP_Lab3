package logic;

import java.io.Serializable;
import java.util.Objects;

public record Product(String name, double price, ProductType type) implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Product product)) {
            return false;
        }

        return Double.compare(this.price, product.price) == 0 && Objects.equals(this.name, product.name) &&
                this.type == product.type;
    }

    @Override
    public String toString() {
        return "Product{" + "name='" + this.name + '\'' + ", price=" + this.price + ", type=" + this.type + '}';
    }
}
