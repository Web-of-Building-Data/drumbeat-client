package fi.aalto.cs.drumbeat.models;

public class DrbServerContainer extends DrbContainer {

	public DrbServerContainer(String name, String uri) {
		super(DrbContainerType.SERVER, uri, null);
		setName(name);
	}
	
	

}
