package fi.aalto.cs.drumbeat.views;

import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;
import fi.aalto.cs.drumbeat.MyUI;
import fi.aalto.cs.drumbeat.models.DrbContainer;
import fi.aalto.cs.drumbeat.models.DrbContainerType;

@SuppressWarnings("serial")
public class ActionsPanel extends Panel {
	
	private HorizontalLayout layout;
//	private final Button disconnectButton;
	private final Button deleteButton;
	private final Button addChildButton;
	private final NewContainerWindow newContainerWindow;
	private DrbContainer currentContainer;
	
	public ActionsPanel() {
		
		setCaption("Actions");
		
		layout = new HorizontalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setSizeFull();
		setContent(layout);
		
		float buttonWidth = 180.f;
		
		deleteButton = new Button("Delete...");
		deleteButton.setWidth(buttonWidth, Unit.PIXELS);
		deleteButton.addClickListener(e -> {
			String action = deleteButton.getCaption().replace("...", "").toLowerCase();
			MessageBox.createQuestion()
				.withHtmlMessage("Do you really want to <b>" + action + "</b>?")
				.withYesButton(() -> {
					MyUI ui = (MyUI)getUI();
					ui.deleteContainer(currentContainer);
				})
				.withNoButton(ButtonOption.focus())
				.open();
			});
		layout.addComponent(deleteButton);
		
		newContainerWindow = new NewContainerWindow();
		
		addChildButton = new Button("Add child...");
		addChildButton.setWidth(buttonWidth, Unit.PIXELS);
		addChildButton.addClickListener(e -> {
			if (newContainerWindow.getParent() == null) {
				newContainerWindow.setParentContainer(currentContainer);
				getUI().addWindow(newContainerWindow);
			}
			
//			TextField idInput = new TextField("ID");
//			TextField nameInput = new TextField("Name");
//			MessageBox.createQuestion()
//				.withCaption(addChildButton.getCaption().replace("...", ""))
//				.withMessage(idInput)
//				.withMessage(nameInput)
//				.withOkButton(() -> {
//						MyUI ui = (MyUI)getUI();
//						ui.addChildContainer(idInput.getValue(), nameInput.getValue());
//					}, ButtonOption.focus())
//				.withCancelButton()
//				.open();
			});
		layout.addComponent(addChildButton);
		
	}
	
	public void setActions(DrbContainer container) {
		
		currentContainer = container;
		
		DrbContainerType type = container.getType();
		DrbContainerType childType = type.getChildContainerType();
		
		if (container.isServer()) {
			deleteButton.setCaption("Disconnect...");
			deleteButton.setEnabled(true);
		} else {
			List<DrbContainer> children = container.getChildren();
			deleteButton.setCaption("Delete...");			
			deleteButton.setEnabled(children == null || children.isEmpty());
		}
		
		addChildButton.setEnabled(childType != null);

		if (childType != null) {
			addChildButton.setCaption("Add " + childType.getName() + "...");
		}
	}
	
}
