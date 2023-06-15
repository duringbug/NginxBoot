/*
 * @Description:
 * @Author: 唐健峰
 * @Date: 2023-06-01 21:25:58
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-15 15:27:34
 */
package cloud.duringbug.utils;
import cloud.duringbug.server.proxy.BIOProxyServer;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;







public class HttpRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);
    public static String handleHttpRequest(SocketChannel channel, String request) throws IOException {
        // 解析请求路径和请求方法
        String[] requestParts = request.split("\\s+");
        String method = requestParts[0];
        String path = requestParts[1];
        System.out.println("method: "+method+" path: "+path);
        if(path.startsWith("http://")){
            // if (!path.startsWith("http://")) {
            //     path = "http://" + path.substring(5); // 以"http://"作为默认协议
            // }
            OkHttpClient client = new OkHttpClient();
            try {
                Request httpRequest = new Request.Builder()
                        .url(path)
                        .build();
                if ("GET".equals(method)) {
                    try (Response httpResponse = client.newCall(httpRequest).execute()) {
                        LOGGER.info("响应状态码：" + httpResponse.code());

                        String response = httpResponse.body().string();
                        byte[] responseBytes = response.getBytes("UTF-8");
                        int contentLength = responseBytes.length;
                        response = "HTTP/1.1 " + httpResponse.code() + " "+httpResponse.message()+"\n" +
                        "Content-Type: "+httpResponse.header("Content-Type")+";charset=UTF-8\r\n" +
                        "Content-Length: " + contentLength + "\r\n\r\n" +
                        response;

                        // LOGGER.info("响应结果：" + response);
                        // 将响应内容写入 SocketChannel
                        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
                        while (buffer.hasRemaining()) {
                            // 非阻塞写操作，可能会造成部分数据未发送成功
                            channel.write(buffer);
                        }
                        return response;
                    }
                } else if ("POST".equals(method)) {
                    LOGGER.info("处理post请求");
                    // 处理POST请求
                    String requestBody = request.substring(request.indexOf("\r\n\r\n") + 4);
                    RequestBody body = RequestBody.create(requestBody, MediaType.get("application/x-www-form-urlencoded"));
                    Request httpRequest2 = new Request.Builder()
                            .url(path)
                            .post(body)
                            .build();
                    try (Response httpResponse = client.newCall(httpRequest2).execute()) {
                        LOGGER.info("响应状态码：" + httpResponse.code());

                        String response = httpResponse.body().string();
                        byte[] responseBytes = response.getBytes("UTF-8");
                        int contentLength = responseBytes.length;
                        response = "HTTP/1.1 " + httpResponse.code() + " "+httpResponse.message()+"\n" +
                        "Content-Type: "+httpResponse.header("Content-Type")+";charset=UTF-8\r\n" +
                        "Content-Length: " + contentLength + "\r\n\r\n" +
                        response;

                        // LOGGER.info("响应结果：" + response);
                        // 将响应内容写入 SocketChannel
                        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
                        while (buffer.hasRemaining()) {
                            // 非阻塞写操作，可能会造成部分数据未发送成功
                            channel.write(buffer);
                        }
                        return response;
                    }
                } else {
                    // 其他请求方法暂不处理，返回 501 Not Implemented
                    String response = "HTTP/1.1 501 Not Implemented\r\nContent-Type: text/plain;charset=UTF-8\r\nContent-Length: 0\r\n\r\n";
                    ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
                    channel.write(buffer);
                }
            }catch (Exception e){
                LOGGER.warn(e.getMessage());
            }
        }
        else{
            // TODO 本地
        }
        return null;
    }
    public static String localHandleHttpRequest(SocketChannel channel, String request) throws IOException, InterruptedException{
        if(request.startsWith("GET")){
            String uri="";
            int firstSpaceIndex = request.indexOf(' ');
            int secondSpaceIndex = request.indexOf(' ', firstSpaceIndex + 1);
            if (firstSpaceIndex != -1 && secondSpaceIndex != -1) {
                uri = request.substring(firstSpaceIndex + 1, secondSpaceIndex);
                if(uri.equals("/shutdown")){
                    LOGGER.info("服务端端退出");
                    Thread.sleep(500);
                    return "stop";
                }
            }else {
                LOGGER.error("非http请求");
            }
            return new String(httpGetProcessor(request));
        }else if(request.startsWith("POST")){
            String uri="";
            int firstSpaceIndex = request.indexOf(' ');
            int secondSpaceIndex = request.indexOf(' ', firstSpaceIndex + 1);
            if (firstSpaceIndex != -1 && secondSpaceIndex != -1) {
                uri = request.substring(firstSpaceIndex + 1, secondSpaceIndex);
                if(uri.equals("/shutdown")){
                    LOGGER.info("服务端端退出");
                    Thread.sleep(500);
                    return "stop";
                }
            }else {
                LOGGER.error("非http请求");
            }
            return new String(httpPostProcessor(request));
        }
        return null;
    }

    private static  byte[] httpPostProcessor(String request) throws IOException{
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
    private static byte[] httpGetProcessor(String request) throws IOException{
        if(request.startsWith("http://")){
            request=request.substring(7);
        }
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
    private static String getContentType(String uri){
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
    private static Map getRequestParams(String uri){
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

