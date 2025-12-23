package org.pitest.mutationtest.autoconfig;

import org.junit.Test;
import org.pitest.mutationtest.config.ConfigUpdaterVerifier;
import org.pitest.mutationtest.config.ReportOptions;

import static org.assertj.core.api.Assertions.assertThat;

public class KeepMacOsFocusTest {
    KeepMacOsFocus underTest = new KeepMacOsFocus();

    ConfigUpdaterVerifier v = ConfigUpdaterVerifier.confirmFactory(underTest);

    @Test
    public void addsHeadlessTrueToJvmArgs() {
        var data = new ReportOptions();

        underTest.updateConfig(null, data);
        assertThat(data.getJvmArgs()).contains("-Djava.awt.headless=true");
    }

    @Test
    public void isOnChain() {
        v.isOnChain();
    }

    @Test
    public void featureIsNamedMacOsFocus() {
        v.featureName().isEqualTo("macos_focus");
    }

    @Test
    public void featureIsOnByDefault() {
       v.isOnByDefault();
    }
}