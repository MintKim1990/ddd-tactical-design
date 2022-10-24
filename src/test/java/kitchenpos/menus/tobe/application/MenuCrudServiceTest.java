package kitchenpos.menus.tobe.application;

import kitchenpos.ToBeFixtures;
import kitchenpos.menus.tobe.domain.menu.MenuRepository;
import kitchenpos.menus.tobe.domain.menugroup.MenuGroup;
import kitchenpos.menus.tobe.domain.menugroup.MenuGroupRepository;
import kitchenpos.menus.tobe.dto.menu.CreateMenuRequest;
import kitchenpos.menus.tobe.dto.menu.MenuProductRequest;
import kitchenpos.menus.tobe.dto.menu.ProductRequest;
import kitchenpos.products.application.FakePurgomalumClient;
import kitchenpos.products.infra.PurgomalumClient;
import kitchenpos.products.tobe.application.InMemoryProductRepository;
import kitchenpos.products.tobe.domain.Product;
import kitchenpos.products.tobe.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.ToBeFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("메뉴 CRUD 서비스")
class MenuCrudServiceTest {
    private MenuRepository menuRepository;
    private MenuCrudService menuCrudService;
    private Product product;
    private MenuGroup menuGroup;
    private ProductRepository productRepository;
    private MenuGroupRepository menuGroupRepository;
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        menuRepository = new InMemoryMenuRepository();
        purgomalumClient = new FakePurgomalumClient();
        menuCrudService = new MenuCrudService(menuRepository, productRepository, menuGroupRepository, purgomalumClient);
        product = productRepository.save(product("후라이드", 16_000L));
        menuGroup = menuGroupRepository.save(menuGroup("메뉴그룹"));
        menuRepository.save(menu("후라이드치킨", 19_000L, true, ToBeFixtures.menuProduct(product, 2L)));
    }

    @DisplayName("메뉴의 목록을 조회할 수 있다.")
    @Test
    void findMenus() {
        assertThat(menuCrudService.findAll()).hasSize(1);
    }

    @DisplayName("상품이 없으면 등록할 수 없다.")
    @ParameterizedTest
    @CsvSource({"후라이드메뉴, 10, 후라이드상품, 1, 1"})
    void createEmptyProductMenu(String menuName, BigDecimal menuPrice, String productName, BigDecimal productPrice, BigDecimal quantity) {
        assertThatThrownBy(() -> menuCrudService.create(createMenuRequest(menuGroup.getId(), menuName, menuPrice,
                menuProductRequests(UUID.randomUUID(), productName, productPrice, quantity))))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("해당하는 상품이 업습니다.");
    }

    @DisplayName("메뉴의 가격은 0원 이상이어야 한다.")
    @ParameterizedTest
    @CsvSource({"후라이드메뉴, 후라이드상품, 1, 1, -1"})
    void menuPriceOverZero(String menuName, String productName, BigDecimal productPrice, BigDecimal quantity, BigDecimal menuPrice) {
        List<MenuProductRequest> menuProductRequests = new ArrayList<>();
        menuProductRequests.add(menuProductRequest(product.getId(), productName, productPrice, quantity));
        assertThatThrownBy(() -> menuCrudService.create(createMenuRequest(menuGroup.getId(), menuName, menuPrice, menuProductRequests)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("0원 이하일 수 없습니다.");
    }

    @DisplayName("메뉴명은 null 이나 공백일 수 없습니다.")
    @ParameterizedTest
    @NullAndEmptySource
    void createEmptyMenuName(String menuName) {
        assertThatThrownBy(() -> menuCrudService.create(createMenuRequest(menuGroup.getId(), menuName, BigDecimal.valueOf(3000),
                menuProductRequests(product.getId(), "후라이드", BigDecimal.ONE, BigDecimal.ONE))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 이나 공백일 수 없습니다.");
    }

    @DisplayName("메뉴에 속한 상품 금액의 합은 메뉴의 가격보다 크거나 같아야 한다.")
    @ParameterizedTest
    @CsvSource({"후라이드메뉴, 3001, 후라이드상품, 1, 1"})
    void validateMenuProductsPriceSum(String menuName, BigDecimal menuPrice, String productName, BigDecimal productPrice, BigDecimal quantity) {
        assertThatThrownBy(() -> menuCrudService.create(createMenuRequest(menuGroup.getId(), menuName, menuPrice,
                menuProductRequests(product.getId(), productName, productPrice, quantity))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴에 속한 상품 금액의 합은 메뉴의 가격보다 작을 수 없습니다.");
    }

    @DisplayName("상품이 없으면 등록할 수 없다.")
    @ParameterizedTest
    @CsvSource({"후라이드메뉴, 3001, 후라이드상품, 3003, 1"})
    void existProduct(String menuName, BigDecimal menuPrice, String productName, BigDecimal productPrice, BigDecimal quantity) {
        assertThatThrownBy(() -> menuCrudService.create(createMenuRequest(menuGroup.getId(), menuName, menuPrice,
                menuProductRequests(UUID.randomUUID(), productName, productPrice, quantity))))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("해당하는 상품이 업습니다.");
    }

    @DisplayName("메뉴는 특정 메뉴 그룹에 속해야 한다.")
    @ParameterizedTest
    @CsvSource({"후라이드메뉴, 3001, 후라이드상품, 1, 1"})
    void existMenuGroup(String menuName, BigDecimal menuPrice, String productName, BigDecimal productPrice, BigDecimal quantity) {
        assertThatThrownBy(() -> menuCrudService.create(createMenuRequest(UUID.randomUUID(), menuName, menuPrice,
                menuProductRequests(UUID.randomUUID(), productName, productPrice, quantity))))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("해당하는 메뉴 그룹이 업습니다.");
    }

    @DisplayName("1 개 이상의 등록된 상품으로 메뉴를 등록할 수 있다.")
    @ParameterizedTest
    @CsvSource({"후라이드메뉴, 3001"})
    void createMenuProductSizeOverOne(String menuName, BigDecimal menuPrice) {
        List<MenuProductRequest> menuProductRequests = new ArrayList<>();
        assertThatThrownBy(() -> menuCrudService.create(createMenuRequest(menuGroup.getId(), menuName, menuPrice, menuProductRequests)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메뉴 상품을 등록해주세요.");
    }

    private static List<MenuProductRequest> menuProductRequests(UUID productId, String productName, BigDecimal productPrice, BigDecimal quantity) {
        List<MenuProductRequest> menuProductRequests = new ArrayList<>();
        menuProductRequests.add(menuProductRequest(productId, productName, productPrice, quantity));
        return menuProductRequests;
    }

    private CreateMenuRequest createMenuRequest(UUID menuGroupId, String menuName, BigDecimal menuPrice, List<MenuProductRequest> menuProductRequests) {
        return new CreateMenuRequest(menuGroupId, menuName, menuPrice, menuProductRequests);
    }

    private static MenuProductRequest menuProductRequest(UUID productId, String productName, BigDecimal productPrice, BigDecimal quantity) {
        return new MenuProductRequest(productRequest(productId, productName, productPrice), quantity);
    }

    private static ProductRequest productRequest(UUID productId, String productName, BigDecimal productPrice) {
        return new ProductRequest(productId, productName, productPrice);
    }

}