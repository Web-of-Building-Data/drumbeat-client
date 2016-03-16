package fi.aalto.cs.drumbeat.views;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fi.aalto.cs.drumbeat.common.DrbApplication;
import fi.aalto.cs.drumbeat.models.DrbServer;

@SuppressWarnings("serial")
public class NewServerWindow extends Window {
	
	public NewServerWindow() {
		super("Connect to new server");
		setModal(true);
		setResizable(false);
		setWidth(300.0f, Unit.PIXELS);
		
		VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		
		layout.setMargin(true);
		layout.setSpacing(true);
		
		TextField txtServerName = new TextField("Name");
		txtServerName.setSizeFull();
		layout.addComponent(txtServerName);
		
		TextField txtServerUrl = new TextField("URL");
		txtServerUrl.setSizeFull();
		layout.addComponent(txtServerUrl);
		
		Button btnConnect = new Button("Connect");		
		btnConnect.addClickListener(e -> {
			DrbServer serverContainer = new DrbServer(txtServerName.getValue(), txtServerUrl.getValue());
			DrbApplication.getInstance().addServer(serverContainer);
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
	
}
