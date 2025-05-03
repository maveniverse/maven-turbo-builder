package com.github.seregamorph.maven.test.builder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Chernov
 */
@JsonIgnoreProperties({
    "//" // for comments
})
public class SimpleParallelConfig {

    private List<String> prioritizedModules = new ArrayList<>();

    public List<String> getPrioritizedModules() {
        return prioritizedModules;
    }

    public void setPrioritizedModules(List<String> prioritizedModules) {
        this.prioritizedModules = prioritizedModules;
    }
}
