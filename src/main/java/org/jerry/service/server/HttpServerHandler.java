package org.jerry.service.server;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.util.CharsetUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

//import org.json.JSONException;

public class HttpServerHandler extends SimpleChannelUpstreamHandler {
    private final static Logger log = Logger.getLogger(HttpServerHandler.class);
    private final static boolean debug = log.isDebugEnabled();
    protected final Object handler;
    protected final String CONTENT_TYPE_V;
    protected Map<String, Method> methodMap;

    /** 构造函数 */
    public HttpServerHandler(Object handler) {
        super();
        this.handler = handler;
        this.CONTENT_TYPE_V = "text/plain; charset=UTF-8";
        Method[] handlerMethods = handler.getClass().getMethods();
        Map<String, Method> m = new HashMap<String, Method>();
        for (Method method : handlerMethods)
            m.put(method.getName(), method);
        this.methodMap = Collections.unmodifiableMap(m);
    }

    /** 构造函数 */
    public HttpServerHandler(Object handler, String CONTENT_TYPE) {
        super();
        this.handler = handler;
        this.CONTENT_TYPE_V = CONTENT_TYPE;
        Method[] handlerMethods = handler.getClass().getMethods();
        Map<String, Method> m = new HashMap<String, Method>();
        for (Method method : handlerMethods)
            m.put(method.getName(), method);
        this.methodMap = Collections.unmodifiableMap(m);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent ev) {
        ev.getCause().printStackTrace();

        try {
            ev.getChannel().close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
//        long ctime = System.currentTimeMillis();
        HttpRequest request = (HttpRequest) e.getMessage();
        if (request == null) return;

        Map<String, String> map = packRequest(request);
        map.put("uri", request.getUri());
        map.put("RemoteAddress", e.getRemoteAddress().toString());
        map.put("HttpMethod", request.getMethod().getName());
        processOneMessage(e, request, map);
    }

    protected void processOneMessage(MessageEvent e, HttpRequest request, Map<String, String> map) throws Exception {
        // Decide whether to close the connection or not.
        boolean keepAlive = isKeepAlive(request);

        Object handlerResult = null;
        try {
            handlerResult = callMethod("doGet", map);
        } catch (Exception rpc_e) {
            log.error(e.getMessage());
        }


        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.setContent(ChannelBuffers.copiedBuffer((String) handlerResult, CharsetUtil.getEncoder(Charset.forName("utf-8")).charset()));
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (keepAlive) {
            response.setHeader(HttpHeaders.Names.CONNECTION, "keep-alive");
            response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
        }
        ChannelFuture future = e.getChannel().write(response);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }


    protected Object callMethod(String method, Map<String, String> map) throws Exception {
        Method m = methodMap.get(method);
        if (m == null)
            throw new IOException("No such method");
        return m.invoke(handler, map);
    }

    /**
     * @param request
     * @return
     * @throws
     */
    public Map<String, String> parseQueryString(HttpRequest request) {
        if (debug) {
            log.debug("Parse the query string: " + request.getUri());
        }
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        return traversalDecoder(decoder, null);
    }

    /**
     * @param decoder
     * @param parameters
     * @return
     */
    private Map<String, String> traversalDecoder(QueryStringDecoder decoder, Map<String, String> parameters) {
        if (null == parameters) {
            parameters = new HashMap<String, String>();
        }
        Iterator<Map.Entry<String, List<String>>> iterator = decoder.getParameters().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> entry = iterator.next();
            parameters.put(entry.getKey(), entry.getValue().get(0));
        }
        return parameters;
    }

    /**
     * @param request
     * @return
     * @throws Exception
     */
    public Map<String, String> packRequest(HttpRequest request) throws Exception {
        Map<String, String> map = parseQueryString(request);
        if (!request.getMethod().equals(HttpMethod.GET)) {
            throw new IOException("Not support http method except GET!");
        }
        return map;
    }
}
