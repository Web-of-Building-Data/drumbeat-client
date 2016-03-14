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

import fi.aalto.cs.drumbeat.models.DrbServerContainer;

public class DrbApplication {
	
	public static List<DrbServerContainer> serverContainers;
	
	public static final Lang RDF_LANG_DEFAULT = Lang.TURTLE;
	
	public static List<DrbServerContainer> getServerContainers() {
		if (serverContainers == null) {
			
			serverContainers = new LinkedList<>();
			
			serverContainers.add(
					new DrbServerContainer("Architect", "http://architect.drb.cs.hut.fi")
			);
			
			serverContainers.add(
					new DrbServerContainer("Structural Engineer", "http://structural.drb.cs.hut.fi")
			);
		}
		return serverContainers;
	}
	
	public static Model parseModel(String content) {
		Model model = ModelFactory.createDefaultModel();
		InputStream in = new BufferedInputStream(new ByteArrayInputStream(content.getBytes()));
		RDFDataMgr.read(model, in, RDF_LANG_DEFAULT);
		return model;
	}
	

}
