package com.example.java7;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HasSwitchOnStringTesteeTest {

  private final HasSwitchOnStringTestee testee = new HasSwitchOnStringTestee();

  @Test
  public void stringSwitchShouldReturnA() throws Exception {
    // given
    final var input = "a";

    // when
    final var result = testee.switchString(input);

    // then
    assertThat(result).isEqualTo("A");
  }

  @Test
  public void stringSwitchShouldReturnB() throws Exception {
    // given
    final var input = "b";

    // when
    final var result = testee.switchString(input);

    // then
    assertThat(result).isEqualTo("B");
  }

  @Test
  public void stringSwitchShouldReturnC() throws Exception {
    // given
    final var input = "c";

    // when
    final var result = testee.switchString(input);

    // then
    assertThat(result).isEqualTo("C");
  }

  @Test(expected = IllegalArgumentException.class)
  public void stringSwitchShouldThrowIllegalArgumentException()
      throws Exception {
    // given
    final var input = "x";

    // when
    testee.switchString(input);

    // then
    // exception
  }

  @Test
  public void switchIntegerAndThenSwitchStringShouldReturnA1()
      throws Exception {
    // given
    final var input = 1;

    // when
    final var result = testee.switchIntegerAndThenSwitchString(input);

    // then
    assertThat(result).isEqualTo("A1");
  }

  @Test
  public void switchIntegerAndThenSwitchStringShouldReturnB1()
      throws Exception {
    // given
    final var input = 2;

    // when
    final var result = testee.switchIntegerAndThenSwitchString(input);

    // then
    assertThat(result).isEqualTo("B1");
  }

  @Test
  public void switchIntegerAndThenSwitchStringShouldReturnC1()
      throws Exception {
    // given
    final var input = 3;

    // when
    final var result = testee.switchIntegerAndThenSwitchString(input);

    // then
    assertThat(result).isEqualTo("C1");
  }

  @Test(expected = IllegalArgumentException.class)
  public void switchIntegerAndThenSwitchStringShouldThrowIllegalArgumentException()
      throws Exception {
    // given
    final var input = 4;

    // when
    testee.switchIntegerAndThenSwitchString(input);

    // then
  }
}
