/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.stdlib.cache.compiler;

import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.Package;
import io.ballerina.projects.ProjectEnvironmentBuilder;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.environment.Environment;
import io.ballerina.projects.environment.EnvironmentBuilder;
import io.ballerina.tools.diagnostics.Diagnostic;
import io.ballerina.tools.diagnostics.DiagnosticInfo;
import io.ballerina.tools.diagnostics.DiagnosticSeverity;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tests the custom cache compiler plugin.
 */
public class CompilerPluginTest {

    private static ProjectEnvironmentBuilder getEnvironmentBuilder() {
        Path distributionPath = Paths.get("../", "target", "ballerina-runtime")
                .toAbsolutePath();
        Environment environment = EnvironmentBuilder.getBuilder().setBallerinaHome(distributionPath).build();
        return ProjectEnvironmentBuilder.getBuilder(environment);
    }

    private Package loadPackage(String path) {
        Path projectDirPath = Paths.get("src", "test", "resources", "diagnostics").
                toAbsolutePath().resolve(path);
        BuildProject project = BuildProject.load(getEnvironmentBuilder(), projectDirPath);
        return project.currentPackage();
    }

    @Test
    public void testInvalidConfig1() {
        DiagnosticResult diagnosticResult = loadPackage("sample1").getCompilation().diagnosticResult();
        List<Diagnostic> errorDiagnosticsList = diagnosticResult.diagnostics().stream()
                .filter(r -> r.diagnosticInfo().severity().equals(DiagnosticSeverity.ERROR))
                .collect(Collectors.toList());
        assertValues(errorDiagnosticsList);
    }

    @Test
    public void testInvalidConfig2() {
        DiagnosticResult diagnosticResult = loadPackage("sample2").getCompilation().diagnosticResult();
        List<Diagnostic> errorDiagnosticsList = diagnosticResult.diagnostics().stream()
                .filter(r -> r.diagnosticInfo().severity().equals(DiagnosticSeverity.ERROR))
                .collect(Collectors.toList());
        assertValues(errorDiagnosticsList);
    }

    @Test
    public void testInvalidConfig3() {
        DiagnosticResult diagnosticResult = loadPackage("sample3").getCompilation().diagnosticResult();
        List<Diagnostic> errorDiagnosticsList = diagnosticResult.diagnostics().stream()
                .filter(r -> r.diagnosticInfo().severity().equals(DiagnosticSeverity.ERROR))
                .collect(Collectors.toList());
        assertValues(errorDiagnosticsList);
    }

    @Test
    public void testInvalidConfig4() {
        DiagnosticResult diagnosticResult = loadPackage("sample4").getCompilation().diagnosticResult();
        List<Diagnostic> errorDiagnosticsList = diagnosticResult.diagnostics().stream()
                .filter(r -> r.diagnosticInfo().severity().equals(DiagnosticSeverity.ERROR))
                .collect(Collectors.toList());
        Assert.assertEquals(errorDiagnosticsList.size(), 2);
        DiagnosticInfo invalidCleanupInterval = errorDiagnosticsList.get(0).diagnosticInfo();
        Assert.assertEquals(invalidCleanupInterval.code(), DiagnosticsCodes.CACHE_104.getErrorCode());
        Assert.assertEquals(invalidCleanupInterval.messageFormat(),
                "invalid value: a greater than zero value is expected");

        DiagnosticInfo invalidPolicy = errorDiagnosticsList.get(1).diagnosticInfo();
        Assert.assertEquals(invalidPolicy.code(), DiagnosticsCodes.CACHE_105.getErrorCode());
        Assert.assertEquals(invalidPolicy.messageFormat(),
                "invalid value: only 'cache:LRU' value is supported");
    }

    @Test
    public void testConfigWithOutVariables() {
        DiagnosticResult diagnosticResult = loadPackage("sample5").getCompilation().diagnosticResult();
        List<Diagnostic> errorDiagnosticsList = diagnosticResult.diagnostics().stream()
                .filter(r -> r.diagnosticInfo().severity().equals(DiagnosticSeverity.ERROR))
                .collect(Collectors.toList());
        Assert.assertEquals(errorDiagnosticsList.size(), 0);
    }

    @Test
    public void testConfigWithConstants() {
        DiagnosticResult diagnosticResult = loadPackage("sample6").getCompilation().diagnosticResult();
        List<Diagnostic> errorDiagnosticsList = diagnosticResult.diagnostics().stream()
                .filter(r -> r.diagnosticInfo().severity().equals(DiagnosticSeverity.ERROR))
                .collect(Collectors.toList());
        Assert.assertEquals(errorDiagnosticsList.size(), 0);
    }

    @Test(description = "Tests whether there are no compilation failures for constants and configurables as included " +
            "params. Those validations will be ignored.")
    public void testConfigWithIncludedParams() {
        DiagnosticResult diagnosticResult = loadPackage("sample7").getCompilation().diagnosticResult();
        List<Diagnostic> errorDiagnosticsList = diagnosticResult.diagnostics().stream()
                .filter(r -> r.diagnosticInfo().severity().equals(DiagnosticSeverity.ERROR))
                .collect(Collectors.toList());
        Assert.assertEquals(errorDiagnosticsList.size(), 0);
    }

    private void assertValues(List<Diagnostic> errorDiagnosticsList) {
        long availableErrors = errorDiagnosticsList.size();
        Assert.assertEquals(availableErrors, 5);
        DiagnosticInfo invalidCapacity = errorDiagnosticsList.get(0).diagnosticInfo();
        Assert.assertEquals(invalidCapacity.code(), DiagnosticsCodes.CACHE_101.getErrorCode());
        Assert.assertEquals(invalidCapacity.messageFormat(),
                "invalid value: a greater than zero value is expected");

        DiagnosticInfo invalidEvictionFactor = errorDiagnosticsList.get(1).diagnosticInfo();
        Assert.assertEquals(invalidEvictionFactor.code(), DiagnosticsCodes.CACHE_102.getErrorCode());
        Assert.assertEquals(invalidEvictionFactor.messageFormat(),
                "invalid value: a value between 0 (exclusive) and 1 (inclusive) is expected");


        DiagnosticInfo invalidDefaultMaxAge = errorDiagnosticsList.get(2).diagnosticInfo();
        Assert.assertEquals(invalidDefaultMaxAge.code(), DiagnosticsCodes.CACHE_103.getErrorCode());
        Assert.assertEquals(invalidDefaultMaxAge.messageFormat(),
                "invalid value: a greater than 0 value or -1(to indicate forever valid) value is expected");

        DiagnosticInfo invalidCleanupInterval = errorDiagnosticsList.get(3).diagnosticInfo();
        Assert.assertEquals(invalidCleanupInterval.code(), DiagnosticsCodes.CACHE_104.getErrorCode());
        Assert.assertEquals(invalidCleanupInterval.messageFormat(),
                "invalid value: a greater than zero value is expected");

        DiagnosticInfo invalidPolicy = errorDiagnosticsList.get(4).diagnosticInfo();
        Assert.assertEquals(invalidPolicy.code(), DiagnosticsCodes.CACHE_105.getErrorCode());
        Assert.assertEquals(invalidPolicy.messageFormat(),
                "invalid value: only 'cache:LRU' value is supported");
    }
}
