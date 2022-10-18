package kitchenpos.products.tobe.domain;

import static org.junit.jupiter.api.Assertions.*;

import kitchenpos.products.application.FakePurgomalumClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("상품이름 테스트")
class DisplayedNameTest {

  @DisplayName("상품이름을 생성할 수 있다.")
  @Test
  void createName() {
    DisplayedName name = DisplayedName.from("후라이드 치킨");
    Assertions.assertThat(name).isEqualTo(DisplayedName.from("후라이드 치킨"));
  }

  @DisplayName("상품이름은 없거나 비어있을 수 없다")
  @ParameterizedTest
  @NullAndEmptySource
  void createName_notValidName(String value) {
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(() -> DisplayedName.from(value));
  }

  @DisplayName("상품이름은 비속어가 포함될 수 없다")
  @ParameterizedTest
  @ValueSource(strings = {"비속어", "욕설"})
  void createName_notProfanity(String value) {
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(() -> DisplayedName.from(value, new FakeDisplayedNameValidator()));
  }

}