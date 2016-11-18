package de.sekmi.li2b2.api.work;

import java.io.IOException;
import java.util.List;

public interface WorkplaceManager {

	List<WorkplaceItem> getFoldersByUserId(String userId, String domain) throws IOException;
	
	WorkplaceItem getItem(String itemId);
	void deleteItem(String itemId);
	List<WorkplaceItem> getChildren(String parentId);
	void moveItem(String itemId, String newParentId);
}
