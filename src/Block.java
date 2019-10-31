import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Block {
    private Mot mot;
    private Block pred;
	public Block(Mot mot, Block pred) {
		super();
		this.mot = mot;
		this.pred = pred;
	}

	public int getScore() {
		if(pred == null) {
			return mot.getScore();
		}
		return mot.getScore() + pred.getScore();
	}
	
	public Mot getMot() {
		return mot;
	}
	
	public static Block getPred(Mot m, List<Block> blockchain) throws NoSuchAlgorithmException, IOException {
		if(Arrays.equals(m.getHead(), Sha.hash_sha256(""))) return null;
		for(Block b: blockchain) {
			if(Arrays.equals(b.getMot().hash(), m.getHead())) {
				return b;
			}
		}
		throw new RuntimeException("Block dont have a predecessor and is not first block");
	}
}
