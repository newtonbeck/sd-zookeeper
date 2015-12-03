package br.edu.ufabc.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class RecebendoNotificacao implements Watcher {

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		new RecebendoNotificacao().executa();
	}

	public void executa() throws IOException, InterruptedException, KeeperException {
		ZooKeeper zooKeeper = new ZooKeeper("localhost", 3000, this);
		zooKeeper.getChildren("/foo", true);
		while (true) {
			Thread.sleep(2_000);
		}
	}

	/**
	 * Pra ver funcionar entra no zkCli.sh e cria ou deleta um nó dentro de /foo
	 */
	@Override
	public void process(WatchedEvent event) {
		System.out.printf("Recebi o evento do tipo %s e do caminho %s\n", event.getType(), event.getPath());
	}

}
