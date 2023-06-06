/*
 * @Description: 
 * @Author: 唐健峰
 * @Date: 2023-06-01 19:11:33
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-06 20:43:14
 */
package cloud.duringbug.server.proxy;
import cloud.duringbug.conf.Config;
import cloud.duringbug.server.Enum.Protocol;
import cloud.duringbug.utils.HttpRequest;
import cloud.duringbug.utils.WhichProtocol;
import cloud.duringbug.utils.XmlToClass;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;







public class ProxyServer extends Thread{
    Config config;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyServer.class);
    private static int BYTE_LENGTH = 0x6f6f6f;
    private Selector selector;
    public ProxyServer() throws IOException{
        this.config=XmlToClass.getConfig();
    }
    private void accept(SelectionKey key) throws IOException { 
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel(); 
        SocketChannel channel = serverChannel.accept(); 
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress(); 
        LOGGER.info("已连接: " + remoteAddr);
        // 监听读事件
        channel.register(this.selector, SelectionKey.OP_READ);
    }
    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BYTE_LENGTH);
        int numRead = -1;
        try {
            numRead = channel.read(buffer);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        if (numRead == -1) {
            Socket socket = channel.socket();
            SocketAddress remoteAddr = socket.getRemoteSocketAddress(); 
            LOGGER.info("连接关闭: " + remoteAddr); 
            channel.close();
            key.cancel();
            return;
        }
        byte[] data = new byte[numRead]; 
        System.arraycopy(buffer.array(), 0, data, 0, numRead); 
        String protocol=new String(data);
        LOGGER.info("服务端已收到消息: ");
        if(WhichProtocol.getProtocol(protocol).equals(Protocol.HTTP1)){
            channel.register(this.selector, SelectionKey.OP_WRITE,HttpRequest.handleHttpRequest(channel, protocol)); 
        }
        // 构造要写入的数据
    }
    private void write(SelectionKey key) throws IOException {
        LOGGER.info("进入write方法");
        SocketChannel channel = (SocketChannel) key.channel();
        String response = (String) key.attachment();
        if(response==null){
            return;
        }
        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    public void startServer() throws IOException{
        this.selector = Selector.open();
        // ServerSocketChannel与serverSocket类似
        ServerSocketChannel serverSocket = ServerSocketChannel.open(); 
        serverSocket.socket().bind(new InetSocketAddress(config.getPort()));
        // 设置无阻塞
        serverSocket.configureBlocking(false);
        // 将channel注册到selector 
        serverSocket.register(this.selector, SelectionKey.OP_ACCEPT); 
        LOGGER.info("服务端已启动");
        for (;;) {
            // 操作系统提供的非阻塞I/O
            int readyCount = selector.select(); 
            if (readyCount == 0) {
                continue; 
            }
            // 处理准备完成的fd
            Set<SelectionKey> readyKeys = selector.selectedKeys(); 
            Iterator iterator = readyKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = (SelectionKey) iterator.next();
                iterator.remove();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    this.accept(key);
                }else if (key.isReadable()){
                    this.read(key);
                }
                else if (key.isWritable()){
                    this.write(key);
                    key.channel().register(this.selector, SelectionKey.OP_READ);
                }
            }
        }
    }
    @Override
    public void run() {
        try {
            new ProxyServer().startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
