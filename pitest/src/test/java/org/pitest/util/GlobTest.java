package org.pitest.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GlobTest {

  @Test
  public void shouldHandleEmptyStrings() {
    final var glob = new Glob("");
    assertTrue(glob.matches(""));
  }

  @Test
  public void shouldFindExactMatches() {
    final var value = "org.foo.foo";
    final var glob = new Glob(value);
    assertTrue(glob.matches(value));
  }

  @Test
  public void shouldNotMatchNonMatchingStringWhenNoWildcardsPresent() {
    final var value = "org.foo.foo";
    final var glob = new Glob("org.foo");
    assertFalse(glob.matches(value));
  }

  @Test
  public void shouldMatchEverythingAfterAStar() {
    final var glob = new Glob("org.foo.*");
    assertTrue(glob.matches("org.foo.foo"));
    assertTrue(glob.matches("org.foo."));
    assertTrue(glob.matches("org.foo.bar"));
  }

  @Test
  public void shouldNotMatchIfContentDiffersBeforeAStar() {
    final var glob = new Glob("org.foo.*");
    assertFalse(glob.matches("org.fo"));
  }

  @Test
  public void shouldEscapeDotsInGeneratedRegex() {
    final var glob = new Glob("org.foo.bar");
    assertFalse(glob.matches("orgafooabar"));
  }

  @Test
  public void shouldSupportQuestionMarkWildCard() {
    final var glob = new Glob("org?foo?bar");
    assertTrue(glob.matches("org.foo.bar"));
    assertTrue(glob.matches("orgafooabar"));
  }

  @Test
  public void shouldEscapeEscapesInGeneratedRegex() {
    final var glob = new Glob("org.\\bar");
    assertTrue(glob.matches("org.\\bar"));
    assertFalse(glob.matches("org.bar"));
  }

  @Test
  public void shouldSupportMultipleWildcards() {
    final var glob = new Glob("foo*bar*car");
    assertTrue(glob.matches("foo!!!bar!!!car"));
    assertFalse(glob.matches("foo!!!!!car"));
  }

  @Test
  public void shouldBeCaseSensitive() {
    final var glob = new Glob("foo*bar*car");
    assertTrue(glob.matches("foo!!!bar!!!car"));
    assertFalse(glob.matches("foo!!!Bar!!!car"));
  }

  @Test
  public void matchesStringsWithPlusSign() {
    final var glob = new Glob("foo+bar+car");
    assertTrue(glob.matches("foo+bar+car"));
    assertFalse(glob.matches("foo-Bar-car"));
  }

  @Test
  public void shouldSupportDoubleStarPackageMatcher() {
    final var glob = new Glob("**.databinding.**.Foo");
    assertTrue(glob.matches("databinding.Foo"));
    assertTrue(glob.matches("databinding.bar.Foo"));
    assertTrue(glob.matches("databinding.bar.car.Foo"));
    assertTrue(glob.matches("foo.databinding.Foo"));
    assertTrue(glob.matches("foo.car.databinding.bar.Foo"));
    assertTrue(glob.matches(".databinding.Foo"));
    assertFalse(glob.matches("databindingfoo.Foo"));
    assertFalse(glob.matches("foodatabinding.Foo"));
    assertFalse(glob.matches("databinding.fooFoo"));
  }

  @Test
  public void escapesParentheses() {
    final var glob = new Glob("some () path");
    assertTrue(glob.matches("some () path"));
    assertFalse(glob.matches("some path"));
  }

  @Test
  public void escapesSquareBrackets() {
    final var glob = new Glob("some [] path");
    assertTrue(glob.matches("some [] path"));
    assertFalse(glob.matches("some path"));
  }
}
