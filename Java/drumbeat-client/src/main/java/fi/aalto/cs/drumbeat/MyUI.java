package fi.aalto.cs.drumbeat;

import java.util.Map;
import java.util.TreeMap;

import javax.activity.InvalidActivityException;
import javax.servlet.annotation.WebServlet;
import javax.ws.rs.core.Response.Status;

import com.hp.hpl.jena.rdf.model.Model;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fi.aalto.cs.drumbeat.common.DrbApplication;
import fi.aalto.cs.drumbeat.models.DrbContainer;
import fi.aalto.cs.drumbeat.models.DrbServer;
import fi.aalto.cs.drumbeat.views.DrbContainerTreeView;
import fi.aalto.cs.drumbeat.views.RdfTableView;
import fi.aalto.cs.drumbeat.views.ActionsPanel;
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
	private Label rightContainerTitle;
	private ActionsPanel rightActionsPanel;
	private RdfTableView rightRdfTableView;
	
	private AddServerWindow dialogAddServerWindow;
	
	private DrbContainer selectedContainer;
	
	private Map<DrbServer, DrbContainerTreeView> serverTreeViews;
	

    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	
    	serverTreeViews = new TreeMap<>();
    	
        final VerticalLayout mainLayout = new VerticalLayout();
        dialogAddServerWindow = new AddServerWindow();        
        
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
        			if (dialogAddServerWindow.getParent() == null) {
        				addWindow(dialogAddServerWindow);
        			}
        		})
        );        
        
        //
        // right panel
        //
        rightPanel = new VerticalLayout();
        rightPanel.setMargin(true);
        rightPanel.setSpacing(true);
        rightPanel.setSizeFull();
        
        rightContainerTitle = new Label();
        rightPanel.addComponent(rightContainerTitle);
        
        rightActionsPanel = new ActionsPanel();
        rightPanel.addComponent(rightActionsPanel);
        
        rightRdfTableView = new RdfTableView();
        rightPanel.addComponent(rightRdfTableView);
        
        hsplit.setFirstComponent(leftPanel);
        hsplit.setSecondComponent(rightPanel);
        hsplit.setSplitPosition(25.0f, Unit.PERCENTAGE);
        
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        
        setContent(mainLayout);
        
        DrbApplication.getInstance().init(this);
        setSelectedContainer(null);
    }
    

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
    
    
    public DrbContainer getSelectedContainer() {
    	return selectedContainer;
    }
    
    
    public void setSelectedContainer(DrbContainer container) {
    	
    	selectedContainer = container;
    	
    	if (container != null) {
    		rightPanel.setVisible(true);
    		
    		rightContainerTitle.setValue(String.format("<h1>%s</h1>", container.toString()));
            rightContainerTitle.setContentMode(ContentMode.HTML);
            
			if (!container.isServer()) {
				Model data = container.getData();
				rightRdfTableView.setData(data);
				rightRdfTableView.setVisible(true);
			} else {
				rightRdfTableView.setVisible(false);
			}
			
			rightActionsPanel.setActions(container);
			
    	} else {
    		rightPanel.setVisible(false);
    	}
    }
    
    
    public synchronized void deleteSelectedContainer() {
		try {
			Status status = selectedContainer.delete();
			
			if (!status.getFamily().equals(Status.Family.SUCCESSFUL)) {
				throw new InvalidActivityException(String.format("HTTP response status: %d (%s)", status.getStatusCode(), status));
			}
		} catch (Exception ex) {
			Notification.show(ex.getMessage(), Type.ERROR_MESSAGE);
			return;
		}
		
		Notification.show(selectedContainer + " deleted");

		DrbContainerTreeView treeView = getContainerTreeView(selectedContainer);		
		assert(treeView != null);

		if (selectedContainer.isServer()) {
			leftTreePanel.removeComponent(treeView);
		} else {
	    	try {
	    		treeView.refresh();
	    	} catch (Exception e) {
	    		Notification.show("Unexpected exception: " + e.getMessage(), Type.ERROR_MESSAGE);
	    	}
		}

		setSelectedContainer(null);
    }
    
    
    public DrbContainerTreeView getContainerTreeView(DrbContainer container) {
    	DrbContainerTreeView treeView = serverTreeViews.get(selectedContainer.getServer());
		if (treeView == null) {
			throw new NullPointerException("Tree for " + container + " not found"); 
		}
		return treeView;    	
    }
    
    
    public void refreshServerView(DrbServer server) {
    	DrbContainerTreeView treeView = serverTreeViews.get(server);
    	
    	try {
    		treeView.refresh();
    	} catch (Exception e) {
    		Notification.show("Unexpected exception: " + e.getMessage(), Type.ERROR_MESSAGE);
    	}
    }
    
    

	public synchronized boolean tryAddServer(DrbServer server) {
		
		DrbContainerTreeView tree;
		
    	try {
    		tree = new DrbContainerTreeView(server);    	
    	} catch (Exception e) {
    		Notification.show("Unexpected exception: " + e.getMessage(), Type.ERROR_MESSAGE);
    		return false;
    	}
    	
		leftTreePanel.addComponent(tree);
		serverTreeViews.put(server, tree);
		
		return true;
		
	}

}
