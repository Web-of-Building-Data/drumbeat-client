package fi.aalto.cs.drumbeat.views;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import com.vaadin.data.Item;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class RdfTableView extends Table {
	
	@SuppressWarnings("unchecked")
	public void setData(Model model) {

		this.removeAllItems();
		
		addContainerProperty("subject", String.class, null);
		addContainerProperty("predicate", String.class, null);
		addContainerProperty("object", String.class, null);
		
		StmtIterator stmtIterator = model.listStatements();
		while (stmtIterator.hasNext()) {
			Statement statement = stmtIterator.next();
			Resource subject = statement.getSubject();
			Resource predicate = statement.getPredicate();
			RDFNode object = statement.getObject();
			
			Object rowId = addItem();
			Item row = getItem(rowId);
			
			row.getItemProperty("subject").setValue(subject.getURI());
			row.getItemProperty("predicate").setValue(predicate.getURI());
			row.getItemProperty("object").setValue(object.toString());
			
		}
		
		
	}

}
