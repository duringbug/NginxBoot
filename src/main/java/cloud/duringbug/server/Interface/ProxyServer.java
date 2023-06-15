package cloud.duringbug.server.Interface;
import java.io.IOException;

public interface ProxyServer{
    public void startServer() throws IOException,InterruptedException;
    public void run();
}
