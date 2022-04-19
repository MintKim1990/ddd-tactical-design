package kitchenpos.products.dto;

import kitchenpos.products.tobe.domain.Product;

public class ProductResponse {
    private String id;
    private String name;
    private Long price;

    private ProductResponse() { }

    private ProductResponse(String id, String name, Long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public static ProductResponse from(Product saved) {
        return new ProductResponse(saved.getId().toString(), saved.getName(), saved.getPrice());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }
}