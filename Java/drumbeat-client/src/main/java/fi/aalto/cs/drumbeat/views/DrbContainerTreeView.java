package fi.aalto.cs.drumbeat.views;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Tree;

import fi.aalto.cs.drumbeat.models.DrbContainer;

@SuppressWarnings("serial")
public class DrbContainerTreeView extends Tree {
	
	private final String PROPERTY_RELATED_CONTAINER_OBJECT = "relatedContainer";
			
	private final DrbContainer rootContainer;
	
	@SuppressWarnings("unchecked")
	public DrbContainerTreeView(DrbContainer rootContainer, RdfTableView rdfTableView) {
		
		this.rootContainer = rootContainer;
		
		super.addContainerProperty(PROPERTY_RELATED_CONTAINER_OBJECT, DrbContainer.class, null);
		
		List<DrbContainer> children = rootContainer.getChildren();
		addChildren(null, children);
		
		addValueChangeListener(e -> {
			String selectedItemId = (String) getValue();
			if (selectedItemId != null) {
				Property<DrbContainer> property = getContainerProperty(selectedItemId, PROPERTY_RELATED_CONTAINER_OBJECT);
				DrbContainer selectedContainer = property.getValue();
				Model data = selectedContainer.getData();
				rdfTableView.setData(data);
			}
//			new Notification(container.getName(), Type.ERROR_MESSAGE).show(Page.getCurrent());
		});
		
	}
	
	public DrbContainer getRootContainer() {
		return rootContainer;
	}
	
	@SuppressWarnings("unchecked")
	private void addChildren(String parentItemId, List<DrbContainer> children) {
		if (children == null || children.isEmpty()) {
			if (parentItemId != null) {
				setChildrenAllowed(parentItemId, false);
			}
			return;
		}
		
		if (parentItemId != null) {
			setChildrenAllowed(parentItemId, true);
		}

		for (DrbContainer child : children) {
			String childItemId = child.getName();
			Item childItem = addItem(childItemId);
			
			if (parentItemId != null) {
				setParent(childItemId, parentItemId);
			}
			
			childItem
				.getItemProperty(PROPERTY_RELATED_CONTAINER_OBJECT)
				.setValue(child);
			
			List<DrbContainer> childrenOfChild = child.getChildren();
			addChildren(childItemId, childrenOfChild);
		}
		
	}
	
	
}