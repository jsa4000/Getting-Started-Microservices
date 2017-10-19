package client;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class ZooKeeperClientOne  implements Watcher {
	
	private ZooKeeper zk;
	
	private final String PATH = "/election";
	
	public ZooKeeperClientOne() throws IOException, KeeperException, InterruptedException{
		
		/********************
		 * STARTING ZOOKEEPER
		 ********************/
		System.out.println("CLIENT ONE :: STARTING\n");	
		zk = new ZooKeeper("127.0.0.1", 3000, this);
		System.out.println("CLIENT ONE :: FINISHED STARTING\n");
		
		// Leader Election
		leaderElection();
		
		int pause = new Scanner(System.in).nextInt();
	}

	/**
	 * Leader Election
	 * @throws InterruptedException 
	 * @throws KeeperException
	 */
	public void leaderElection() throws KeeperException, InterruptedException{
		
		// If is the first client, then it should create the znode "/election"
		Stat stat = zk.exists(PATH, false);
		if(stat == null){
			System.out.println("CLIENT ONE :: Im the first client, creating " + PATH + ".");
			String election = "/election";
			String r = zk.create(election, new byte[0], Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
			System.out.println("CLIENT ONE :: " + r + " created.");
		}
		
		// Create znode z with path "ELECTION/n_" with both SEQUENCE and EPHEMERAL flags
		String childPath = PATH + "/n_";
		
		childPath = zk.create(childPath, new byte[0], Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println("CLIENT ONE :: My leader proposal created. Path = " + childPath + ".");
		
		// Let C be the children of "ELECTION", and i be the sequence number of z;
		// Watch for changes on "ELECTION/n_j", where j is the smallest sequence
		// number such that j < i and n_j is a znode in C;
		List<String> children = zk.getChildren(PATH, false);
		
		String tmp = children.get(0);
		
		for(String s : children){
			if(tmp.compareTo(s) > 0)
				tmp = s;	
		}
		
		// i contains the smallest sequence number
		//String leader = PATH + "/n_" + i;
		String leader = PATH + "/" + tmp;
		Stat s = zk.exists(leader, true);
		
		// syso
		System.out.println("CLIENT ONE :: Leader is the owner of znode: " + leader);
		System.out.println("CLIENT ONE :: Leader id: " + s.getEphemeralOwner());
		
		/*
		Let ELECTION be a path of choice of the application. To volunteer to be a leader:
		1.Create znode z with path "ELECTION/n_" with both SEQUENCE and EPHEMERAL flags;
		2.Let C be the children of "ELECTION", and i be the sequence number of z;
		3.Watch for changes on "ELECTION/n_j", where j is the smallest sequence number such that j < i and n_j is a znode in C;

		Upon receiving a notification of znode deletion:
		1.Let C be the new set of children of ELECTION;
		2.If z is the smallest node in C, then execute leader procedure;
		3.Otherwise, watch for changes on "ELECTION/n_j", where j is the smallest sequence number such that j < i and n_j is a znode in C;
		 */
	}
	
	public void newLeaderElection() throws KeeperException, InterruptedException{
		
		List<String> children = zk.getChildren(PATH, false);
		
		String tmp = children.get(0);
		
		for(String s : children){
			if(tmp.compareTo(s) > 0)
				tmp = s;	
		}
		
		// i contains the smallest sequence number
		String leader = PATH + "/" + tmp;
		Stat s = zk.exists(leader, true);

		// syso
		System.out.println("CLIENT ONE :: Leader is the owner of znode: " + leader);
		System.out.println("CLIENT ONE :: Leader id: " + s.getEphemeralOwner());
	}
	

	@Override
	public void process(WatchedEvent event) {
				
		switch (event.getType()){
		
		case NodeChildrenChanged:
			System.out.println("CLIENT ONE :: NodeChildrenChanged | ZNode: " + event.getPath());
			break;
			
		case NodeCreated:
			System.out.println("CLIENT ONE :: NodeCreated | ZNode: " + event.getPath());
			break;
		           
		case NodeDataChanged:
			System.out.println("CLIENT ONE :: NodeDataChanged | ZNode: " + event.getPath());
			break;
		           
		case NodeDeleted:
			System.out.println("CLIENT ONE :: NodeDeleted | ZNode: " + event.getPath());
			System.out.println("CLIENT ONE :: Leader was lost, newLeaderElection started.");
			try {
				newLeaderElection();
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
			
		case None:
			
			switch (event.getState()){
			
			case Disconnected:
				System.out.println("CLIENT ONE :: Disconnected.");
				break;
				
			case Expired:
				System.out.println("CLIENT ONE :: Expired.");
				break;
		
			case NoSyncConnected:
				System.out.println("CLIENT ONE :: NoSyncConnected - Deprecated");
				break;
				
			case SyncConnected:
				System.out.println("CLIENT ONE :: SyncConnected.");
				break;
			
			case Unknown:
				System.out.println("CLIENT ONE :: Unknown - Deprecated");
				break;
			}
			
		}
		
	}


	public static void main (String[] args){

		try {
			new ZooKeeperClientOne();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
