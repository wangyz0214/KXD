package message;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketIsok {
	
	public static boolean isHostConnectable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port),3000);
        } catch (IOException e) {
            //e.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
	public static String getProperty() {
		String[] hosts={"10.10.152.31","10.10.152.31","10.10.152.33"};
		String key = null;
		int[] ports={2181,2182,2181};
		boolean istrue=false;
		for(int i=0;i<hosts.length;i++){
			istrue=isHostConnectable(hosts[i],ports[i]);
			key = hosts[i] +":" + ports[i];
			if(istrue){
				break;
			}
		}
		System.out.println(key);
		return key;
	}
}
