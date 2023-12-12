## webSocket开发模块任务

### webSocket server
```java
@Slf4j
@Configuration
public class NettyWebSocketServer {
    public static final int WEB_SOCKET_PORT = 8090;
    public static final NettyWebSocketServerHandler NETTY_WEB_SOCKET_SERVER_HANDLER = new NettyWebSocketServerHandler();
    // 创建线程池执行器
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors());

    /**
     * 启动 ws server
     *
     * @return
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {
        run();
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        Future<?> future = bossGroup.shutdownGracefully();
        Future<?> future1 = workerGroup.shutdownGracefully();
        future.syncUninterruptibly();
        future1.syncUninterruptibly();
        log.info("关闭 ws server 成功");
    }

    public void run() throws InterruptedException {
        // 服务器启动引导对象
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.INFO)) // 为 bossGroup 添加 日志处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //30秒客户端没有向服务器发送心跳则关闭连接
                        pipeline.addLast(new IdleStateHandler(30, 0, 0));
                        // 因为使用http协议，所以需要使用http的编码器，解码器
                        pipeline.addLast(new HttpServerCodec());
                        // 以块方式写，添加 chunkedWriter 处理器
                        pipeline.addLast(new ChunkedWriteHandler());
                        /**
                         * 说明：
                         *  1. http数据在传输过程中是分段的，HttpObjectAggregator可以把多个段聚合起来；
                         *  2. 这就是为什么当浏览器发送大量数据时，就会发出多次 http请求的原因
                         */
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        //保存用户ip
                        pipeline.addLast(new HttpHeadersHandler());
                        /**
                         * 说明：
                         *  1. 对于 WebSocket，它的数据是以帧frame 的形式传递的；
                         *  2. 可以看到 WebSocketFrame 下面有6个子类
                         *  3. 浏览器发送请求时： ws://localhost:7000/hello 表示请求的uri
                         *  4. WebSocketServerProtocolHandler 核心功能是把 http协议升级为 ws 协议，保持长连接；
                         *      是通过一个状态码 101 来切换的
                         */
                        pipeline.addLast(new WebSocketServerProtocolHandler("/"));
                        // 自定义handler ，处理业务逻辑
                        pipeline.addLast(NETTY_WEB_SOCKET_SERVER_HANDLER);
                    }
                });
        // 启动服务器，监听端口，阻塞直到启动成功
        serverBootstrap.bind(WEB_SOCKET_PORT).sync();
    }

}
```

### webSocket 处理器
```java
@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketService webSocketService;

    // 当web客户端连接后，触发该方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.webSocketService = getService();
    }

    // 客户端离线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx);
    }

    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 可能出现业务判断离线后再次触发 channelInactive
        log.warn("触发 channelInactive 掉线![{}]", ctx.channel().id());
        userOffLine(ctx);
    }

    private void userOffLine(ChannelHandlerContext ctx) {
        this.webSocketService.removed(ctx.channel());
        ctx.channel().close();
    }

    /**
     * 心跳检查
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 关闭用户的连接
                userOffLine(ctx);
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            this.webSocketService.connect(ctx.channel());
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            if (StrUtil.isNotBlank(token)) {
                this.webSocketService.authorize(ctx.channel(), new WSAuthorize(token));
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    // 处理异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("异常发生，异常消息 ={}", cause);
        ctx.channel().close();
    }

    private WebSocketService getService() {
        return SpringUtil.getBean(WebSocketService.class);
    }

    // 读取客户端发送的请求报文
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        WSBaseReq wsBaseReq = JSONUtil.toBean(msg.text(), WSBaseReq.class);
        WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsBaseReq.getType());
        switch (wsReqTypeEnum) {
            case LOGIN:
                this.webSocketService.handleLoginReq(ctx.channel());
                log.info("请求二维码 = " + msg.text());
                break;
            case HEARTBEAT:
                break;
            default:
                log.info("未知类型");
        }
    }
}
```

#### 事件

channelActive(ChannelHandlerContext ctx) 方法：

1、该方法在通道激活时被调用，表示与远程对等方建立了连接。
2、当与客户端建立起连接并且通道处于活动状态时，该方法将被触发。
3、一般可以在这里执行与客户端建立连接后的初始化工作或发送初始消息等操作。
userEventTriggered(ChannelHandlerContext ctx, Object evt) 方法：

userEventTriggered(ChannelHandlerContext ctx, Object evt) 方法：

1、该方法在自定义事件被触发时被调用。
2、Netty 允许用户触发自定义事件，可以通过 ChannelPipeline 发送事件来通知 ChannelHandler。
3、这个方法可以用来处理自定义事件，例如心跳检测、空闲超时等。
4、evt 参数表示触发的自定义事件对象，可以根据具体的事件类型进行处理。



channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) 方法：

1、该方法在接收到新的消息时被调用。
2、当从客户端接收到数据帧（Frame）时，该方法将被触发。
3、TextWebSocketFrame 是 Netty 提供的用于处理文本 WebSocket 帧的特定类型。
4、可以在这里处理客户端发送的消息，例如解析消息内容、处理业务逻辑、给客户端发送响应等操作。



