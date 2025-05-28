package com.github.seregamorph.maven.turbo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionRunner;
import org.apache.maven.plugin.MojosExecutionStrategy;
import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.seregamorph.maven.turbo.MavenPropertyUtils.getProperty;
import static com.github.seregamorph.maven.turbo.MavenPropertyUtils.isEmptyOrTrue;

/**
 * @author Sergey Chernov
 */
public class TurboMojosExecutionStrategy implements MojosExecutionStrategy {

/*
    original phases of the default lifecycle [
        "validate",
        "initialize",
        "generate-sources",
        "process-sources",
        "generate-resources",
        "process-resources",
        "compile",
        "process-classes",

     |==>
     |   "generate-test-sources",
     |   "process-test-sources",
     |   "generate-test-resources",
     |   "process-test-resources",
     |   "test-compile",
     |   "process-test-classes",
     |   "test",
     |
     |  // moved before "*test*" phases
     |= "prepare-package",
     |= "package",

        "pre-integration-test",
        "integration-test",
        "post-integration-test",
        "verify",
        "install",
        "deploy"
    ]
*/

    private static boolean isPackage(String lifecyclePhase) {
        return Arrays.asList("prepare-package", "package")
            .contains(lifecyclePhase);
    }

    private static boolean isTest(String lifecyclePhase) {
        // "generate-test-sources", "process-test-sources", "generate-test-resources", "process-test-resources",
        // "test-compile", "process-test-classes", "test", "pre-integration-test", "integration-test",
        // "post-integration-test"
        return "test".equals(lifecyclePhase)
            || lifecyclePhase.contains("-test-")
            || lifecyclePhase.startsWith("test-")
            || lifecyclePhase.endsWith("-test");
    }

    @Override
    public void execute(
        List<MojoExecution> mojos,
        MavenSession session,
        MojoExecutionRunner mojoRunner
    ) throws LifecycleExecutionException {
        List<MojoExecution> packageMojos = mojos.stream()
            .filter(mojo -> {
                String lifecyclePhase = mojo.getLifecyclePhase();
                return lifecyclePhase != null && isPackage(lifecyclePhase);
            })
            .collect(Collectors.toList());
        int firstTestMojoIndex = -1;
        for (int i = 0; i < mojos.size(); i++) {
            MojoExecution mojoExecution = mojos.get(i);
            String lifecyclePhase = mojoExecution.getLifecyclePhase();
            if (lifecyclePhase != null && isTest(lifecyclePhase)) {
                firstTestMojoIndex = i;
                break;
            }
        }
        List<MojoExecution> reorderedMojos = new ArrayList<>(mojos);
        if (!packageMojos.isEmpty() && firstTestMojoIndex != -1) {
            reorderedMojos.removeAll(packageMojos);
            reorderedMojos.addAll(firstTestMojoIndex, packageMojos);
        }

        List<MojoExecution> executedPackageMojos = new ArrayList<>();
        MavenProject currentProject = session.getCurrentProject();
        // There can be scenarios when we use TurboBuilder as default, but disable per project, property or via profile,
        // when it's known that the downstream dependencies should be only scheduled when all phases are completed.
        boolean skipTurboSignal = isEmptyOrTrue(getProperty(session, currentProject, "skipTurboSignal"));
        boolean signaled = skipTurboSignal;
        for (MojoExecution mojoExecution : reorderedMojos) {
            if (!signaled && packageMojos.isEmpty()) {
                String lifecyclePhase = mojoExecution.getLifecyclePhase();
                if (lifecyclePhase != null && isTest(lifecyclePhase)) {
                    signaled = true;
                    // signal before tests
                    SignalingExecutorCompletionService.signal(currentProject);
                }
            }
            mojoRunner.run(mojoExecution);
            if (!signaled) {
                if (packageMojos.contains(mojoExecution)) {
                    executedPackageMojos.add(mojoExecution);
                    if (packageMojos.equals(executedPackageMojos)) {
                        signaled = true;
                        // signal after package
                        SignalingExecutorCompletionService.signal(currentProject);
                    }
                }
            }
        }
    }
}
