/*
 * @Description: 
 * @Author: 唐健峰
 * @Date: 2023-06-08 16:29:17
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-08 18:02:42
 */
package cloud.duringbug.start;

import cloud.duringbug.conf.Config;
import cloud.duringbug.server.Interface.ProxyServer;
import cloud.duringbug.server.proxy.BIOProxyServer;
import cloud.duringbug.server.proxy.NIOProxyServer;
import cloud.duringbug.utils.XmlToClass;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class NgxServer extends Thread{
    private static final Logger LOGGER = LoggerFactory.getLogger(NgxServer.class);
    ProxyServer proxyServer;
    public NgxServer() throws IOException{
        Config config=XmlToClass.getConfig();
        if(config.getProxyType().equals("NIOServer")){
            proxyServer=new NIOProxyServer();
            proxyServer.run();
        }
        else if(config.getProxyType().equals("BIOServer")){
           proxyServer=new BIOProxyServer();
           proxyServer.run();
        }
        else{
            LOGGER.error("ngxboot.xml中的"+config.getProxyType()+"为无效类型服务器");
        }
    }
    @Override
    public void run() {
        this.proxyServer.run();
    }
}
