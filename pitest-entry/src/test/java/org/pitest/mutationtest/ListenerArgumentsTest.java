package org.pitest.mutationtest;

import org.junit.Test;
import org.pitest.mutationtest.verify.BuildMessage;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ListenerArgumentsTest {

    @Test
    public void removesDuplicateBuildIssues() {
        List<BuildMessage> issues = asList(BuildMessage.buildMessage("foo"), BuildMessage.buildMessage("foo"));
        var underTest = new ListenerArguments(null, null, null, null, 0, false, null, issues);
        assertThat(underTest.issues()).containsExactly(BuildMessage.buildMessage("foo"));
    }

    @Test
    public void ordersBuildIssuesByPriority() {
        var a = new BuildMessage("foo", null, 5);
        var b = new BuildMessage("important", null, 0);
        var c = new BuildMessage("bar", null, 4);
        List<BuildMessage> issues = asList(a,b,c);
        var underTest = new ListenerArguments(null, null, null, null, 0, false, null, issues);
        assertThat(underTest.issues()).containsExactly(b, c, a);
    }
}