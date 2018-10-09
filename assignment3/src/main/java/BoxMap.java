import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BoxMap implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Address> addresses;

	public BoxMap() {
		this.addresses = new ArrayList<Address>();
	}
	
	public BoxMap(List<Address> addresses) {
		this.addresses = addresses;
	}
	
	public List<Address> getBoxAddresses() {
		return this.addresses;
	}

}
