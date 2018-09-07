/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.utils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author prem
 */
public class IDLogger {

    private final Set<String> ids = new ConcurrentHashMap().keySet(new Object());

    public String forceAdd(String id) {
        if (ids.add(id)) {
            return id;
        } else {
            return recursiveTryAdd(new StringBuilder(id)).toString();
        }
    }

    public boolean canAdd(String id) {
        return ids.contains(id);
    }

    private StringBuilder recursiveTryAdd(StringBuilder builder) {
        if (ids.add(builder.toString())) {
            return builder;
        } else {
            return recursiveTryAdd(builder.append("$"));
        }
    }
}
