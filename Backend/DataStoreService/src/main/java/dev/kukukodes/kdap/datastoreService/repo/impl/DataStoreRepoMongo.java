package dev.kukukodes.kdap.datastoreService.repo.impl;

import dev.kukukodes.kdap.datastoreService.constants.DbConst;
import dev.kukukodes.kdap.datastoreService.entity.dataStore.DataStore;
import dev.kukukodes.kdap.datastoreService.repo.IDataStoreRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Repository
public class DataStoreRepoMongo implements IDataStoreRepo {

    private final MongoTemplate template;

    public DataStoreRepoMongo(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public DataStore addDataStore(DataStore dataStore) {
        log.info("Adding datastore to database : {}", dataStore);
        DataStore addedDs = template.insert(dataStore);
        log.info("Added datastore to database : {}", addedDs);
        return addedDs;

    }

    @Override
    public boolean updateDataStore(DataStore dataStore) {
        Query query = Query.query(Criteria.where("_id").is(dataStore.getId()));
        Update update = new Update();
        update.set(DbConst.DocumentFields.CommonFields.NAME, dataStore.getName());
        update.set(DbConst.DocumentFields.CommonFields.DESCRIPTION, dataStore.getDescription());
        update.set(DbConst.DocumentFields.DataStore.FIELDS, dataStore.getFields());
        var modifiedTime = LocalDate.now();
        update.set(DbConst.DocumentFields.CommonFields.MODIFIED, modifiedTime);
        update.set(DbConst.DocumentFields.DataStore.USER_ID, dataStore.getUserID());
        log.info("Updating name {}, description {}, fields {}, modified {}, userID {}", dataStore.getName(), dataStore.getDescription(), dataStore.getFields(), modifiedTime, dataStore.getUserID());
        var updated = template.updateFirst(query, update, DataStore.class);
        return updated.wasAcknowledged();
    }

    @Override
    public DataStore getDataStoreByID(String id) {
        log.info("Getting datastore whose id is {}", id);
        DataStore foundDs = template.findOne(Query.query(Criteria.where(DbConst.DocumentFields.CommonFields.ID).is(id)), DataStore.class);
        log.info("Found dataStore : {}", foundDs);
        return foundDs;
    }

    @Override
    public List<DataStore> getDataStoresByUserID(String userID) {
        log.info("Getting all data stores of user : {}", userID);
        var foundDs = template.find(Query.query(Criteria.where(DbConst.DocumentFields.DataStore.USER_ID).is(userID)), DataStore.class);
        log.info("found dataStore : {}", foundDs);
        return foundDs;
    }
}
