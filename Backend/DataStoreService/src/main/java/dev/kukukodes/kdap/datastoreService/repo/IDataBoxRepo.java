package dev.kukukodes.kdap.datastoreService.repo;


import dev.kukukodes.kdap.datastoreService.entity.dataStore.DataBox;

import java.util.List;

public interface IDataBoxRepo {
    /**
     * Add new data store
     */
    DataBox addDataStore(DataBox dataStore);

    /**
     * Update the data store info excluding created and id
     */
    boolean updateDataStore(DataBox dataStore);

    /**
     * Get data store based on its ID
     */
    DataBox getDataStoreByID(String id);

    /**
     * Get Data stores
     * @param userID
     * @return
     */
    List<DataBox> getDataStoresByUserID(String userID);
}
