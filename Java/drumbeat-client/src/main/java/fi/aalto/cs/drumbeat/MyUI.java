package fi.aalto.cs.drumbeat;

import javax.servlet.annotation.WebServlet;

import com.hp.hpl.jena.sparql.function.library.e;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fi.aalto.cs.drumbeat.common.DrbApplication;
import fi.aalto.cs.drumbeat.models.DrbContainer;
import fi.aalto.cs.drumbeat.models.DrbContainerType;
import fi.aalto.cs.drumbeat.models.DrbServerContainer;
import fi.aalto.cs.drumbeat.views.DrbContainerTreeView;
import fi.aalto.cs.drumbeat.views.RdfTableView;
import fi.aalto.cs.drumbeat.views.ServerConnectionPopupView;

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

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout mainLayout = new VerticalLayout();
        
        final PopupView popupView = new PopupView(null, new ServerConnectionPopupView(this));
        mainLayout.addComponent(popupView);

        final HorizontalSplitPanel hsplit  = new HorizontalSplitPanel();
        mainLayout.addComponent(hsplit);
        
        //
        // left panel
        //        
        leftPanel = new VerticalLayout();
        
        leftTreePanel = new VerticalLayout();
        leftPanel.addComponent(leftTreePanel);
        
        leftPanel.addComponents(
        		new Button("Connect...", e -> {
        			popupView.setPopupVisible(true);
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
        
        //
        // connect to servers
        //
        for (DrbServerContainer serverContainer : DrbApplication.getServerContainers()) {
        	connectServer(serverContainer);
        }
        
        setContent(mainLayout);
    }
    
    public void connectServer(DrbServerContainer serverContainer) {
    	
    	Label lblTitle = new Label(
    			String.format("<h3><b><a href=\"%s\">%s</a></b></h3>",
    					serverContainer.getBaseUri(),
    					serverContainer.getName()),
    			ContentMode.HTML);
    	leftTreePanel.addComponent(lblTitle);
    	
    	DrbContainerTreeView tree = new DrbContainerTreeView(serverContainer, rightRdfTableView);
    	leftTreePanel.addComponent(tree);
    	
    	
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
