package kitchenpos.menus.ui.request;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public class MenuProductCreateRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Long quantity;

    public MenuProductCreateRequest() {
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}