/*
 * @Description:
 * @Author: 唐健峰
 * @Date: 2023-06-01 21:25:58
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-06 21:35:32
 */
package cloud.duringbug.utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
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
        if(path.startsWith("/api")){
            if (!path.startsWith("http://")) {
                path = "http://" + path.substring(5); // 以"http://"作为默认协议
            }
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
                        LOGGER.info("响应结果：" + response);

                        // 将响应内容写入 SocketChannel
                        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
                        channel.write(buffer);
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
}
