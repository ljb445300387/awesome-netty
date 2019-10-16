package com.anthonyzero.server.handler;

import com.anthonyzero.protocol.request.MessageRequestPacket;
import com.anthonyzero.protocol.response.MessageResponsePacket;
import com.anthonyzero.session.Session;
import com.anthonyzero.utils.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 一对一聊天
 *
 * @author admin
 */
@ChannelHandler.Sharable
public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {
    public static final MessageRequestHandler INSTANCE = new MessageRequestHandler();

    private MessageRequestHandler() {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                MessageRequestPacket messageRequestPacket) throws Exception {
        //1.拿到当前消息发送方的会话信息
        Session session = SessionUtil.getSession(channelHandlerContext.channel());

        //2. 拿到消息接收方的channel
        Channel toUserChannel = SessionUtil.getChannel(messageRequestPacket.getToUserId());

        //3. 构造发送消息
        MessageResponsePacket response = new MessageResponsePacket();
        response.setFromUserId(session.getUserId());
        response.setFromUserName(session.getUserName());

        //4.成功将消息发送给消息接收方 失败发给自己提示失败消息
        if (toUserChannel != null && SessionUtil.hasLogin(toUserChannel)) {
            response.setSuccess(true);
            response.setMessage(messageRequestPacket.getMessage());
            toUserChannel.writeAndFlush(response);
        } else {
            response.setSuccess(false);
            response.setMessage("[" + messageRequestPacket.getToUserId() + "] 不在线，发送失败!");
            channelHandlerContext.channel().writeAndFlush(response);
        }
    }
}
