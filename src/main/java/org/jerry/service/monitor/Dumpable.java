package org.jerry.service.monitor;

import java.io.IOException;

/**
 * 用于监控
 */
public interface Dumpable {

    String dump();

    void dump(Appendable out, String indent) throws IOException;

}