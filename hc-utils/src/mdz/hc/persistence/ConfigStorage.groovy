package mdz.hc.persistence

public interface ConfigStorage {

	String getConfig(String name)
	
	void setConfig(String name, String value)
}
