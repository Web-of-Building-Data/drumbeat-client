package fi.aalto.cs.drumbeat.views;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fi.aalto.cs.drumbeat.MyUI;
import fi.aalto.cs.drumbeat.common.DrbApplication;
import fi.aalto.cs.drumbeat.models.DrbContainer;
import fi.aalto.cs.drumbeat.models.DrbServer;

@SuppressWarnings("serial")
public class NewContainerWindow extends Window {
	
	private DrbContainer parent;
	
	public NewContainerWindow() {
		super("Connect to new server");
		setModal(true);
		setResizable(false);
		setWidth(300.0f, Unit.PIXELS);
		
		VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		
		layout.setMargin(true);
		layout.setSpacing(true);
		
		TextField txtID = new TextField("ID");
		txtID.setSizeFull();
		layout.addComponent(txtID);
		
		TextField txtName = new TextField("Name");
		txtName.setSizeFull();
		layout.addComponent(txtName);
		
		Button btnConnect = new Button("Create");		
		btnConnect.addClickListener(e -> {
			MyUI ui = (MyUI)getUI();
			ui.addChildContainer(parent, txtID.getValue(), txtName.getValue());
			close();
		});
		
		
		Button btnClose = new Button("Cancel");
		btnClose.addClickListener(e -> {
			close();
		});
		
		HorizontalLayout buttonPanel = new HorizontalLayout(btnConnect, btnClose);
		buttonPanel.setSpacing(true);
		
		layout.addComponent(buttonPanel);
		layout.setComponentAlignment(buttonPanel, Alignment.BOTTOM_RIGHT);
	}
	
	public void setParentContainer(DrbContainer parent) {
		this.parent = parent;
	}
	
}
