package org.pitest.mutationtest.report.html;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.pitest.mutationtest.MutationResult;

public class PackageSummaryDataTest {

  @Test
  public void shouldReturnSummaryDataInAlphabeticOrder() {
    final var testee = new PackageSummaryData("foo");
    final var a = makeSummaryData("a");
    final var z = makeSummaryData("z");
    testee.addSummaryData(z);
    testee.addSummaryData(a);
    assertEquals(Arrays.asList(a, z), testee.getSummaryData());

  }

  @Test
  public void shouldSortByPackageName() {
    final var aa = new PackageSummaryData("aa");
    final var ab = new PackageSummaryData("ab");
    final var c = new PackageSummaryData("c");
    final List<PackageSummaryData> actual = Arrays.asList(c, aa, ab);
    Collections.sort(actual);
    assertEquals(Arrays.asList(aa, ab, c), actual);
  }

  private MutationTestSummaryData makeSummaryData(final String fileName) {
    return new MutationTestSummaryData(fileName,
        Collections.<MutationResult> emptyList(),
        Collections.<String> emptyList(), Collections.emptyList(),
        0);
  }

}
