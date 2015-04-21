package org.jerry.service.server;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Map;

public class DemoHandler implements Handler {

    private static final Logger LOGGER = Logger.getLogger(DemoHandler.class);

    public DemoHandler() {
    }

    public String doGet(Map<String, String> map) {
//        String seedType = map.get("type");
        String url = map.get("url");
//        String id  = map.get("id");
        if (StringUtils.isBlank(url)) {
            return "error parameter value for 'url'";
        }
        return "";
    }

    @Override
    public String doPost(Map<String, String> map, String content) {
        return "";
    }

}
