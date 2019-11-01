import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class Block {
    private Mot mot;
    private Block pred;
    private int score;
    
	public Block(Mot mot, Block pred) {
		super();
		this.mot = mot;
		this.pred = pred;
		if(pred == null) {
			this.score = mot.getScore();
		}
		else {
			this.score = mot.getScore() +pred.getScore();
		}
	}

	public int getScore() {
		if(pred == null) {
			return 0;
		}
		return score;
	}
	
	public Mot getMot() {
		return mot;
	}
	
	public static Block getPred(Mot m, Iterable<Block> blockchain) throws NoSuchAlgorithmException, IOException {
		if(m.isEmptyWord()) return null;
		for(Block b: blockchain) {
			if(Arrays.equals(b.getMot().hash(), m.getHead())) {
				return b;
			}
		}
		throw new RuntimeException("Block dont have a predecessor and is not first block");
	}
	
	public String toString() {
		return mot.toString();
	}

}
