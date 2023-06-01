/*
 * @Description: 
 * @Author: 唐健峰
 * @Date: 2023-06-01 20:18:26
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-01 20:38:35
 */
package cloud.duringbug.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class FeedBack {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeedBack.class);
    public void HTTP1_1(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        // 构造要写入的数据
        String response = "Hello, client!";
        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
        while (buffer.hasRemaining()) {
            // 非阻塞写操作，可能会造成部分数据未发送成功
            channel.write(buffer);
        }
        // 检查是否已经全部发送完成
        if (!buffer.hasRemaining()){
            LOGGER.info("服务端已发送响应: " + response);
        }
        return;
    }
}
