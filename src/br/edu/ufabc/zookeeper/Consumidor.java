package br.edu.ufabc.zookeeper;

import java.util.List;
import java.util.Random;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;

import br.edu.ufabc.zookeeper.util.Nos;

import org.apache.zookeeper.ZooKeeper;

public class Consumidor implements Runnable, Watcher {

	public static void main(String[] args) throws Exception {
		new Thread(new Consumidor()).start();
	}

	private ZooKeeper zooKeeper;

	public Consumidor() throws Exception {
		// Conectar com o ZooKeeper
		zooKeeper = new ZooKeeper("localhost", 3_000, this);

		// Registra o watch da fila
		zooKeeper.getChildren("/queue", true);
	}

	@Override
	public void run() {
		while (true) {

		}
	}

	@Override
	public void process(WatchedEvent event) {
		if (EventType.NodeChildrenChanged == event.getType()) {
			System.out.println("Um novo item foi inserido na fila");

			boolean tenhoLock = false;

			try {
				// Vai dormir antes de processar o nó
				Thread.sleep(new Random().nextInt(3_000));

				System.out.println("Tentado pegar o lock da fila...");

				// Tenta pegar o lock
				zooKeeper.create("/lock", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				tenhoLock = true;

				System.out.println("Consegui o lock! :) Vamos trabalhar!");

				List<String> nos = zooKeeper.getChildren("/queue", true);
				String maisNovo = "/queue/" + Nos.maisNovo(nos);

				System.out.println("O nó " + maisNovo + " será processado");

				byte[] bytes = zooKeeper.getData(maisNovo, true, null);
				int numero = Integer.parseInt(new String(bytes, "UTF-8"));
				System.out.println("Recebi o número: " + numero);

				Thread.sleep(3_000);
			} catch (KeeperException e) {
				System.out.println("Não consegui pegar o lock, não foi dessa vez :(");

				// Continua monitorando a fila
				try {
					zooKeeper.getChildren("/queue", true);
				} catch (KeeperException | InterruptedException e1) {
					// TODO O que fazer?
				}
			} catch (Exception e) {
				// TODO O que fazer?
			} finally {
				// Libera o lock
				if (tenhoLock) {
					try {
						System.out.println("Estou liberando o lock");

						zooKeeper.delete("/lock", -1);

						System.out.println("Lock liberado com sucesso");
					} catch (Exception e) {
						// TODO O que fazer?
					}

				}
			}
		}
	}
}
