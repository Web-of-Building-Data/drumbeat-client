package fi.aalto.cs.drumbeat.models;

import org.apache.jena.rdf.model.Resource;

import fi.aalto.cs.drumbeat.common.DrbOntology.LBDHO;

public class DrbContainerType {
	
	public static final DrbContainerType SERVER = new DrbContainerType("Server", "", null); 
	public static final DrbContainerType COLLECTION = new DrbContainerType("Collection", "collections", LBDHO.Collection); 
	public static final DrbContainerType DATA_SOURCE = new DrbContainerType("Data Source", "datasources", LBDHO.DataSource); 
	public static final DrbContainerType DATA_SET = new DrbContainerType("Data Set", "datasets", LBDHO.DataSet); 

	public static final DrbContainerType[] ALL_TYPES = new DrbContainerType[] {
			SERVER,
			COLLECTION,
			DATA_SOURCE,
			DATA_SET
	};
	
	static {
		for (int i = 0; i < ALL_TYPES.length; ++i) {
			ALL_TYPES[i].level = i;
			ALL_TYPES[i].parentContainerType = i > 0 ? ALL_TYPES[i-1] : null;			
			ALL_TYPES[i].childContainerType = i < ALL_TYPES.length - 1 ? ALL_TYPES[i+1] : null;			
		}
	}
	
	private String name;
	private String path;
	private Resource classResource;
	private int level;
	private DrbContainerType parentContainerType;
	private DrbContainerType childContainerType;
	
	public DrbContainerType(String name, String path, Resource classResource) {
		this.name = name;
		this.path = path;
		this.classResource = classResource;
	}
	
	public int getLevel() {
		return level;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;		
	}
	
	public Resource getClassResource() {
		return classResource;
	}
	
	public DrbContainerType getParentContainerType() {
		return parentContainerType;
	}
	
	public DrbContainerType getChildContainerType() {
		return childContainerType;
	}
	
}
