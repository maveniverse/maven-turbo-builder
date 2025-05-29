package com.github.seregamorph.maven.turbo;

import javax.inject.Named;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.SessionScoped;
import org.apache.maven.execution.MavenSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sergey Chernov
 */
@SessionScoped
@Named
public class TurboMavenLifecycleParticipant extends AbstractMavenLifecycleParticipant {

    private static final Logger logger = LoggerFactory.getLogger(TurboMavenLifecycleParticipant.class);

    @Override
    public void afterProjectsRead(MavenSession session) {
        checkBuilderAndPhase(session);
    }

    @Override
    public void afterSessionEnd(MavenSession session) {
        checkBuilderAndPhase(session);
    }

    private static void checkBuilderAndPhase(MavenSession session) {
        String builderId = session.getRequest().getBuilderId();
        if (TurboBuilder.BUILDER_TURBO.equals(builderId)) {
            if (session.getRequest().getGoals().contains("package")) {
                logger.warn("package phase is requested in combination with turbo builder.\n"
                    + "Please note, that compiling and running test is not included in execution\n"
                    + "because of phase reordering. To run tests, use test goal (also includes package)\n"
                    + "instead of package.");
            }
        }
    }
}
