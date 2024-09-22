package dev.kukukodes.kdap.datastoreService.repo;


import dev.kukukodes.kdap.datastoreService.entity.dataStore.DataStore;
import dev.kukukodes.kdap.datastoreService.repo.impl.DataStoreRepoMongo;

import java.util.List;

public interface IDataStoreRepo {
    /**
     * Add new data store
     */
    DataStore addDataStore(DataStore dataStore);

    /**
     * Update the data store info excluding created and id
     */
    boolean updateDataStore(DataStore dataStore);

    /**
     * Get data store based on its ID
     */
    DataStore getDataStoreByID(String id);

    /**
     * Get Data stores
     * @param userID
     * @return
     */
    List<DataStore> getDataStoresByUserID(String userID);
}
