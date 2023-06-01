/*
 * @Description: 
 * @Author: 唐健峰
 * @Date: 2023-06-01 18:41:42
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-01 18:48:39
 */
package cloud.duringbug.utils;

import cloud.duringbug.conf.Config;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XmlToClass {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlToClass.class);
    public static Config getConfig() throws IOException{
        InputStream in = XmlToClass.class.getResourceAsStream("/config.xml");
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        String xml= result.toString("UTF-8");
        xml = xml.replaceAll("(?m)^\\s*<\\?xml(.+?)\\?>\n?", "");
        XStream xs=new XStream();
        xs.addPermission(AnyTypePermission.ANY);
        xs.processAnnotations(Config.class);
        xs.alias("config", Config.class);
        Config config=new Config();
        try {
            config=(Config) xs.fromXML(xml);
        } catch (Exception e) {
            LOGGER.error("config.xml格式出现错误: "+e.getMessage());
            return null;
        }
        return config;
    }
}
