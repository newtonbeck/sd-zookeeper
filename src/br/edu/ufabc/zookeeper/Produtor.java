package br.edu.ufabc.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class Produtor implements Runnable, Watcher {

	public static void main(String[] args) throws Exception {
		new Thread(new Produtor()).start();
	}

	private ZooKeeper zooKeeper;

	public Produtor() throws Exception {
		zooKeeper = new ZooKeeper("localhost", 3_000, this);

		// Cria seu nó para leader election
		zooKeeper.create("/leaders/node", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
	}

	@Override
	public void run() {
		while (true) {

		}
	}

	@Override
	public void process(WatchedEvent event) {

	}

}
