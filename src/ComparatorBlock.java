import java.util.Comparator;

class ComparatorBlock implements Comparator<Block> {
    public int compare(Block a, Block b) {
    	if(a.getScore() > b.getScore()) return 1;
    	else return -1;
    }
}