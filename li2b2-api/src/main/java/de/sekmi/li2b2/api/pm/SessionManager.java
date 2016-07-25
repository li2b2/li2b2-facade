package de.sekmi.li2b2.api.pm;

public interface SessionManager {

	
	public interface Session{
		String getId();
		String getUserId();
		String getProjectId();
		String getToken();
		// XXX set project id? or set at creation?
		long getCreationTime();
		long getLastAccess();
	}
	
	/** 
	 * Access an existing session by its session id.
	 * This operation will also update the {@link Session#getLastAccess()}
	 * time stamp.
	 * 
	 * @param sessionId session id
	 * @return session or {@code null} if not available or expired.
	 */
	public Session accessSession(String sessionId);
	
	public Session createSession(String userId);
	public void deleteSession(String sessionId);
	

	/**
	 * Get the timeout duration in milliseconds. Sessions
	 * last accessed longer than this will be discarded.
	 * @return timeout in milliseconds
	 */
	public long getTimeoutMillis();
	/**
	 * Iterate over all sessions. The remove() operation should
	 * be supported by the iterators.
	 * @return all sessions
	 */
	Iterable<Session> allSessions();
}
