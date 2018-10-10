import java.io.Serializable;

public class Address implements Serializable{
	private static final long serialVersionUID = 1L;
	private int port;
	private String address;
	
	Address(String address, int port) {
		this.address = address;
		this.port = port;
	}
	
	int getPort() {
		return port;
	}
	
	String getAddress() {
		return address;
	}
	
	@Override
	public boolean equals(Object a) {
		if(a instanceof Address){
            return (this.getAddress().equals(((Address) a).getAddress())) && (this.getPort() == ((Address) a).getPort());
        }
        return false;
	}
	
	@Override
	public String toString() {
		return address + ":" + port;
	}
}
