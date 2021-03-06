package fi.aalto.cs.drumbeat.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResIterator;

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
		id = parent != null ? uri.substring(uri.lastIndexOf('/') + 1) : "";
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
	
	public DrbContainerType getType() {
		return type;
	}
	
	public DrbServer getServer() {
		return parent != null ? parent.getServer() : (DrbServer)this;
	}
	
	public synchronized void loadData() {
		System.out.println("Loading data: " + uri);
		
		String response =
				ClientBuilder
					.newClient()
					.target(uri)
					.request(DrbApplication.RDF_LANG_DEFAULT.getHeaderString())
					.get(String.class);
		
		data = DrbApplication.parseModel(response);		
	}
	
	public synchronized Model getData() {
		if (data == null) {
			loadData();
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
		return String.format("%s '%s'", getType().getName(), getName()) ;
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
		
		target = target.path(this.getId());
		
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
	
	
	public Status delete() {
		if (!isServer()) {
			System.out.println("Deleting container: " + uri);
			
			Response response = ClientBuilder
				.newClient()
				.target(uri)
				.request(DrbApplication.RDF_LANG_DEFAULT.getHeaderString())
				.delete();
			
			return Status.fromStatusCode(response.getStatus());
		}
		return Status.NO_CONTENT;
	}

	public DrbContainer addChild(String id, String name) {
		
		DrbContainerType childContainerType = type.getChildContainerType();
		if (childContainerType == null) {
			throw new NullPointerException();
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
		
		target =
			target
				.path(this.getId())
				.path(id);
		
		
		Form form = new Form();
		form.param("name", name);		
		
		System.out.println("Creating object: " + target.getUri());
		
		final Response response =
				target
					.request(DrbApplication.RDF_LANG_DEFAULT.getHeaderString())
					.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
		
		if (response.getStatusInfo().getFamily() == Status.Family.SUCCESSFUL) {
			return new DrbContainer(childContainerType, target.getUri().toString(), this);			
		} else {
			throw new RuntimeException(String.format("Error %d (%s): %s", response.getStatus(), response.getStatusInfo(), response.getEntity()));			
		}		
		
		
	}
	

}
