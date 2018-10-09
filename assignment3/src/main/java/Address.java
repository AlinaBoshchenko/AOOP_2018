import java.io.Serializable;

public class Address implements Serializable{
	private static final long serialVersionUID = 1L;
	private int port;
	private String name;
	
	public Address(String a, int p) {
		this.name = a;
		this.port = p;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object a) {
		if(a instanceof Address){
            return (this.getName().equals(((Address) a).getName())) && (this.getPort() == ((Address) a).getPort());
        }
        return false;
	}
	
	@Override
	public String toString() {
		return name + ":" + port;
	}
}
