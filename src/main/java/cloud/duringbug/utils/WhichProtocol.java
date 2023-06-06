/*
 * @Description: 
 * @Author: 唐健峰
 * @Date: 2023-06-01 21:17:26
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-06 20:35:53
 */
package cloud.duringbug.utils;

import cloud.duringbug.server.Enum.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WhichProtocol {
    private static final Logger LOGGER = LoggerFactory.getLogger(WhichProtocol.class);
    public static Protocol getProtocol(String data){
        if (data.startsWith("GET") || data.startsWith("POST") || data.startsWith("HEAD")) {
            // LOGGER.info("服务端已收到 HTTP/1.x 请求: \n" + data);
            LOGGER.info("服务端已收到 HTTP/1.x 请求: \n" );
            return Protocol.HTTP1;
        } else {
            LOGGER.info("服务端已收到其他协议请求: " );
            // 进行其他协议的处理
            return Protocol.ELSE;
        }
    }
}
