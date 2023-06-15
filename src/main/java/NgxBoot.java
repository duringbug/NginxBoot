/*
 * @Description: 
 * @Author: 唐健峰
 * @Date: 2023-06-01 17:07:41
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-15 15:04:55
 */
import cloud.duringbug.start.NgxServer;
import cloud.duringbug.utils.Logo;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NgxBoot {
    private static final Logger LOGGER = LoggerFactory.getLogger(NgxBoot.class);
    public static void main(String[] args) throws IOException {
        LOGGER.info("NginxBoot开始启动");
        Logo.printLogo();
        NgxServer ngxServer=new NgxServer("/ngxboot.xml");
        ngxServer.start();
        LOGGER.info("NginxBoot启动成功");
    }
}
