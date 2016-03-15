package fi.aalto.cs.drumbeat.views;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

import fi.aalto.cs.drumbeat.common.DrbApplication;
import fi.aalto.cs.drumbeat.models.DrbContainer;
import fi.aalto.cs.drumbeat.models.DrbServer;

@SuppressWarnings("serial")
public class DrbContainerTreeView extends VerticalLayout {
	
	private final String PROPERTY_RELATED_CONTAINER_OBJECT = "relatedContainer";
			
	private final DrbServer server;
	
	private Tree tree;
	
	public DrbContainerTreeView(DrbServer server) {		
		this.server = server;
		
    	Label lblTitle = new Label(
    			String.format("<h3><b><a href=\"%s\">%s</a></b></h3>",
    					server.getBaseUri(),
    					server.getName()),
    			ContentMode.HTML);    	
    	addComponent(lblTitle); 
    	
    	tree = new Tree();
		tree.addContainerProperty(PROPERTY_RELATED_CONTAINER_OBJECT, DrbContainer.class, null);
		addComponent(tree);
		
		refresh();
	}
	
	
	@SuppressWarnings("unchecked")
	public void refresh() {
		tree.removeAllItems();
		
		addContainer(null, server);
		
		tree.addValueChangeListener(e -> {
			String selectedItemId = (String) tree.getValue();
			if (selectedItemId != null) {
				Property<DrbContainer> property = tree.getContainerProperty(selectedItemId, PROPERTY_RELATED_CONTAINER_OBJECT);
				DrbContainer selectedContainer = property.getValue();
				DrbApplication.getInstance().getUI().setSelectedContainer(selectedContainer);
			}
		});
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	private void addContainer(String parentItemId, DrbContainer container) {
		if (parentItemId != null) {
			tree.setChildrenAllowed(parentItemId, true);
		}

		String itemId = !container.isServer() ? container.getName() : "<root>";		
		Item childItem = tree.addItem(itemId);		
		childItem
			.getItemProperty(PROPERTY_RELATED_CONTAINER_OBJECT)
			.setValue(container);

		tree.setChildrenAllowed(itemId, false);
		if (parentItemId != null) {
			tree.setParent(itemId, parentItemId);
		}		
		
		List<DrbContainer> children = container.getChildren();		
		if (children != null) {
			for (DrbContainer child : children) {
				addContainer(itemId, child);				
			}
		}
	}
	
	
	
}