import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jdk.nashorn.internal.runtime.FindProperty;

public class DicoServer {

	static final int PORT = 1990;
	static private Set<String> dicto = new HashSet<String>();
	static int MIN_SIZE = -1;
	static int MAX_SIZE = -1;

	static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	static Condition dicoReadyCond = readWriteLock.writeLock().newCondition();
	static boolean dicoReady = false;
	


	static private void parseDico(String filename) throws IOException {
		readWriteLock.writeLock().lock();
		String[] param = filename.split("_");
		String max_size_str = param[3].replaceAll("[^0-9]", "");
		MIN_SIZE = Integer.parseInt(param[2]);
		MAX_SIZE = Integer.parseInt(max_size_str);
		BufferedReader bf = new BufferedReader(new FileReader(filename));
		String line;
		while((line = bf.readLine()) != null) {
			dicto.add(line);
		}
		bf.close();

		dicoReady = true;
		dicoReadyCond.signalAll();
		readWriteLock.writeLock().unlock();
	}

	static boolean isInDict(String word) throws InterruptedException {
		try {
			readWriteLock.readLock().lock();
			if(!dicoReady) {
				readWriteLock.readLock().unlock();
				readWriteLock.writeLock().lock();
				try {
					while(!dicoReady) {
						dicoReadyCond.await();

					}
					readWriteLock.readLock().lock();
				}finally {
					readWriteLock.writeLock().unlock();
				}
			}
			return dicto.contains(word);
		}finally {
			readWriteLock.readLock().unlock();
		}
	}

	public static void main(String args[]) throws IOException {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();

		}
		parseDico(args[0]);
		while (true) {
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				System.out.println("I/O error: " + e);
			}
			// new thread for a client
			new EchoThread(socket).start();
		}
	}
}