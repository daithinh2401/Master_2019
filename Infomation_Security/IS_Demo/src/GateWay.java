import java.util.ArrayList;
import java.util.List;

public class GateWay {

	private String GWID;
	private String PGW;
	private List<String> message;
	
	public GateWay(String gwid, String pgw) {
		this.GWID 		= gwid;
		this.PGW 		= pgw;
		this.message 	= new ArrayList<>();
	}
	
	public String getGWID() {
		return GWID;
	}
	
	public void setGWID(String gwid) {
		GWID = gwid;
	}
	
	public String getPGW() {
		return PGW;
	}
	
	public void setPGW(String pgw) {
		PGW = pgw;
	}
	
	public void addToMessage(String str) {
		message.add(str);
	}
}
