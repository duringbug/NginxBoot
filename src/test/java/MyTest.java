/*
 * @Description: 
 * @Author: 唐健峰
 * @Date: 2023-06-01 18:07:18
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-15 15:11:47
 */

import cloud.duringbug.server.Interface.ProxyServer;
import cloud.duringbug.server.proxy.FastNIOServer;
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
        System.out.println(XmlToClass.getConfig("/ngxboot.xml"));
    }
    @Test
    public void testProxyServer() throws IOException{
        ProxyServer proxyServer=new FastNIOServer("/ngxboot.xml");
        proxyServer.run();
    }
}
