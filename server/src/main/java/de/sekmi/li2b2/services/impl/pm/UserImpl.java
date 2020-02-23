package de.sekmi.li2b2.services.impl.pm;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import de.sekmi.li2b2.api.pm.Parameter;
import de.sekmi.li2b2.api.pm.Project;
import de.sekmi.li2b2.api.pm.User;
import de.sekmi.li2b2.services.PMService;

/**
 * Implementation of li2b2 user.
 * Two users are equal, if their login and domain match.
 * 
 * @author R.W.Majeed
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class UserImpl implements User {
	
	@XmlTransient
	private ProjectManagerImpl pm;
	private String login;
	private String password;
	private Map<String,String> properties;
	private List<ParamImpl> params;

	public UserImpl(ProjectManagerImpl pm, String login){
		this.pm = pm;
		this.login = login;
		this.properties = new HashMap<>();
		this.params = new ArrayList<>();
		//this.domain = domain;
	}
	@Override
	public String getName() {
		return login;
	}
	protected void setName(String login) {
		this.login = login;
	}

	@Override
	public String getFullName() {
		return properties.get(PMService.USER_FULLNAME);
	}

	@Override
	public boolean isAdmin() {
		String admin = getProperty(PMService.USER_ISADMIN);
		return admin != null && admin.contentEquals(Boolean.TRUE.toString());
	}

	@Override
	public Iterable<Project> getProjects() {
		return pm.getUserProjects(this);
	}

	@Override
	public boolean hasPassword(char[] password) {
		if( this.password == null ){
			// no password, cannot login
			return false;
		}
		return this.password.equals(calculatePasswordDigest(password));
	}

	/**
	 * Calculate the message digest for the given password.
	 * To prevent rainbow table attacks, the user name is added to the digest input.
	 * @param password password
	 * @return message digest output
	 */
	private String calculatePasswordDigest(char[] password) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(pm.getPasswordDigestAlgorithm());
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Unsupported password digest algorithm "+pm.getPasswordDigestAlgorithm());
		}
		CharBuffer in = CharBuffer.allocate(login.length()+password.length);
		in.append(login);
		in.append(CharBuffer.wrap(password));
		in.flip();
		ByteBuffer bytes = StandardCharsets.UTF_8.encode(in);
		byte[] b = new byte[bytes.remaining()];
		bytes.get(b);
		
		b = md.digest(b);
		String result = Base64.getEncoder().encodeToString(b);
		return result;
	}
	@Override
	public void setPassword(char[] newPassword) {
		
		this.password = calculatePasswordDigest(newPassword);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + login.hashCode();
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserImpl other = (UserImpl) obj;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		
		if (!login.equals(other.login))
			return false;
		return true;
	}

	@Override
	public void setAdmin(boolean admin) {
		if( admin == false ) {
			properties.remove(PMService.USER_ISADMIN);
		}else {
			setProperty(PMService.USER_ISADMIN, Boolean.TRUE.toString());
		}
	}
	@Override
	public Map<String, String> getProperties() {
		return this.properties;
	}
	@Override
	public String getProperty(String key) {
		return properties.get(key);
	}
	@Override
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}
	@Override
	public List<ParamImpl> getParameters() {
		return this.params;
	}
	@Override
	public ParamImpl addParameter(String name, String datatype, String value) {
		ParamImpl param = new ParamImpl(name,datatype,value);
		this.params.add(param);
		return param;
	}
	@Override
	public Parameter updateParameter(int index, String name, String datatype, String value) {
		ParamImpl param = new ParamImpl(name,datatype,value);
		return this.params.set(index, param);
	}
}
