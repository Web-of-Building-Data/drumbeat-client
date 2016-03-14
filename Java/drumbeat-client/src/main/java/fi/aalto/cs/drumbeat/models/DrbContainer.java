package fi.aalto.cs.drumbeat.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ResIterator;

import fi.aalto.cs.drumbeat.common.DrbOntology.LBDHO;
import fi.aalto.cs.drumbeat.common.DrbApplication;

public class DrbContainer implements Comparable<DrbContainer> {
	
	private final DrbContainerType type;
	private final String uri;
	private final String id;
	private final DrbContainer parent;
	
	private String name;	
	private Model data;

	public DrbContainer(DrbContainerType type, String uri, DrbContainer parent) {
		this.type = type;
		this.uri = uri;
		this.parent = parent;
		id =  uri.substring(uri.lastIndexOf('/') + 1);
		
	}
	
	public boolean isServer() {
		return parent == null;
	}
	
	public String getUri() {
		return uri;
	}
	
	public String getId() {
		return id;
	}
	
	public Model getData() {
		if (data == null) {
			String response =
					ClientBuilder
						.newClient()
						.target(uri)
						.request(DrbApplication.RDF_LANG_DEFAULT.getHeaderString())
						.get(String.class);
			
			data = DrbApplication.parseModel(response);
		}
		return data;
	}
	
	public String getName() {
		if (name == null && type.getClassResource() != null) {
			NodeIterator it = getData().listObjectsOfProperty(LBDHO.name);
			if (it.hasNext()) {
				return it.next().asLiteral().getString();
			} else {
				throw new NullPointerException("Property " + LBDHO.name + " is not found");
			}
		}
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public DrbContainer getParent() {
		return parent;
	}
	
	public String getBaseUri() {
		if (parent != null) {
			return parent.getBaseUri();
		} else {
			return uri;
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s [%s]", getName(), getId()) ;
	}	
	
	@Override
	public int compareTo(DrbContainer o) {
		return uri.compareTo(o.uri);
	}
	
	private LinkedList<DrbContainer> getParentList() {
		LinkedList<DrbContainer> parents = new LinkedList<>();
		for (DrbContainer currentParent = parent; currentParent != null; currentParent = currentParent.getParent()) {
			parents.addFirst(currentParent);
		}
		return parents;
	}
	
	public List<DrbContainer> getChildren() {
		
		DrbContainerType childContainerType = type.getChildContainerType();
		if (childContainerType == null) {
			return null;
		}
		
		WebTarget target = 
				ClientBuilder
					.newClient()
					.target(getBaseUri())
					.path(childContainerType.getPath());
		
		List<DrbContainer> parentList = getParentList();
		if (parentList != null) {
			for (DrbContainer parent : getParentList()) {
				if (!parent.isServer()) {
					target = target.path(parent.getId());
				}
			}
		}
		
		if (!this.isServer()) {
			target = target.path(this.getId());
		}
		
		System.out.println("Getting children from: " + target.getUri());
		
		final String response =
				target
					.request(DrbApplication.RDF_LANG_DEFAULT.getHeaderString())
					.get(String.class);
		
		final Model dataSourcesModel = DrbApplication.parseModel(response);
		
		final ResIterator resIterator = dataSourcesModel.listSubjects();
		
		final List<DrbContainer> children = new ArrayList<>();
		
		while (resIterator.hasNext()) {
			final String childUri = resIterator.next().getURI();			
			final DrbContainer child = new DrbContainer(childContainerType, childUri, this);
			children.add(child);
		}			
		
		return children;
	}
	
	
	

}
