package com.github.seregamorph.maven.test.builder;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.plugin.DefaultMojosExecutionStrategy;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionRunner;
import org.apache.maven.plugin.MojosExecutionStrategy;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.sisu.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Named
@Singleton
@Priority(10)
public class DelegatingMojosExecutionStrategy implements MojosExecutionStrategy {

    private final MojosExecutionStrategy delegate;

    @Inject
    public DelegatingMojosExecutionStrategy(MavenSession session, Logger logger) {
        String builderId = session.getRequest().getBuilderId();
        if (TurboBuilder.BUILDER_TURBO.equals(builderId)) {
            logger.info("Using mojo reordering TurboMojosExecutionStrategy");
            delegate = new TurboMojosExecutionStrategy();
        } else {
            logger.info("Using default mojo ordering DefaultMojosExecutionStrategy");
            delegate = new DefaultMojosExecutionStrategy();
        }
    }

    @Override
    public void execute(List<MojoExecution> mojos, MavenSession session, MojoExecutionRunner mojoExecutionRunner)
        throws LifecycleExecutionException {
        delegate.execute(mojos, session, mojoExecutionRunner);
    }
}
