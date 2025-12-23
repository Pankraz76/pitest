package org.pitest.coverage;


import org.junit.Test;
import org.pitest.testapi.Description;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pitest.coverage.CoverageMother.aBlockLocation;

public class BasicStatListenerTest {

    BasicStatListener testee = new BasicStatListener();

    @Test
    public void noMessagesWhenNoTestData() {
        assertThat(testee.messages()).isEmpty();
    }

    @Test
    public void storesSlowestTest() {
        var cr1 = CoverageMother.aCoverageResult()
                .withExecutionTime(19)
                .build();

        var d = new Description("foo", "bar");
        var cr2 = CoverageMother.aCoverageResult()
                .withExecutionTime(20)
                .withTestUnitDescription(d)
                .build();

        var cr3 = CoverageMother.aCoverageResult()
                .withExecutionTime(19)
                .build();

        testee.accept(cr1);
        testee.accept(cr2);
        testee.accept(cr3);

        assertThat(testee.messages())
                .contains("Slowest test (foo) took 20 ms");
    }

    @Test
    public void storesLargestTest() {
        var cr1 = CoverageMother.aCoverageResult()
                .withVisitedBlocks(aBlockLocation().build(3))
                .build();

        var d = new Description("foo", "bar");
        var cr2 = CoverageMother.aCoverageResult()
                .withVisitedBlocks(aBlockLocation().build(4))
                .withTestUnitDescription(d)
                .build();

        var cr3 = CoverageMother.aCoverageResult()
                .withVisitedBlocks(aBlockLocation().build(3))
                .build();

        testee.accept(cr1);
        testee.accept(cr2);
        testee.accept(cr3);

        assertThat(testee.messages())
                .contains("Largest test (foo) covered 4 blocks");
    }

    @Test
    public void reportsNumberOfVerySlowTests() {
        var cr1 = CoverageMother.aCoverageResult()
                .withExecutionTime(2000)
                .build();

        var cr2 = CoverageMother.aCoverageResult()
                .withExecutionTime(2001)
                .build();

        var cr3 = CoverageMother.aCoverageResult()
                .withExecutionTime(1999)
                .build();

        var cr4 = CoverageMother.aCoverageResult()
                .withExecutionTime(2001)
                .build();

        testee.accept(cr1);
        testee.accept(cr2);
        testee.accept(cr3);
        testee.accept(cr4);

        assertThat(testee.messages())
                .contains("2 tests took longer than 2000 ms");
    }

}
