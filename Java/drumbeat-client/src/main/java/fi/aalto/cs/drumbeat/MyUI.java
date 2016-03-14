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
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fi.aalto.cs.drumbeat.controllers.DrbApplication;
import fi.aalto.cs.drumbeat.models.DrbContainer;
import fi.aalto.cs.drumbeat.models.DrbContainerType;
import fi.aalto.cs.drumbeat.views.DrbContainerTreeView;
import fi.aalto.cs.drumbeat.views.RdfTableView;

/**
 *
 */
@SuppressWarnings("serial")
@Theme("mytheme")
@Widgetset("fi.aalto.cs.drumbeat.MyAppWidgetset")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout mainLayout = new VerticalLayout();
        
        final HorizontalSplitPanel hsplit  = new HorizontalSplitPanel();
        mainLayout.addComponent(hsplit );
        
        final VerticalLayout menu = new VerticalLayout();
        
//        final Button btnConnect = new Button("Connect", e -> {
//        	e.
//        });
        
        
        final RdfTableView rdfTableView = new RdfTableView();
        
        for (String serverUrl : DrbApplication.SERVER_URLS) {
        	
        	Label label1 = new Label(String.format("<h3><b>%s</b></h3>", serverUrl), ContentMode.HTML);
        	menu.addComponent(label1);
        	
        	DrbContainer serverContainer = new DrbContainer(DrbContainerType.SERVER, serverUrl);
        	DrbContainerTreeView tree = new DrbContainerTreeView(serverContainer, rdfTableView);
            menu.addComponent(tree);
        }
        
        hsplit .setFirstComponent(menu);
        hsplit .setSecondComponent(rdfTableView);
        hsplit.setSplitPosition(25.0f, Unit.PERCENTAGE);
        
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        
        setContent(mainLayout);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
