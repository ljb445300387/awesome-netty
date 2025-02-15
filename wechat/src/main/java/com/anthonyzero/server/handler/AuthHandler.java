package com.anthonyzero.server.handler;

import com.anthonyzero.utils.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author admin
 */
@ChannelHandler.Sharable
public class AuthHandler extends ChannelInboundHandlerAdapter {
    public static final AuthHandler INSTANCE = new AuthHandler();

    private AuthHandler() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //客户端如果第一个指令为非登录指令，AuthHandler 直接将客户端连接关闭
        if (!SessionUtil.hasLogin(ctx.channel())) {
            //如果未登录，直接强制关闭连接
            ctx.channel().close();
            return;
        }
        // 已经登录 就把读到的数据向下传递，传递给后续指令处理器
        //热插拔
        ctx.pipeline().remove(this);
        super.channelRead(ctx, msg);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        if (SessionUtil.hasLogin(ctx.channel())) {
            System.out.println("当前连接登录验证完毕，无需再次验证, AuthHandler被移除");
        } else {
            System.out.println("无登录验证，强制关闭连接!");
        }
    }
}
