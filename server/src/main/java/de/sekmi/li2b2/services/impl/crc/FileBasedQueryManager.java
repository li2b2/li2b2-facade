package de.sekmi.li2b2.services.impl.crc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import de.sekmi.li2b2.api.crc.Query;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class FileBasedQueryManager extends AbstractQueryManager{
	private static final Logger log = Logger.getLogger(FileBasedQueryManager.class.getName());

	@XmlTransient
	private Path xmlFlushTarget;
	@XmlTransient
	private Path xmlQueryDir;


	public void setFlushDestination(Path config, Path queryDir) throws IOException {
		this.xmlFlushTarget = config;
		this.xmlQueryDir = queryDir;
		if( !Files.isDirectory(xmlQueryDir) ) {
			Files.createDirectory(xmlQueryDir);
		}
	}

	@Override
	public void flushManager() {
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

	@Override
	public void flushQuery(QueryImpl query) {
		if( xmlQueryDir == null ) {
			return; // skip flushing, no directory specified
		}
		Path dest = getQueryPath(query);
		JAXB.marshal(query, dest.toFile());
	}

	@Override
	public void loadAllQueries() throws IOException{
		Objects.requireNonNull(xmlQueryDir);
		List<QueryImpl> queries = new ArrayList<>();
		try( Stream<Path> files = Files.list(xmlQueryDir) ){
			Iterator<Path> i = files.iterator();
			while( i.hasNext() ) {
				Path path = i.next();
				log.info("Unmarshalling "+path.toFile());
				QueryImpl query = JAXB.unmarshal(path.toFile(), QueryImpl.class);
				// TODO catch unmarshalexception
				queries.add(query);
			}
		}
		this.queries = queries;
	}

	@Override
	public void deleteQuery(Query query) throws IOException{
		super.deleteQuery(query);
		// delete query from filesystem
		Path path = getQueryPath(query);
		Files.deleteIfExists(path);
	}

}
