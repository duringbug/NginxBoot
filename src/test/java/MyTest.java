/*
 * @Description: 
 * @Author: 唐健峰
 * @Date: 2023-06-01 18:07:18
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-01 19:59:28
 */

import cloud.duringbug.server.proxy.NIOProxyServer;
import cloud.duringbug.utils.XmlToClass;
import java.io.IOException;
import org.junit.Test;








public class MyTest {
    @Test
    public void testMain() throws IOException{
        NgxBoot ngxBoot=new NgxBoot();
        ngxBoot.main(null);
    }
    @Test
    public void testXml() throws IOException{
        System.out.println(XmlToClass.getConfig());
    }
    @Test
    public void testProxyServer() throws IOException{
        NIOProxyServer proxyServer=new NIOProxyServer();
        proxyServer.run();
    }
}
