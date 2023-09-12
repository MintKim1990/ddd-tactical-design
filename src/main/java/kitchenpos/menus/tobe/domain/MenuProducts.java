package kitchenpos.menus.tobe.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Embeddable
public class MenuProducts {

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(
            name = "menu_id",
            nullable = false,
            columnDefinition = "binary(16)",
            foreignKey = @ForeignKey(name = "fk_menu_product_to_menu")
    )
    private List<MenuProduct> values;

    protected MenuProducts() {
    }

    public MenuProducts(List<MenuProduct> values) {
        validateValues(values);
        this.values = values;
    }

    private void validateValues(List<MenuProduct> values) {
        if (Objects.isNull(values) || values.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    public List<MenuProduct> getMenuProducts() {
        return Collections.unmodifiableList(values);
    }

    public List<UUID> getProductIds() {
        List<UUID> uuids = values.stream()
                .map(MenuProduct::getProductId)
                .collect(Collectors.toList());

        return Collections.unmodifiableList(uuids);
    }

}
