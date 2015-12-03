package br.edu.ufabc.zookeeper;

import java.util.List;
import java.util.Scanner;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import br.edu.ufabc.zookeeper.util.Nos;

public class Produtor implements Runnable, Watcher {

	public static void main(String[] args) throws Exception {
		new Thread(new Produtor()).start();
	}

	private Scanner input;

	private ZooKeeper zooKeeper;

	private String meuNo;

	private volatile boolean souLider;

	public Produtor() throws Exception {
		// Cria o leitor do teclado
		input = new Scanner(System.in);

		// Conectar com o ZooKeeper
		zooKeeper = new ZooKeeper("localhost", 3_000, this);

		// Cria seu nó para leader election
		meuNo = zooKeeper.create("/leaders/node", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println("Nó " + meuNo + " para eleição criado com sucesso.");

		// Registra o watch no diretório de leaders
		List<String> nos = zooKeeper.getChildren("/leaders", true);

		String maisVelho = Nos.maisVelho(nos);

		// Se não tem nenhum nó ele vira líder
		souLider = meuNo.endsWith(maisVelho);
	}

	@Override
	public void run() {
		while (true) {
			if (souLider) {
				// Lê o próximo número do usuário
				System.out.println("Entre com um número positivo ou -1 para sair:");
				int numero = input.nextInt();

				// Se for -1 sai do programa
				if (numero == -1) {
					System.exit(0);
					break;
				}

				// Caso contrário envia o número para a fila
				try {
					String noDoNumero = zooKeeper.create("/queue/node", String.valueOf(numero).getBytes(),
							Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
					System.out.println("O número " + numero + " foi enviado para a fila dentro do nó " + noDoNumero);
				} catch (KeeperException | InterruptedException e) {
					// TODO O que fazer?
				}
			}
		}
	}

	@Override
	public void process(WatchedEvent event) {
		// Algum nó entrou ou saiu do grupo
		if (EventType.NodeChildrenChanged == event.getType()) {
			System.out.println("Iniciando a eleição...");
			try {
				// Espera até o nó antigo sair do diretório
				Thread.sleep(3_000);

				// Verifica quem está no grupo
				List<String> nos = zooKeeper.getChildren("/leaders", true);

				// O líder será o nó mais velho
				String noMaisVelho = Nos.maisVelho(nos);
				System.out.println("O nó mais velho é " + noMaisVelho);
				if (meuNo.endsWith(noMaisVelho)) {
					System.out.println("Venci a eleição! Chegou minha vez :)");
					souLider = true;
				} else {
					System.out.println("Ainda não foi dessa vez :(");
					souLider = false;
				}
			} catch (KeeperException | InterruptedException e) {
				// TODO O que fazer?
			}
		}
	}

}
