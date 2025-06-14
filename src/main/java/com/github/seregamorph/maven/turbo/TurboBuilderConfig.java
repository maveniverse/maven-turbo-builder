package com.github.seregamorph.maven.turbo;

import javax.inject.Inject;
import org.apache.maven.execution.MavenSession;

/**
 * @author Sergey Chernov
 */
public class TurboBuilderConfig {

    private final boolean turboTestCompile;

    @Inject
    public TurboBuilderConfig(MavenSession session) {
        String turboTestCompile = MavenPropertyUtils.getProperty(session, "turboTestCompile");
        this.turboTestCompile = MavenPropertyUtils.isTrue(turboTestCompile);
    }

    TurboBuilderConfig(boolean turboTestCompile) {
        this.turboTestCompile = turboTestCompile;
    }

    public boolean isTurboTestCompile() {
        return turboTestCompile;
    }

    @Override
    public String toString() {
        return "TurboBuilderConfig{" +
            "turboTestCompile=" + turboTestCompile +
            '}';
    }
}
