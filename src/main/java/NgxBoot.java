/*
 * @Description: 
 * @Author: 唐健峰
 * @Date: 2023-06-01 17:07:41
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-02 16:27:59
 */
import cloud.duringbug.server.proxy.ProxyServer;
import cloud.duringbug.utils.Logo;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author 唐健峰
 * @version 1.0
 * @date 2023/6/1 17:07
 * @description:
 */
public class NgxBoot {
    private static final Logger LOGGER = LoggerFactory.getLogger(NgxBoot.class);
    public static void main(String[] args) throws IOException {
        LOGGER.info("NginxBoot开始启动");
        Logo.printLogo();
        ProxyServer proxyServer=new ProxyServer();
        proxyServer.run();
    }
}
