package dev.kukukodes.kdap.dataBoxService.repo;


import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;

import java.util.List;

public interface IDataBoxRepo {
    /**
     * Add new data store.
     * UID, Created and updated will be updated manually.
     */
    DataBox addDataStore(DataBox dataStore);

    /**
     * Update the data store info excluding created and id.
     * Modified date will be updated internally
     */
    boolean updateDataStore(DataBox dataStore);

    /**
     * Get data store based on its ID
     */
    DataBox getDataBoxByID(String id);

    /**
     * Get Data stores
     */
    List<DataBox> getDataStoresByUserID(String userID);

    boolean deleteDataBox(String id);

    List<DataBox> getAllDatastore(int skip, int limit);
}
