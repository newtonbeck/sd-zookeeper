package br.edu.ufabc.zookeeper;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

public class Consumidor implements Runnable, Watcher {

	static Integer mutex = 0;
	
	public Consumidor() throws Exception {
		ZooKeeper zk = new ZooKeeper("localhost", 3_000, this);
		System.out.println("Consumidor criado...");
	}
	
	public static void main(String[] args) throws Exception {
		Consumidor consumidor = new Consumidor();
		consumidor.run();
	}
	
	@Override
	public void run() {
		try {
			ZooKeeper zk = new ZooKeeper("localhost", 3_000, this);
			
			byte[] lockNode;
			System.out.println("Consumidor esperando por lock...");
			
			while(zk.exists("/lock", false) != null);
			
			synchronized(mutex) {
				//Agora nós temos o lock!
				System.out.println("Consumidor adquiriu lock!");
				
				zk.create("/lock", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				
				byte[] bytes = zk.getData("/queue/node", true, null);
				int decoded = Integer.parseInt(new String(bytes, "UTF-8")); 
				processarInformacao(decoded);
				
				//Libera o diretório de lock
				System.out.println("Consumidor liberando seu lock!");
				zk.delete("/lock", -1);
				mutex.notify();
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	private void processarInformacao(int data) {
		System.out.println("Consumidor recebeu: " + data + "!");
	}
	
	@Override
	public void process(WatchedEvent event) {
		//Do nothing
	}
}
