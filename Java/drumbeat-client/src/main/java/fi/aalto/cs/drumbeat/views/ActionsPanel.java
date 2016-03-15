package fi.aalto.cs.drumbeat.views;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

import fi.aalto.cs.drumbeat.MyUI;
import fi.aalto.cs.drumbeat.models.DrbContainer;
import fi.aalto.cs.drumbeat.models.DrbContainerType;

@SuppressWarnings("serial")
public class ActionsPanel extends Panel {
	
	private HorizontalLayout layout;
	private final Button disconnectButton;
	private final Button deleteButton;
	private final Button addChildButton;
	
	public ActionsPanel() {
		
		setCaption("Actions");
		
		layout = new HorizontalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setSizeFull();
		setContent(layout);
		
		disconnectButton = new Button("Disconnect...");
		disconnectButton.addClickListener(e -> {
			ConfirmDialog.show(getUI(), "Are you sure?", dialog -> {
				if (dialog.isConfirmed()) {
					MyUI ui = (MyUI)getUI();
					ui.deleteSelectedContainer();
				}
			});			
		});

		deleteButton = new Button("Delete...");
		deleteButton.addClickListener(e -> {
			ConfirmDialog.show(getUI(), "Are you sure?", dialog -> {
				if (dialog.isConfirmed()) {
					MyUI ui = (MyUI)getUI();
					ui.deleteSelectedContainer();
				}
			});
		});
		
		
		addChildButton = new Button("Add child...");
		
	}
	
	public void setActions(DrbContainer container) {
		
		layout.removeAllComponents();
		
		DrbContainerType type = container.getType();
		DrbContainerType childType = type.getChildContainerType();
		
		if (container.isServer()) {
			layout.addComponent(disconnectButton);
		} else if (container.getChildren().isEmpty()) {
			layout.addComponent(deleteButton);			
		}		
		
		if (childType != null) {
			layout.addComponent(addChildButton);
		}
	}
	
}
