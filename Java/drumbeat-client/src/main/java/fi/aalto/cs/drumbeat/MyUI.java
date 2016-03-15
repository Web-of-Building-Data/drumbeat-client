package fi.aalto.cs.drumbeat;

import java.rmi.UnexpectedException;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fi.aalto.cs.drumbeat.common.DrbApplication;
import fi.aalto.cs.drumbeat.models.DrbServer;
import fi.aalto.cs.drumbeat.views.DrbContainerTreeView;
import fi.aalto.cs.drumbeat.views.RdfTableView;
import fi.aalto.cs.drumbeat.views.AddServerWindow;

/**
 *
 */
@SuppressWarnings("serial")
@Theme("mytheme")
@Widgetset("fi.aalto.cs.drumbeat.MyAppWidgetset")
public class MyUI extends UI {
	
	private VerticalLayout leftPanel;
	private VerticalLayout leftTreePanel;
	private VerticalLayout rightPanel;
	private RdfTableView rightRdfTableView;
	
	private AddServerWindow addServerWindow;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout mainLayout = new VerticalLayout();
        addServerWindow = new AddServerWindow();        
        
        final HorizontalSplitPanel hsplit  = new HorizontalSplitPanel();
        mainLayout.addComponent(hsplit);
        
        //
        // left panel
        //        
        leftPanel = new VerticalLayout();
        leftPanel.setSpacing(true);
        
        leftTreePanel = new VerticalLayout();
        leftPanel.addComponent(leftTreePanel);
        
        leftPanel.addComponents(
        		new Button("Connect...", e -> {
        			if (addServerWindow.getParent() == null) {
        				addWindow(addServerWindow);
        			}
        		})
        );        
        
        //
        // right panel
        //
        rightPanel = new VerticalLayout();
        
        rightRdfTableView = new RdfTableView();
        rightPanel.addComponent(rightRdfTableView);
        
        hsplit.setFirstComponent(leftPanel);
        hsplit.setSecondComponent(rightPanel);
        hsplit.setSplitPosition(25.0f, Unit.PERCENTAGE);
        
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        
        setContent(mainLayout);
        
        DrbApplication.getInstance().init(this);        
    }
    

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
    
    

	public boolean tryAddServer(DrbServer server) {
		
		DrbContainerTreeView tree;
		
    	try {
    		tree = new DrbContainerTreeView(server, rightRdfTableView);    	
    	} catch (Exception e) {
    		Notification.show("Unexpected exception: " + e.getMessage(), Type.ERROR_MESSAGE);
    		return false;
    	}
    	
    	Label lblTitle = new Label(
    			String.format("<h3><b><a href=\"%s\">%s</a></b></h3>",
    					server.getBaseUri(),
    					server.getName()),
    			ContentMode.HTML);
    	
    	leftTreePanel.addComponent(lblTitle);    		
		leftTreePanel.addComponent(tree);
		
		return true;
		
	}
}
