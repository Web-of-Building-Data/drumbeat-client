package fi.aalto.cs.drumbeat.common;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import fi.aalto.cs.drumbeat.MyUI;
import fi.aalto.cs.drumbeat.models.DrbServer;

public class DrbApplication {
	
	public static final Lang RDF_LANG_DEFAULT = Lang.TURTLE;
	
	private static DrbApplication instance = new DrbApplication();
	
	public static DrbApplication getInstance() {
		return instance;
	}
	
	
	
	private final List<DrbServer> serverContainers;
	private MyUI ui;
	
	public DrbApplication() {
		serverContainers = new LinkedList<>();
	}
	
	
	public List<DrbServer> getServers() {
		return serverContainers;
	}
	
	public void init(MyUI ui) {
		this.ui = ui;
		addServer(new DrbServer("Architect", "http://architect.drb.cs.hut.fi"));		
		addServer(new DrbServer("Structural Engineer", "http://structural.drb.cs.hut.fi"));
	}
	
	public static Model parseModel(String content) {
		Model model = ModelFactory.createDefaultModel();
		InputStream in = new BufferedInputStream(new ByteArrayInputStream(content.getBytes()));
		RDFDataMgr.read(model, in, RDF_LANG_DEFAULT);
		return model;
	}
	
	
	public void addServer(DrbServer server) {
		if (ui.tryAddServer(server)) {
			serverContainers.add(server);
		}
	}
	

}
