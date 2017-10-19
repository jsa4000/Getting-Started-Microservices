package server;

import java.io.IOException;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;

public class ZooKeeperServerThree {

	/*******************************************************************************
	 * RUN SERVER THREE
	 ******************************************************************************/
	public static void main(String[] args){
		
		ZooKeeperServerMain zkserver = new ZooKeeperServerMain();
		ServerConfig sc = new ServerConfig();
		QuorumPeerConfig qpc = new QuorumPeerConfig(); 
		
		try {
			qpc.parse("conf/zoo3.cfg");
		} catch (ConfigException e) {
			e.printStackTrace();
		}
		
		sc.readFrom(qpc);
		
		System.out.println("SERVER THREE :: STARTED\n");
		
		try {
			zkserver.runFromConfig(sc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("SERVER THREE :: AFTER runFromConfig =O\n");
		
	}
	
}
