/*
 * @Description: 
 * @Author: 唐健峰
 * @Date: 2023-06-01 17:51:27
 * @LastEditors: ${author}
 * @LastEditTime: 2023-06-08 16:38:28
 */
package cloud.duringbug.conf;



public class Config {

  


    private int port;
    private String password;
    private String proxyType;

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProxyType() {
        return proxyType;
    }

    @Override
    public String toString() {
        return "Config [port=" + port + ", password=" + password + ", proxyType=" + proxyType + "]";
    }


}
