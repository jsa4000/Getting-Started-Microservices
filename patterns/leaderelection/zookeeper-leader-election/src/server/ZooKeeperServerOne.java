package server;

import java.io.IOException;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;

public class ZooKeeperServerOne {
	
	/*******************************************************************************
	 * RUN SERVER ONE
	 ******************************************************************************/
	public static void main(String[] args){
		
		ZooKeeperServerMain zkserver = new ZooKeeperServerMain();
		ServerConfig sc = new ServerConfig();
		QuorumPeerConfig qpc = new QuorumPeerConfig(); 
		
		try {
			qpc.parse("conf/zoo1.cfg");
		} catch (ConfigException e) {
			e.printStackTrace();
		}
		
		sc.readFrom(qpc);
		
		System.out.println("SERVER ONE :: STARTED\n");
		
		try {
			zkserver.runFromConfig(sc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("SERVER ONE :: AFTER runFromConfig. Something is very wrong.\n");
		
	}
	
}
