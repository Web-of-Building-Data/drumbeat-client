package fi.aalto.cs.drumbeat.views;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	
//	private final DrbServer server;
	
	private final Tree tree;
	
	private final Map<DrbContainer, Object> itemIds;
	
	public DrbContainerTreeView(DrbServer server) {		
//		this.server = server;
		
		itemIds = new TreeMap<>();		
		
    	Label lblTitle = new Label(
    			String.format("<h3><b><a href=\"%s\">%s</a></b></h3>",
    					server.getBaseUri(),
    					server.getName()),
    			ContentMode.HTML);    	
    	addComponent(lblTitle); 
    	
    	tree = new Tree();
		tree.addContainerProperty(PROPERTY_RELATED_CONTAINER_OBJECT, DrbContainer.class, null);
		tree.addValueChangeListener(e -> notifyValueChanged());
		addComponent(tree);		
		
		addContainer(null, server);
	}
	
	
	public void notifyAddedItem(DrbContainer container) {
		DrbContainer parent = container.getParent();
		Object parentItemId = itemIds.remove(parent);
		Object itemId = addContainer(parentItemId, container);
		tree.setValue(itemId);
		notifyValueChanged();
	}
	

	public void notifyRemovedItem(DrbContainer container) {
		tree.setValue(null);

		Object itemId = itemIds.remove(container);
		
		if (itemId != null) {
			Object parentItemId = tree.getParent(itemId);
			tree.removeItem(itemId);
			if (parentItemId != null) {
				tree.setChildrenAllowed(parentItemId, !tree.getChildren(parentItemId).isEmpty());
				tree.setValue(parentItemId);
			}
		}
		
		notifyValueChanged();
	}
	
	public void notifyValueChanged() {
		Object selectedItemId = tree.getValue();
		if (selectedItemId != null) {
			@SuppressWarnings("unchecked")
			Property<DrbContainer> property = tree.getContainerProperty(selectedItemId, PROPERTY_RELATED_CONTAINER_OBJECT);
			DrbContainer selectedContainer = property.getValue();
			DrbApplication.getInstance().getUI().setSelectedContainer(selectedContainer);
		}		
	}
	
	
	@SuppressWarnings("unchecked")
	private Object addContainer(Object parentItemId, DrbContainer container) {
		if (parentItemId != null) {
			tree.setChildrenAllowed(parentItemId, true);
		}

		Object itemId = tree.addItem();
		itemIds.put(container, itemId);
		tree.setItemCaption(itemId, !container.isServer() ? container.getName() : "<root>");
		
		Item childItem = tree.getItem(itemId);		
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
		return itemId;
	}
	
	
	
	
}