package br.edu.ufabc.zookeeper;

public class Produtor implements Runnable {

	public static void main(String[] args) {
		new Thread(new Produtor()).start();
	}

	@Override
	public void run() {
		while (true) {

		}
	}

}
