package org.jerry.service.server;

import java.util.Map;

/**
 */
public interface Handler {

    public String doGet(Map<String, String> map);

    public String doPost(Map<String, String> map, String content);
}
