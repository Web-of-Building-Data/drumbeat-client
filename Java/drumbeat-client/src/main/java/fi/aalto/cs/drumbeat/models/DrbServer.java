package fi.aalto.cs.drumbeat.models;

public class DrbServer extends DrbContainer {

	public DrbServer(String name, String uri) {
		super(DrbContainerType.SERVER, uri, null);
		setName(name);
	}
	
	

}
