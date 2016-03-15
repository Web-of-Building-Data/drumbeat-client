package fi.aalto.cs.drumbeat.views;

import java.rmi.UnexpectedException;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Tree;

import fi.aalto.cs.drumbeat.models.DrbContainer;
import fi.aalto.cs.drumbeat.models.DrbServer;

@SuppressWarnings("serial")
public class DrbContainerTreeView extends Tree {
	
	private final String PROPERTY_RELATED_CONTAINER_OBJECT = "relatedContainer";
			
	private final DrbServer server;
	
	@SuppressWarnings("unchecked")
	public DrbContainerTreeView(DrbServer server, RdfTableView rdfTableView) throws UnexpectedException {
		
		this.server = server;
		
		super.addContainerProperty(PROPERTY_RELATED_CONTAINER_OBJECT, DrbContainer.class, null);
		addContainer(null, server);
		
		addValueChangeListener(e -> {
			String selectedItemId = (String) getValue();
			if (selectedItemId != null) {
				Property<DrbContainer> property = getContainerProperty(selectedItemId, PROPERTY_RELATED_CONTAINER_OBJECT);
				DrbContainer selectedContainer = property.getValue();
				if (selectedContainer != null) {
					if (!selectedContainer.isServer()) {
						Model data = selectedContainer.getData();
						rdfTableView.setData(data);
						rdfTableView.setVisible(true);
					} else {
						rdfTableView.setVisible(false);
					}
				}
			}
		});
		
	}
	
	public DrbServer getServer() {
		return server;
	}	
	
	@SuppressWarnings("unchecked")
	private void addContainer(String parentItemId, DrbContainer container) throws UnexpectedException {
		if (parentItemId != null) {
			setChildrenAllowed(parentItemId, true);
		}

		String itemId = !container.isServer() ? container.getName() : "<root>";		
		Item childItem = addItem(itemId);		
		childItem
			.getItemProperty(PROPERTY_RELATED_CONTAINER_OBJECT)
			.setValue(container);

		setChildrenAllowed(itemId, false);
		if (parentItemId != null) {
			setParent(itemId, parentItemId);
		}		
		
		List<DrbContainer> children = container.getChildren();		
		if (children != null) {
			for (DrbContainer child : children) {
				addContainer(itemId, child);				
			}
		}
	}
	
	
	
}