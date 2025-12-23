package org.pitest.mutationtest.verify;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;


public class BuildMessageTest {
    @Test
    public void obeysHashcodeEqualsContract() {
        EqualsVerifier.forClass(BuildMessage.class)
                .verify();
    }

    @Test
    public void sortsZeroPriorityFirst() {
        var a = new BuildMessage("a","",10);
        var b = new BuildMessage("b","",0);
        var c = new BuildMessage("c","",5);

        List<BuildMessage> l = asList(a,b,c);
        Collections.sort(l);
        assertThat(l).containsExactly(b,c,a);
    }

    @Test
    public void includesURLInToStringWhenPresent() {
        var underTest = new BuildMessage("text", "https://pitest.org", 0);
        assertThat(underTest.toString()).isEqualTo("text (https://pitest.org)");
    }

    @Test
    public void doesNotIncludeURLInToStringWhenNull() {
        var underTest = new BuildMessage("text", null, 0);
        assertThat(underTest.toString()).isEqualTo("text");
    }
}