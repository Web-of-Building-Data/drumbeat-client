package fi.aalto.cs.drumbeat.views;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import fi.aalto.cs.drumbeat.MyUI;
import fi.aalto.cs.drumbeat.common.DrbApplication;
import fi.aalto.cs.drumbeat.models.DrbServerContainer;

@SuppressWarnings("serial")
public class ServerConnectionPopupView extends VerticalLayout {
	
	private TextField txtServerName = new TextField("Name");
	private TextField txtServerUrl = new TextField("URL");
	
	private Button btnConnect = new Button("Connect");
	
	public ServerConnectionPopupView(MyUI myUI) {
		this.addComponent(txtServerName);
		this.addComponent(txtServerUrl);
		this.addComponent(btnConnect);
		
		btnConnect.addClickListener(e -> {
			DrbServerContainer serverContainer = new DrbServerContainer(txtServerName.getValue(), txtServerUrl.getValue());
			DrbApplication.getServerContainers().add(serverContainer);
			myUI.connectServer(serverContainer);
		});
	}
	

}
