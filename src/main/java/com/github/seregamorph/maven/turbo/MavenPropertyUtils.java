package com.github.seregamorph.maven.turbo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

/**
 * @author Sergey Chernov
 */
final class MavenPropertyUtils {

    /*@Nullable*/
    static String getProperty(MavenSession session, String propertyName) {
        String propertyValue = session.getSystemProperties().getProperty(propertyName);
        if (propertyValue == null) {
            propertyValue = session.getUserProperties().getProperty(propertyName);
        }
        return propertyValue;
    }

    /*@Nullable*/
    static String getProperty(MavenSession session, MavenProject project, String propertyName) {
        String propertyValue = getProperty(session, propertyName);
        if (propertyValue == null) {
            propertyValue = project.getProperties().getProperty(propertyName);
        }
        return propertyValue;
    }

    static boolean isTrue(String value) {
        return "true".equals(value);
    }

    private MavenPropertyUtils() {
    }
}
