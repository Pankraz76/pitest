/*
 * Copyright 2011 Henry Coles
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.pitest.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.mockito.Mockito;
import org.pitest.mutationtest.config.ConfigOption;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.util.Unchecked;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.pitest.mutationtest.config.ExecutionMode.DRY_RUN;
import static org.pitest.util.Verbosity.DEFAULT;
import static org.pitest.util.Verbosity.QUIET;
import static org.pitest.util.Verbosity.VERBOSE;

public class MojoToReportOptionsConverterTest extends BasePitMojoTest {

  private MojoToReportOptionsConverter testee;
  private SurefireConfigConverter      surefireConverter;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    var surefire = new Plugin();
    surefire.setGroupId("org.apache.maven.plugins");
    surefire.setArtifactId("maven-surefire-plugin");
    this.surefireConverter = Mockito.mock(SurefireConfigConverter.class);
    List<Plugin> mavenPlugins = Collections.singletonList(surefire);
    when(this.project.getBuildPlugins()).thenReturn(mavenPlugins);
    var build = new Build();
    build.setOutputDirectory("");
    when(this.project.getBuild()).thenReturn(build);
    when(this.project.getBasedir()).thenReturn(new File("BASEDIR"));
  }

  public void testsParsesReportDir() {
    final var actual = parseConfig("<reportsDirectory>Foo</reportsDirectory>");
    assertEquals(new File("Foo").getAbsolutePath(), actual.getReportDir());
  }

  public void testCreatesPredicateFromListOfTargetClassGlobs() {
    final var xml = "<targetClasses>" + //
        "                     <param>foo*</param>" + //
        "                     <param>bar*</param>" + //
        "                  </targetClasses>";

    final var actual = parseConfig(xml);
    final Predicate<String> actualPredicate = actual.getTargetClassesFilter();
    assertTrue(actualPredicate.test("foo_anything"));
    assertTrue(actualPredicate.test("bar_anything"));
    assertFalse(actualPredicate.test("notfoobar"));
  }

  public void testUsesSourceDirectoriesFromProject() {
    when(this.project.getCompileSourceRoots()).thenReturn(asList("src"));
    when(this.project.getTestCompileSourceRoots()).thenReturn(
        asList("tst"));
    final var actual = parseConfig("");
    assertThat(actual.getSourcePaths()).containsExactly(Path.of("src"), Path.of("tst"));
  }

  public void testParsesExcludedRunners() {
    var runner = "org.springframework.test.context.junit4.SpringJUnit4ClassRunner";
    final var actual = parseConfig("<excludedRunners><param>" + runner + "</param></excludedRunners>");
    assertThat(actual.getExcludedRunners()).hasSize(1).containsExactly(runner);
  }

  public void testParsesListOfJVMArgs() {
    final var xml = "<jvmArgs>" + //
        "                      <param>foo</param>" + //
        "                      <param>bar</param>" + //
        "                  </jvmArgs>";
    final var actual = parseConfig(xml);

    List<String> expectedArgs = new ArrayList<>();
    expectedArgs.add("foo");
    expectedArgs.add("bar");

    assertEquals(expectedArgs, actual.getJvmArgs());
  }

  public void testParsesListOfMutationOperators() {
    final var xml = "<mutators>" + //
        "                      <param>foo</param>" + //
        "                      <param>bar</param>" + //
        "                  </mutators>";
    final var actual = parseConfig(xml);
    assertEquals(asList("foo", "bar"), actual.getMutators());
  }

  public void testParsesListOfFeatures() {
    final var xml = "<features>" + //
        "                      <param>+FOO</param>" + //
        "                      <param>-BAR(foo[1] bar[3])</param>" + //
        "               </features>";
    final var actual = parseConfig(xml);
    assertThat(actual.getFeatures()).contains("+FOO", "-BAR(foo[1] bar[3])");
  }


  public void testParsesNumberOfThreads() {
    final var actual = parseConfig("<threads>42</threads>");
    assertEquals(42, actual.getNumberOfThreads());
  }

  public void testParsesTimeOutFactor() {
    final var actual = parseConfig("<timeoutFactor>1.32</timeoutFactor>");
    assertEquals(1.32f, actual.getTimeoutFactor(), 0.1);
  }

  public void testParsesTimeOutConstant() {
    final var actual = parseConfig("<timeoutConstant>42</timeoutConstant>");
    assertEquals(42, actual.getTimeoutConstant());
  }

  public void testParsesListOfTargetTestClassGlobs() {
    final var xml = "<targetTests>" + //
        "                      <param>foo*</param>" + //
        "                      <param>bar*</param>" + //
        "                  </targetTests>";
    final var actual = parseConfig(xml);
    final Predicate<String> actualPredicate = actual.getTargetTestsFilter();
    assertTrue(actualPredicate.test("foo_anything"));
    assertTrue(actualPredicate.test("bar_anything"));
    assertFalse(actualPredicate.test("notfoobar"));
  }

  public void testParsesListOfExcludedTestClassGlobs() {
    final var xml = "<excludedTestClasses>" + //
        "                      <param>foo*</param>" + //
        "                  </excludedTestClasses>" + //
        "                  <targetTests>" + //
        "                      <param>foo*</param>" + //
        "                      <param>bar*</param>" + //
        "                  </targetTests>";
    final var actual = parseConfig(xml);
    final Predicate<String> testPredicate = actual.getTargetTestsFilter();
    assertFalse(testPredicate.test("foo_anything"));
    assertTrue(testPredicate.test("bar_anything"));
  }

  public void testParsesListOfExcludedClassGlobsAndApplyTheseToTargets() {
    final var xml = "<excludedClasses>" + //
        "                      <param>foo*</param>" + //
        "                  </excludedClasses>" + //
        "                  <targetClasses>" + //
        "                      <param>foo*</param>" + //
        "                      <param>bar*</param>" + //
        "                  </targetClasses>";
    final var actual = parseConfig(xml);
    final Predicate<String> targetPredicate = actual.getTargetClassesFilter();
    assertFalse(targetPredicate.test("foo_anything"));
    assertTrue(targetPredicate.test("bar_anything"));
  }

  public void testDefaultsLoggingPackagesToDefaultsDefinedByDefaultMutationConfigFactory() {
    final var actual = parseConfig("");
    assertEquals(ReportOptions.LOGGING_CLASSES, actual.getLoggingClasses());
  }

  public void testParsesListOfClassesToAvoidCallTo() {
    final var xml = "<avoidCallsTo>" + //
        "                      <param>foo</param>" + //
        "                      <param>bar</param>" + //
        "                      <param>foo.bar</param>" + //
        "                  </avoidCallsTo>";
    final var actual = parseConfig(xml);
    assertEquals(asList("foo", "bar", "foo.bar"),
        actual.getLoggingClasses());
  }

  public void testParsesCommaListOfExcludedMethods() {
    final var xml = "<excludedMethods>" + //
        "                      <param>foo*</param>" + //
        "                      <param>bar*</param>" + //
        "                      <param>car</param>" + //
        "                  </excludedMethods>";
    final var options = parseConfig(xml);
    final Collection<String> actual = options.getExcludedMethods();
    assertThat(actual).containsExactlyInAnyOrder("foo*", "bar*", "car");
  }

  public void testParsesVerboseFlag() {
    assertThat(parseConfig("<verbose>true</verbose>").getVerbosity()).isEqualTo(VERBOSE);
    assertThat(parseConfig("<verbose>false</verbose>").getVerbosity()).isEqualTo(DEFAULT);
  }

  public void testParsesVerbosity() {
    assertThat(parseConfig("<verbosity>quiet</verbosity>").getVerbosity())
            .isEqualTo(QUIET);
  }

  public void testVerboseFlagOverridesVerbosity() {
    assertThat(parseConfig("<verbose>true</verbose><verbosity>DEFAULT</verbosity>").getVerbosity())
            .isEqualTo(VERBOSE);
  }

  public void testParsesDetectInlineCodeFlag() {
    assertTrue(parseConfig("<detectInlinedCode>true</detectInlinedCode>")
        .isDetectInlinedCode());
    assertFalse(parseConfig("<detectInlinedCode>false</detectInlinedCode>")
        .isDetectInlinedCode());
  }

  public void testDefaultsToHtmlReportWhenNoOutputFormatsSpecified() {
    final var actual = parseConfig("");
    assertEquals(new HashSet<>(asList("HTML")),
        actual.getOutputFormats());
  }

  public void testParsesListOfOutputFormatsWhenSupplied() {
    final var xml = "<outputFormats>" + //
        "                      <param>HTML</param>" + //
        "                      <param>CSV</param>" + //
        "                  </outputFormats>";
    final var actual = parseConfig(xml);
    assertEquals(new HashSet<>(asList("HTML", "CSV")),
        actual.getOutputFormats());
  }

  public void testObeysFailWhenNoMutationsFlagWhenPackagingTypeIsNotPOM() {
    when(this.project.getModel()).thenReturn(new Model());
    assertTrue(parseConfig("<failWhenNoMutations>true</failWhenNoMutations>")
        .shouldFailWhenNoMutations());
    assertFalse(parseConfig("<failWhenNoMutations>false</failWhenNoMutations>")
        .shouldFailWhenNoMutations());
  }

  public void testObeysSkipFailingTestsFlagWhenPackagingTypeIsNotPOM() {
	    when(this.project.getModel()).thenReturn(new Model());
	    assertTrue(parseConfig("<skipFailingTests>true</skipFailingTests>")
	        .skipFailingTests());
	    assertFalse(parseConfig("<skipFailingTests>false</skipFailingTests>")
	        .skipFailingTests());
	  }

  public void testParsesTestGroupsToExclude() {
    final var actual = parseConfig("<excludedGroups><value>foo</value><value>bar</value></excludedGroups>");
    assertEquals(asList("foo", "bar"), actual.getGroupConfig()
        .getExcludedGroups());
  }

  public void testParsesTestGroupsToInclude() {
    final var actual = parseConfig("<includedGroups><value>foo</value><value>bar</value></includedGroups>");
    assertEquals(asList("foo", "bar"), actual.getGroupConfig()
        .getIncludedGroups());
  }

  public void testParsesTestMethodsToInclude() {
    final var actual = parseConfig("<includedTestMethods><value>foo</value><value>bar</value></includedTestMethods>");
    assertEquals(asList("foo", "bar"), actual
            .getIncludedTestMethods());
  }

  public void testMaintainsOrderOfClassPath() {
    final var actual = parseConfig("<includedGroups><value>foo</value><value>bar</value></includedGroups>");
    assertEquals(this.classPath, actual.getClassPathElements());
  }

  public void testParsesFullMutationMatrix() {
    final var actual = parseConfig("<fullMutationMatrix>true</fullMutationMatrix>");
    assertEquals(true, actual.isFullMutationMatrix());
  }

  public void testParsesMutationUnitSize() {
    final var actual = parseConfig("<mutationUnitSize>50</mutationUnitSize>");
    assertEquals(50, actual.getMutationUnitSize());
  }

  public void testDefaultsMutationUnitSizeToCorrectValue() {
    final var actual = parseConfig("");
    assertEquals(
        (int) ConfigOption.MUTATION_UNIT_SIZE.getDefault(Integer.class),
        actual.getMutationUnitSize());
  }

  public void testParsesTimeStampedReports() {
    final var actual = parseConfig("<timestampedReports>false</timestampedReports>");
    assertEquals(false, actual.shouldCreateTimeStampedReports());
  }

  public void testParsesHistoryInputFile() {
    final var actual = parseConfig("<historyInputFile>foo</historyInputFile>");
    assertEquals(new File("foo"), actual.getHistoryInputLocation());
  }

  public void testParsesHistoryOutputFile() {
    final var actual = parseConfig("<historyOutputFile>foo</historyOutputFile>");
    assertEquals(new File("foo"), actual.getHistoryOutputLocation());
  }

  public void testParsesLocalHistoryFlag() {
    when(this.project.getGroupId()).thenReturn("com.example");
    when(this.project.getArtifactId()).thenReturn("foo");
    when(this.project.getVersion()).thenReturn("0.1-SNAPSHOT");
    final var actual = parseConfig("<withHistory>true</withHistory>");
    var expected = "com.example.foo.0.1-SNAPSHOT_pitest_history.bin";
    assertThat(actual.getHistoryInputLocation()).isNotNull();
    assertThat(actual.getHistoryInputLocation().getAbsolutePath()).endsWith(expected);
  }

  public void testOverridesExplicitPathsWhenWithHistoryFlagSet() {
    when(this.project.getGroupId()).thenReturn("com.example");
    when(this.project.getArtifactId()).thenReturn("foo");
    when(this.project.getVersion()).thenReturn("0.1-SNAPSHOT");
    final var actual = parseConfig("<historyInputFile>foo.bin</historyInputFile><withHistory>true</withHistory>");
    var expected = "com.example.foo.0.1-SNAPSHOT_pitest_history.bin";
    assertThat(actual.getHistoryInputLocation()).isNotNull();
    assertThat(actual.getHistoryInputLocation().getAbsolutePath()).endsWith(expected);
  }

  public void testParsesLineCoverageExportFlagWhenSet() {
    final var actual = parseConfig("<exportLineCoverage>true</exportLineCoverage>");
    assertTrue(actual.shouldExportLineCoverage());
  }

  public void testParsesLineCoverageExportFlagWhenNotSet() {
    final var actual = parseConfig("<exportLineCoverage>false</exportLineCoverage>");
    assertFalse(actual.shouldExportLineCoverage());
  }

  public void testParsesEngineWhenSet() {
    final var actual = parseConfig("<mutationEngine>foo</mutationEngine>");
    assertEquals("foo", actual.getMutationEngine());
  }

  public void testDefaultsJavaExecutableToNull() {
    final var actual = parseConfig("");
    assertEquals(null, actual.getJavaExecutable());
  }

  public void testParsesJavaExecutable() {
    final var actual = parseConfig("<jvm>foo</jvm>");
    assertEquals("foo", actual.getJavaExecutable());
  }

  public void testParsesExcludedClasspathElements()
      throws DependencyResolutionRequiredException {
    final var sep = File.pathSeparator;

    final Set<Artifact> artifacts = new HashSet<>();
    final Artifact dependency = Mockito.mock(Artifact.class);
    when(dependency.getGroupId()).thenReturn("group");
    when(dependency.getArtifactId()).thenReturn("artifact");
    when(dependency.getFile()).thenReturn(
        new File("group" + sep + "artifact" + sep + "1.0.0" + sep
            + "group-artifact-1.0.0.jar"));
    artifacts.add(dependency);
    when(this.project.getArtifacts()).thenReturn(artifacts);
    when(this.project.getTestClasspathElements()).thenReturn(
        asList("group" + sep + "artifact" + sep + "1.0.0" + sep
            + "group-artifact-1.0.0.jar"));

    final var actual = parseConfig("<classpathDependencyExcludes>"
        + "										<param>group:artifact</param>"
        + "									</classpathDependencyExcludes>");
    assertFalse(actual.getClassPathElements().contains(
        "group" + sep + "artifact" + sep + "1.0.0" + sep
        + "group-artifact-1.0.0.jar"));
  }

  public void testParsesSurefireConfigWhenFlagSet() {
    parseConfig("<parseSurefireConfig>true</parseSurefireConfig>");
    verify(this.surefireConverter).update(any(ReportOptions.class),
        isNull());
  }

  public void testIgnoreSurefireConfigWhenFlagNotSet() {
    parseConfig("<parseSurefireConfig>false</parseSurefireConfig>");
    verify(this.surefireConverter, never()).update(any(ReportOptions.class),
        any(Xpp3Dom.class));
  }

  public void testParsesCustomProperties() {
    final var actual = parseConfig("<pluginConfiguration><foo>foo</foo><bar>bar</bar></pluginConfiguration>");
    assertEquals("foo", actual.getFreeFormProperties().get("foo"));
    assertEquals("bar", actual.getFreeFormProperties().get("bar"));
  }


  public void testLegacyClasspathJarParamDoesNotCauseError() {
    assertThatCode(() -> parseConfig("<useClasspathJar>true</useClasspathJar>"))
            .doesNotThrowAnyException();

  }

  public void testFailsIfObsoleteMaxMutationsParameterUsed() {
    assertThatCode( () -> parseConfig("<maxMutationsPerClass>1</maxMutationsPerClass>"))
            .hasMessageContaining("+CLASSLIMIT(limit[1])");
  }

  public void testParsesProjectBase() {
    final var actual = parseConfig("<projectBase>user</projectBase>");
    assertThat(actual.getProjectBase().toString()).isEqualTo("user");
  }

  public void testParsesInputSourceEncoding() {
    final var actual = parseConfig("<inputEncoding>US-ASCII</inputEncoding>");
    assertThat(actual.getInputEncoding()).isEqualTo(StandardCharsets.US_ASCII);
  }

  public void testParsesOutputEncoding() {
    final var actual = parseConfig("<outputEncoding>US-ASCII</outputEncoding>");
    assertThat(actual.getOutputEncoding()).isEqualTo(StandardCharsets.US_ASCII);
  }

  public void testParsesArgline() {
    var actual = parseConfig("<argLine>foo</argLine>");
    assertThat(actual.getArgLine()).isEqualTo("foo");
  }

  public void testEvaluatesSureFireLateEvalArgLineProperties() {
    properties.setProperty("FOO", "fooValue");
    properties.setProperty("BAR", "barValue");
    properties.setProperty("UNUSED", "unusedValue");
    var actual = parseConfig("<argLine>@{FOO} @{BAR}</argLine>");
    assertThat(actual.getArgLine()).isEqualTo("fooValue barValue");
  }

  public void testEvaluatesNormalPropertiesInArgLines() {
    properties.setProperty("FOO", "fooValue");
    properties.setProperty("BAR", "barValue");
    properties.setProperty("UNUSED", "unusedValue");
    // these are normally auto resolved by maven, but if we pull
    // in an argline from surefire it will not have been escaped.
    var actual = parseConfig("<argLine>${FOO} ${BAR}</argLine>");
    assertThat(actual.getArgLine()).isEqualTo("fooValue barValue");
  }

  public void testEvaluatesLocalRepositoryPropertyInArgLines() {
    when(this.settings.getLocalRepository()).thenReturn("localRepoValue");
    var actual = parseConfig("<argLine>${settings.localRepository}/jar</argLine>");
    assertThat(actual.getArgLine()).isEqualTo("localRepoValue/jar");
  }

  public void testAddsModulesToMutationPathWhenCrossModule() {
    MavenProject dependedOn = project("com.example", "foo");
    MavenProject notDependedOn = project("com.example", "bar");

    when(session.getProjects()).thenReturn(asList(dependedOn, notDependedOn));

    var dependency = new Dependency();
    dependency.setGroupId("com.example");
    dependency.setArtifactId("foo");
    when(project.getDependencies()).thenReturn(asList(dependency));

    final var actual = parseConfig("<crossModule>true</crossModule>");

    assertThat(actual.getCodePaths()).contains("foobuild");
    assertThat(actual.getCodePaths()).doesNotContain("barbuild");
  }

  public void testSetsDryRunMode() {
    var actual = parseConfig("<dryRun>true</dryRun>");
    assertThat(actual.mode()).isEqualTo(DRY_RUN);
  }

  private static MavenProject project(String group, String artefact) {
    var dependedOn = new MavenProject();
    dependedOn.setGroupId(group);
    dependedOn.setArtifactId(artefact);

    var build = new Build();
    build.setOutputDirectory(artefact + "build");
    dependedOn.setBuild(build);

    return dependedOn;
  }

  private ReportOptions parseConfig(final String xml) {
    try {
      final var pom = createPomWithConfiguration(xml);
      final var mojo = createPITMojo(pom);
      Predicate<Artifact> filter = Mockito.mock(Predicate.class);
      when(
          this.surefireConverter.update(any(ReportOptions.class),
              any(Xpp3Dom.class))).then(returnsFirstArg());
      this.testee = new MojoToReportOptionsConverter(mojo,
          this.surefireConverter, filter);
      return this.testee.convert();
    } catch (final Exception ex) {
      throw Unchecked.translateCheckedException(ex);
    }
  }

}
