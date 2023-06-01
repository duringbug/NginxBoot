/*
 * @Description: 
 * @Author: 唐健峰
 * @Date: 2023-06-01 17:51:27
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-01 20:04:25
 */
package cloud.duringbug.conf;

public class Config {

    @Override
    public String toString() {
        return "Config [port=" + port + ", password=" + password + "]";
    }

    private int port;
    private String password;

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }
}
