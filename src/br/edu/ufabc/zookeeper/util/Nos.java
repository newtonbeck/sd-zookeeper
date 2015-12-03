package br.edu.ufabc.zookeeper.util;

import java.util.Collections;
import java.util.List;

public class Nos {

	public static String maisVelho(List<String> nos) {
		return Collections.min(nos);
	}

	public static String maisNovo(List<String> nos) {
		return Collections.max(nos);
	}

}
