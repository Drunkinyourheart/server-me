package org.jerry.service.server;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * 类HttpServerPipelineFactory.java的实现描述：TODO 类实现描述
 */
public class HttpServerPipelineFactory implements ChannelPipelineFactory {
    private final HttpServerHandler handler;

    @SuppressWarnings("unused")
    private final boolean isStream;

    public HttpServerPipelineFactory(Object userHandler, boolean isStream) {
        this.handler = new HttpServerHandler(userHandler);
        this.isStream = isStream;
    }

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("handler", handler);
        return pipeline;
    }
}
