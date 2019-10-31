import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MotVide extends Mot {

	private MotVide(List<Lettre> letters, byte[] head, byte[] politician, byte[] sig) {
		super(letters, head, politician, sig);
		// TODO Auto-generated constructor stub
	}
	
	public MotVide() {
		super(null, null, null,null);
	}
	
	@Override
	public byte[] hash() throws NoSuchAlgorithmException {
		return Sha.hash_sha256("");
	}
	
	@Override
	public int getScore() {
		return 0;
	}

}
