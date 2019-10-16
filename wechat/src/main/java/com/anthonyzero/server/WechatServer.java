package com.anthonyzero.server;

import com.anthonyzero.codec.PacketCodecHandler;
import com.anthonyzero.codec.Splitter;
import com.anthonyzero.handler.ImIdleStateHandler;
import com.anthonyzero.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Date;

/**
 * @author admin
 */
public class WechatServer {

    private static final int PORT = 7000;

    public static void main(String[] args) {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(PORT))
                    //临时存放已完成三次握手的请求的队列的最大长度
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //开启TCP底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //开启Nagle算法，true表示关闭，false表示开启
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) {
                            // handler顺序 从上到下
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            // 空闲检测
                            pipeline.addLast(new ImIdleStateHandler());
                            //拆包器 decode
                            pipeline.addLast(new Splitter());
                            //编码解码器
                            pipeline.addLast(PacketCodecHandler.INSTANCE);
                            pipeline.addLast(LoginRequestHandler.INSTANCE);
                            // 回复客户端心跳包
                            pipeline.addLast(HeartBeatRequestHandler.INSTANCE);
                            pipeline.addLast(AuthHandler.INSTANCE);
                            //聊天请求处理器
                            pipeline.addLast(ImHandler.INSTANCE);
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println(new Date() + ": 端口[" + PORT + "]绑定成功!");
                } else {
                    System.err.println("端口[" + PORT + "]绑定失败!");
                }
            });
            //执行,主线程变为wait状态
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
