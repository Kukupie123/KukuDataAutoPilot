package dev.kukukodes.kdap.datastoreService.repo.impl;

import dev.kukukodes.kdap.datastoreService.constants.DbConst;
import dev.kukukodes.kdap.datastoreService.entity.dataStore.DataBox;
import dev.kukukodes.kdap.datastoreService.repo.IDataBoxRepo;
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
public class DataBoxRepoMongo implements IDataBoxRepo {

    private final MongoTemplate template;

    public DataBoxRepoMongo(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public DataBox addDataStore(DataBox dataStore) {
        log.info("Adding datastore to database : {}", dataStore);
        DataBox addedDs = template.insert(dataStore);
        log.info("Added datastore to database : {}", addedDs);
        return addedDs;

    }

    @Override
    public boolean updateDataStore(DataBox dataStore) {
        Query query = Query.query(Criteria.where("_id").is(dataStore.getId()));
        Update update = new Update();

        if (dataStore.getName() != null) {
            update.set(DbConst.DocumentFields.CommonFields.NAME, dataStore.getName());
        }

        if (dataStore.getDescription() != null) {
            update.set(DbConst.DocumentFields.CommonFields.DESCRIPTION, dataStore.getDescription());
        }

        if (dataStore.getFields() != null) {
            update.set(DbConst.DocumentFields.DataStore.FIELDS, dataStore.getFields());
        }

        var modifiedTime = LocalDate.now();
        update.set(DbConst.DocumentFields.CommonFields.MODIFIED, modifiedTime);

        if (dataStore.getUserID() != null) {
            update.set(DbConst.DocumentFields.DataStore.USER_ID, dataStore.getUserID());
        }

        log.info("Updating name {}, description {}, fields {}, modified {}, userID {}",
                dataStore.getName(), dataStore.getDescription(), dataStore.getFields(), modifiedTime, dataStore.getUserID());

        var updated = template.updateFirst(query, update, DataBox.class);
        return updated.wasAcknowledged();
    }


    @Override
    public DataBox getDataStoreByID(String id) {
        log.info("Getting datastore whose id is {}", id);
        DataBox foundDs = template.findOne(Query.query(Criteria.where(DbConst.DocumentFields.CommonFields.ID).is(id)), DataBox.class);
        log.info("Found dataStore : {}", foundDs);
        return foundDs;
    }

    @Override
    public List<DataBox> getDataStoresByUserID(String userID) {
        log.info("Getting all data stores of user : {}", userID);
        var foundDs = template.find(Query.query(Criteria.where(DbConst.DocumentFields.DataStore.USER_ID).is(userID)), DataBox.class);
        log.info("found dataStore : {}", foundDs);
        return foundDs;
    }
}
