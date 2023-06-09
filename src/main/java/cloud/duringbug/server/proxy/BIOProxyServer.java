/*
 * @Description: 
 * @Author: 唐健峰
 * @Date: 2023-06-08 16:32:56
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-09 12:17:52
 */
package cloud.duringbug.server.proxy;

import cloud.duringbug.server.Interface.ProxyServer;
import cloud.duringbug.utils.XmlToClass;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.String;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;









public class BIOProxyServer implements ProxyServer{
    private static final Logger LOGGER = LoggerFactory.getLogger(BIOProxyServer.class);
    @Override
    public void startServer() throws IOException {
        ServerSocket serverSocket=new ServerSocket(XmlToClass.getConfig().getPort());
        AtomicBoolean shutdown = new AtomicBoolean(false);
        while(!shutdown.get()){
            Socket client = serverSocket.accept();
            new Thread(()->{
                InputStream in;
                try {
                    in=client.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    String request="";
                    while(true){
                        String data=reader.readLine();
                        if(data!=null && !data.trim().equals("")){
                            request=request+data+"\n";
                        }else{
                            break;
                        }
                    }
                    if(request.startsWith("GET")||request.startsWith("POST")){
                        String uri="";
                        int firstSpaceIndex = request.indexOf(' ');
                        int secondSpaceIndex = request.indexOf(' ', firstSpaceIndex + 1);
                        if (firstSpaceIndex != -1 && secondSpaceIndex != -1) {
                            uri = request.substring(firstSpaceIndex + 1, secondSpaceIndex);
                            if(uri.equals("/shutdown")){
                                shutdown.set(true);
                                client.close();
                                LOGGER.info("服务端端退出");
                                Thread.sleep(500);
                                return;
                            }
                        } else {
                            LOGGER.error("非http请求");
                        }

                        OutputStream out=client.getOutputStream();
                        LOGGER.info("<"+client.getInetAddress()+">的请求为"+request);
                        out.write(httpProcessor(request));
                    }
                    client.close();
                    LOGGER.info("客户端退出");
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            }).start();;
        }
    }

    @Override
    public void run() {
        try {
            this.startServer();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
    private byte[] httpProcessor(String request) throws IOException{
        String uri="";
        int firstSpaceIndex = request.indexOf(' ');
        int secondSpaceIndex = request.indexOf(' ', firstSpaceIndex + 1);
        if (firstSpaceIndex != -1 && secondSpaceIndex != -1) {
            uri = request.substring(firstSpaceIndex + 1, secondSpaceIndex);
        } else {
            LOGGER.error("非http请求");
        }
        Map params=getRequestParams(uri);
        if(!params.isEmpty()){
            uri=uri.substring(0, uri.indexOf("?"));
        }
        byte[] body;
        String head="HTTP/1.1 200 OK\n";
        try {
            if(uri.equals("/")){
                throw new FileNotFoundException();
            }
            InputStream in=BIOProxyServer.class.getClassLoader().getResourceAsStream("static"+uri);
            body=in.readAllBytes();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            body= BIOProxyServer.class.getClassLoader().getResourceAsStream("static/404.html").readAllBytes();
            head="HTTP/1.1 404 Not Found\n";
        }
        String contentType="Content-Type: "+getContentType(uri)+";charset=UTF-8\r\n";
        String contentLength="Content-Length: "+body.length+"\r\n\r\n";
          // 使用 ByteArrayOutputStream 构建响应字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // 写入头部信息
        outputStream.write(head.getBytes());
        outputStream.write(contentType.getBytes());
        outputStream.write(contentLength.getBytes());

        // 写入内容部分
        outputStream.write(body);

        // 返回最终的字节数组
        return outputStream.toByteArray();
    }
    private String getContentType(String uri){
        if (uri.endsWith(".html")) {
            return "text/html";
        } else if (uri.endsWith(".xhtml")) {
            return "application/xhtml+xml";
        } else if (uri.endsWith(".xml")) {
            return "application/xml";
        } else if (uri.endsWith(".css")) {
            return "text/css";
        } else if (uri.endsWith(".js")) {
            return "application/javascript";
        } else if (uri.endsWith(".jpg") || uri.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (uri.endsWith(".png")) {
            return "image/png";
        } else if (uri.endsWith(".gif")) {
            return "image/gif";
        } else if (uri.endsWith(".pdf")) {
            return "application/pdf";
        } else if (uri.endsWith(".txt")) {
            return "text/plain";
        } else if (uri.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (uri.endsWith(".ico")) {
            return "image/x-icon";
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    private Map getRequestParams(String uri){
        Map requestParams=new HashMap<String,Object>();
        if(uri==null||uri.trim().equals("")){
            return requestParams;
        }
        int _positon=uri.indexOf("?");
        if(_positon==-1){
            return requestParams;
        }
        String params=uri.substring(_positon+1);
        while(true){
            int _p1=params.indexOf("=");
            int _p2=params.indexOf("&");
            if(_p2==-1){
                requestParams.put(params.substring(0, _p1),params.substring(_p1+1));
                break;
            }else{
                requestParams.put(params.substring(0, _p1),params.substring(_p1+1,_p2));
                params=params.substring(_p2+1);
            }
        }
        return requestParams;
    }
}
