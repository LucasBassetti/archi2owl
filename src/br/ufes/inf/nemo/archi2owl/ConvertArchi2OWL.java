package br.ufes.inf.nemo.archi2owl;

import java.util.HashMap;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;

import br.ufes.inf.nemo.archi2owl.utils.Utils;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.impl.AndJunction;
import com.archimatetool.model.impl.AssociationRelationship;
import com.archimatetool.model.impl.Junction;
import com.archimatetool.model.impl.OrJunction;
import com.archimatetool.model.impl.Relationship;
import com.archimatetool.model.impl.SpecialisationRelationship;

/**
 * Class to convert ArchiMate elements to OWL (Ontology Web Language)
 * 
 * @author Lucas Bassetti
 * @version 0.1
 */

public class ConvertArchi2OWL {

	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private OWLDataFactory factory;
	
	private String 	base,
					namespace;
	
	HashMap<String, String> propertyHashMap, inversePropertyHashMap;
	

	public ConvertArchi2OWL(String namespace){
		
		this.namespace = namespace;
		this.base = namespace.replace("#", "");
		
		manager = OWLManager.createOWLOntologyManager();		
		try {
			ontology = manager.createOntology(IRI.create(base));
			factory = manager.getOWLDataFactory();			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Generate properties and inverse properties hash map
		generatePropertyHashMap();
		generateInversePropertyHashMap();
	}
	
	/**
	 * Method to generate the properties hash map
	 * @author Lucas Bassetti
	 * @version 0.1
	 */
	
	public void generatePropertyHashMap(){
		
		this.propertyHashMap = new HashMap<String, String>();
		
		this.propertyHashMap.put("AccessRelationship", "accesses");
		this.propertyHashMap.put("CompositionRelationship", "composes");
		this.propertyHashMap.put("FlowRelationship", "flowsTo");
		this.propertyHashMap.put("AggregationRelationship", "aggregates");
		this.propertyHashMap.put("AssignmentRelationship", "assignedTo");
		this.propertyHashMap.put("InfluenceRelationship", "influences");
		this.propertyHashMap.put("AssociationRelationship", "associatedWith");
		this.propertyHashMap.put("RealisationRelationship", "realizes");
		this.propertyHashMap.put("TriggeringRelationship", "triggers");
		this.propertyHashMap.put("UsedByRelationship", "usedBy");
		
	}
	
	/**
	 * Method to generate the inverse properties hash map
	 * @author Lucas Bassetti
	 * @version 0.1
	 */
	
	public void generateInversePropertyHashMap(){
		
		this.inversePropertyHashMap = new HashMap<String, String>();
		
		this.inversePropertyHashMap.put("AccessRelationship", "accessedBy");
		this.inversePropertyHashMap.put("CompositionRelationship", "composedOf");
		this.inversePropertyHashMap.put("FlowRelationship", "flowFrom");
		this.inversePropertyHashMap.put("AggregationRelationship", "aggregatedBy");
		this.inversePropertyHashMap.put("AssignmentRelationship", "assignedFrom");
		this.inversePropertyHashMap.put("InfluenceRelationship", "influencedBy");
		this.inversePropertyHashMap.put("RealisationRelationship", "realizedBy");
		this.inversePropertyHashMap.put("TriggeringRelationship", "triggeredBy");
		this.inversePropertyHashMap.put("UsedByRelationship", "uses");
		
	}
	
	/**
	 * Method to convert ArchiMate elements to OWL Classes and Properties
	 * @author Lucas Bassetti
	 * @version 0.1
	 * @param list
	 * @return void
	 */
	
	public void convert(List<IArchimateElement> list){
		
		for(IArchimateElement eObject : list) {
			if(eObject instanceof IArchimateElement) {
				IArchimateElement element = (IArchimateElement)eObject;

				//element.eClass().setName(Utils.formatString(element.eClass().getName()));
				
				// Relationships
				 
				if(element instanceof Relationship){
					
					IArchimateElement elementSource = ((IArchimateElement)((Relationship) element).getSource());
					IArchimateElement elementTarget = ((IArchimateElement)((Relationship) element).getTarget());
					
					String source = Utils.formatString(elementSource.getName());
					String target = Utils.formatString(elementTarget.getName());
					
					if(!(elementSource instanceof Junction) && !(elementSource instanceof AndJunction) && !(elementSource instanceof OrJunction)
						&& (!(elementTarget instanceof Junction) && !(elementTarget instanceof AndJunction) && !(elementTarget instanceof OrJunction))
						)
					{
						
						String relationIRI = namespace + element.eClass().getName() + "_" + source + "_" + target,
							   inverseRelationIRI = namespace + element.eClass().getName() + "_" + target + "_" + source,
							   domainIRI = namespace + source,
							   rangeIRI = namespace + target;
						
						OWLClass domainClass = factory.getOWLClass(IRI.create(domainIRI));
						OWLClass rangeClass = factory.getOWLClass(IRI.create(rangeIRI));
						
						// s = SpecialisationRelationship
						 
						if(element instanceof SpecialisationRelationship){
							generateOWLSubClassOfAxiom(domainClass, rangeClass);
						}
						
						// Other Relationships
						 
						else{
		
							// GENERATE RELATIONSHIP
							
							relationIRI = relationIRI.replaceAll(element.eClass().getName(), propertyHashMap.get(element.eClass().getName()));

							// Generalized Object Property
							
							OWLObjectProperty generalizedObjectProperty = factory.getOWLObjectProperty(IRI.create(namespace + propertyHashMap.get(element.eClass().getName())));
							generateOWLDeclarationAxiom(generalizedObjectProperty);
							
							// Specialized Object Property
							
							OWLObjectProperty specializedObjectProperty = factory.getOWLObjectProperty(IRI.create(relationIRI));
							generateOWLDeclarationAxiom(specializedObjectProperty);
							generateOWLSubObjectPropertyOfAxiom(specializedObjectProperty, generalizedObjectProperty);
							generateOWLObjectPropertyDomainAxiom(specializedObjectProperty, domainClass);
							generateOWLObjectPropertyRangeAxiom(specializedObjectProperty, rangeClass);
							
							// GENERATE SYMMETRIC RELATIONSHIP
							
							if(element instanceof AssociationRelationship){
								
								// Generalized Object Property
								
								generateOWLSymmetricObjectPropertyAxiom(generalizedObjectProperty);	
								
								// Specialized Object Property
								
								generateOWLSymmetricObjectPropertyAxiom(specializedObjectProperty);
								generateOWLObjectPropertyDomainAxiom(specializedObjectProperty, rangeClass);
								generateOWLObjectPropertyRangeAxiom(specializedObjectProperty, domainClass);
							}
							
							// GENERATE INVERSE RELATIONSHIP
							
							else{
								
								inverseRelationIRI = inverseRelationIRI.replaceAll(element.eClass().getName(), inversePropertyHashMap.get(element.eClass().getName()));
								
								// Generalized Object Property
								
								OWLObjectProperty inverseGeneralizedObjectProperty = factory.getOWLObjectProperty(IRI.create(namespace + inversePropertyHashMap.get(element.eClass().getName())));
								generateOWLDeclarationAxiom(inverseGeneralizedObjectProperty);
								generateOWLInverseObjectPropertiesAxiom(generalizedObjectProperty, inverseGeneralizedObjectProperty);
								
								// Specialized Object Property
								
								OWLObjectProperty inverseSpecializedObjectProperty = factory.getOWLObjectProperty(IRI.create(inverseRelationIRI));
								generateOWLDeclarationAxiom(inverseSpecializedObjectProperty);
								generateOWLSubObjectPropertyOfAxiom(inverseSpecializedObjectProperty, inverseGeneralizedObjectProperty);
								generateOWLObjectPropertyDomainAxiom(inverseSpecializedObjectProperty, rangeClass);
								generateOWLObjectPropertyRangeAxiom(inverseSpecializedObjectProperty, domainClass);
								generateOWLInverseObjectPropertiesAxiom(specializedObjectProperty, inverseSpecializedObjectProperty);
							}
						}		
					}
				}
				
				// Classes
				
				else if(!(element instanceof Junction) && !(element instanceof AndJunction) && !(element instanceof OrJunction)){

					String classIRI = namespace + Utils.formatString(element.getName());
					
					OWLClass classOWL = factory.getOWLClass(IRI.create(classIRI));				
					generateOWLDeclarationAxiom(classOWL);				
				}

			}
		}

	}

	
	/**
	 * Method to generate a OWLDeclaration Axiom and add on the ontology
	 * @author Lucas Bassetti
	 * @version 0.1
	 * @param axiom
	 * @return void
	 */
	
	public <A> void generateOWLDeclarationAxiom(A axiom){		
		OWLDeclarationAxiom declareClass = factory.getOWLDeclarationAxiom((OWLEntity) axiom);
		manager.addAxiom(ontology, declareClass);	
	}
	
	/**
	 * Method to generate a OWLSubClassOf Axiom and add on the ontology
	 * @author Lucas Bassetti
	 * @version 0.1
	 * @param specializedClass
	 * @param generalizedClass
	 * @return void
	 */
	
	public void generateOWLSubClassOfAxiom(OWLClass specializedClass, OWLClass generalizedClass){	
		OWLSubClassOfAxiom subClassOf = factory.getOWLSubClassOfAxiom(specializedClass, generalizedClass);		
		manager.addAxiom(ontology, subClassOf);	
	}

	/**
	 * Method to generate a OWLSubObjectPropertyOf Axiom and add on the ontology
	 * @author Lucas Bassetti
	 * @version 0.1
	 * @param specializedObjectProperty
	 * @param generalizedObjectProperty
	 * @return void
	 */
	
	public void generateOWLSubObjectPropertyOfAxiom(OWLObjectProperty specializedObjectProperty, OWLObjectProperty generalizedObjectProperty){	
		OWLSubObjectPropertyOfAxiom subObjectPropertyOf = factory.getOWLSubObjectPropertyOfAxiom(specializedObjectProperty, generalizedObjectProperty);		
		manager.addAxiom(ontology, subObjectPropertyOf);	
	}
	
	/**
	 * Method to generate a OWLInverseObjectPropertyOf Axiom and add on the ontology
	 * @author Lucas Bassetti
	 * @version 0.1
	 * @param objectProperty
	 * @param inverseObjectProperty
	 * @return void
	 */
	
	public void generateOWLInverseObjectPropertiesAxiom(OWLObjectProperty objectProperty, OWLObjectProperty inverseObjectProperty){	
		OWLInverseObjectPropertiesAxiom inverseObjectProperties = factory.getOWLInverseObjectPropertiesAxiom(objectProperty, inverseObjectProperty);		
		manager.addAxiom(ontology, inverseObjectProperties);	
	}
	
	/**
	 * Method to generate a OWLSymmetricObjectProperty Axiom and add on the ontology
	 * @author Lucas Bassetti
	 * @version 0.1
	 * @param objectProperty
	 * @param inverseObjectProperty
	 * @return void
	 */
	
	public void generateOWLSymmetricObjectPropertyAxiom(OWLObjectProperty objectProperty){	
		OWLSymmetricObjectPropertyAxiom symmetricObjectProperty = factory.getOWLSymmetricObjectPropertyAxiom(objectProperty);
		manager.addAxiom(ontology, symmetricObjectProperty);	
	}
	
	/**
	 * Method to generate a OWLObjectPropertyDomain Axiom and add on the ontology
	 * @author Lucas Bassetti
	 * @version 0.1
	 * @param objectProperty
	 * @param domainClass
	 * @return void
	 */
	
	public void generateOWLObjectPropertyDomainAxiom(OWLObjectProperty objectProperty, OWLClass domainClass){		
		OWLObjectPropertyDomainAxiom domainAxiom = factory.getOWLObjectPropertyDomainAxiom(objectProperty, domainClass);
		manager.addAxiom(ontology, domainAxiom);	
	}
	
	/**
	 * Method to generate a OWLObjectPropertyRange Axiom and add on the ontology
	 * @author Lucas Bassetti
	 * @version 0.1
	 * @param objectProperty
	 * @param rangeClass
	 * @return void
	 */
	
	public void generateOWLObjectPropertyRangeAxiom(OWLObjectProperty objectProperty, OWLClass rangeClass){		
		OWLObjectPropertyRangeAxiom rangeAxiom = factory.getOWLObjectPropertyRangeAxiom(objectProperty, rangeClass);
		manager.addAxiom(ontology, rangeAxiom);	
	}
	
	
	
	
	/*
	 * Getters and Setters
	 */
	
	public OWLOntologyManager getManager() {
		return manager;
	}

	public void setManager(OWLOntologyManager manager) {
		this.manager = manager;
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}

	public OWLDataFactory getFactory() {
		return factory;
	}

	public void setFactory(OWLDataFactory factory) {
		this.factory = factory;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	
	public String getNamespace() {
		return namespace;
	}


	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	

}
