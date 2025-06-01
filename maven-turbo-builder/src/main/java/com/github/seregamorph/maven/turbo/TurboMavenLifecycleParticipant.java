package com.github.seregamorph.maven.turbo;

import javax.inject.Named;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.SessionScoped;
import org.apache.maven.execution.MavenSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Build start / finish interceptor that prints a warning to avoid confusion.
 *
 * @author Sergey Chernov
 */
@SessionScoped
@Named
public class TurboMavenLifecycleParticipant extends AbstractMavenLifecycleParticipant {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

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
                logger.warn("package phase is requested in combination with turbo builder (`-bturbo` parameter \n"
                    + "in the command line or .mvn/maven.config). Please note, that \n"
                    + ANSI_RED + "compiling and running tests is not included in the execution" + ANSI_RESET + "\n"
                    + "because of phase reordering. To run tests, use test or verify phase (also includes package)\n"
                    + "instead of package.");
            }
        }
    }
}
