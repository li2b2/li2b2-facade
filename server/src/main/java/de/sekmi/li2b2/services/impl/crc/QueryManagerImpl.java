package de.sekmi.li2b2.services.impl.crc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.w3c.dom.Element;

import de.sekmi.li2b2.api.crc.QueryManager;
import de.sekmi.li2b2.api.crc.QueryStatus;
import de.sekmi.li2b2.api.crc.Query;
import de.sekmi.li2b2.api.crc.ResultType;
import de.sekmi.li2b2.util.JaxbAtomicIntegerAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public class QueryManagerImpl implements QueryManager{
	private static final Logger log = Logger.getLogger(QueryManagerImpl.class.getName());
	@XmlElement
	private List<ResultTypeImpl> types;
	@XmlTransient
	private List<QueryImpl> queries;
	@XmlElement
	@XmlJavaTypeAdapter(JaxbAtomicIntegerAdapter.class)
	private AtomicInteger querySeq;

	@XmlTransient
	private Path xmlFlushTarget;
	@XmlTransient
	private Path xmlQueryDir;

	public QueryManagerImpl(){
		types = new ArrayList<>(5);
		queries = new ArrayList<>();
		querySeq = new AtomicInteger();
	}
	public void addResultType(String name, String displayType, String description){
		types.add(new ResultTypeImpl(name, displayType, description));
	}
	/**
	 * Get result types from type names. The returned list will
	 * contain only result types which are supported by the
	 * query manager.
	 * 
	 * @param types type names
	 * @return result types
	 */
	private ResultType[] getSupportedResultTypes(String[] types){
		List<ResultType> ret = new ArrayList<>();
		for( String type : types ){
			ResultType t = getResultType(type);
			if( t != null ){
				ret.add(t);
			}
		}
		return ret.toArray(new ResultType[ret.size()]);
	}
	protected String getQueryNameFromQueryDefinition(Element definition) {
		return definition.getFirstChild().getTextContent();
	}

	/**
	 * Override this method to perform the actual execution
	 * @param query
	 */
	protected void executeQuery(QueryImpl query) {
		ExecutionImpl e = query.addExecution(QueryStatus.INCOMPLETE);
		// TODO perform execution
		e.setStartTimestamp(Instant.now());

		ResultImpl result = e.getResult("PATIENT_COUNT_XML");
		if( result != null ) {
			int count = new Random().nextInt(Integer.MAX_VALUE);
			result.setBreakdownData(new String[] {"patient_count"}, new int[] {count});
			result.setSetSize(count);
		}
		e.setEndTimestamp(Instant.now());
		
		e.setStatus(QueryStatus.FINISHED);
	}

	@Override
	public final QueryImpl runQuery(String userId, String groupId, Element definition, String[] results) {
		//ResultTypeImpl[] resultTypes = getResultTypes(results);
		// TODO check if all requested result types are supported

		QueryImpl q = new QueryImpl(querySeq.incrementAndGet(), userId, groupId, definition, results);

		// parse display name from i2b2 query definition
		String displayName = getQueryNameFromQueryDefinition(definition);
		if( displayName == null || displayName.isEmpty() ) {
			displayName = "Query "+q.getId();
		}
		q.setDisplayName(displayName);

		

		queries.add(q);
		flushQuery(q);
		flush(); // flush manager to save querySeq state

		executeQuery(q);

		flushQuery(q);
		return q;
	}

	@Override
	public final QueryImpl getQuery(int queryId) {
		for( int i=0; i<queries.size(); i++ ){
			QueryImpl q = queries.get(i);
			if( queryId == q.getId() ){
				return q;
			}
		}
		// not found
		return null;
	}

	@Override
	public final Iterable<? extends Query> listQueries(String userId) {
		// for now, all users can see all queries
		return queries;
	}

	@Override
	public final Iterable<? extends ResultType> getResultTypes() {
		return types;
	}
	@Override
	public void deleteQuery(Query query) throws IOException{
		
		queries.removeIf( q -> query.getId() == q.getId() );
		// TODO delete query from filesystem
		Path path = getQueryPath(query);
		Files.deleteIfExists(path);
	}
	@Override
	public void renameQuery(Query query, String newName) throws IOException{
		query.setDisplayName(newName);
		flushQuery((QueryImpl)query);
	}
//	@Override
//	public QueryExecution getExecution(String instanceId) {
//		return getQuery(instanceId);
//	}


	public void setFlushDestination(Path config, Path queryDir) throws IOException {
		this.xmlFlushTarget = config;
		this.xmlQueryDir = queryDir;
		if( !Files.isDirectory(xmlQueryDir) ) {
			Files.createDirectory(xmlQueryDir);
		}
	}

	public void flush() {
		if( xmlFlushTarget == null ) {
			// no persistence
			return;
		}
		log.info("Writing state to "+xmlFlushTarget);
		JAXB.marshal(this, xmlFlushTarget.toFile());
	}

	private Path getQueryPath(Query query) {
		return xmlQueryDir.resolve(Integer.toString(query.getId())+".xml");
	}
	public void flushQuery(QueryImpl query) {
		if( xmlQueryDir == null ) {
			return; // skip flushing
		}
		Path dest = getQueryPath(query);
		JAXB.marshal(query, dest.toFile());
	}

	public void loadQueries() throws IOException, JAXBException {
		Objects.requireNonNull(xmlQueryDir);
		queries = new ArrayList<>();
//		JAXBContext jc = JAXBContext.newInstance(QueryImpl.class);
//		Unmarshaller um = jc.createUnmarshaller();
		try( Stream<Path> files = Files.list(xmlQueryDir) ){
			Iterator<Path> i = files.iterator();
			while( i.hasNext() ) {
				Path path = i.next();
				log.info("Unmarshalling "+path.toFile());
				//QueryImpl query = (QueryImpl)um.unmarshal(path.toFile());
				QueryImpl query = JAXB.unmarshal(path.toFile(), QueryImpl.class);
				// TODO catch unmarshalexception
				queries.add(query);
			}
		}
	}
}
