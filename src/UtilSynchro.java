import java.lang.reflect.Method;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class UtilSynchro {
	
    public static void waitForCond(ReentrantLock rl, Condition c, Supplier<Boolean> s, Consumer<Boolean> m) throws InterruptedException {
    	rl.lock();
    	try {
    		while(!s.get()) {
	    		c.await();
    		}
    		m.accept(false);
    	}
    	finally {
    		rl.unlock();
    	}
    }
    
    public static void notifyCond(ReentrantLock rl, Condition c, Consumer<Boolean> m)  {
    	rl.lock();
    	try {
    		m.accept(true);
    		c.signalAll();
    	}
    	finally {
    		rl.unlock();
    	}
    }  
}
