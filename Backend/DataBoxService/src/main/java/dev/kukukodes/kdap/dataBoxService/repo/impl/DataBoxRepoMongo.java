package dev.kukukodes.kdap.dataBoxService.repo.impl;

import com.mongodb.client.result.DeleteResult;
import dev.kukukodes.kdap.dataBoxService.constants.DbConst;
import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.repo.IDataBoxRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DataBoxRepoMongo implements IDataBoxRepo {

    private final MongoTemplate template;

    @Override
    public DataBox addDataStore(DataBox dataBox) {
        log.info("Adding datastore to database : {}", dataBox);
        DataBox addedDs = template.insert(dataBox);
        log.info("Added datastore to database : {}", addedDs);
        return addedDs;

    }

    @Override
    public boolean updateDataStore(DataBox dataBox) {
        Query query = Query.query(Criteria.where(DbConst.DocumentFields.CommonFields.ID).is(dataBox.getId()));
        log.info("Query: {}", query);

        Update update = new Update();
        boolean hasUpdates = false;

        if (dataBox.getName() != null) {
            update.set(DbConst.DocumentFields.CommonFields.NAME, dataBox.getName());
            hasUpdates = true;
        }

        if (dataBox.getDescription() != null) {
            update.set(DbConst.DocumentFields.CommonFields.DESCRIPTION, dataBox.getDescription());
            hasUpdates = true;
        }

        if (dataBox.getFields() != null) {
            update.set(DbConst.DocumentFields.DataBox.FIELDS, dataBox.getFields());
            hasUpdates = true;
        }

        if (dataBox.getUserID() != null) {
            update.set(DbConst.DocumentFields.DataBox.USER_ID, dataBox.getUserID());
            hasUpdates = true;
        }

        if (!hasUpdates) {
            log.warn("No updates to perform. All fields are null.");
            return false;
        }

        log.info("Update object: {}", update);

        log.info("Updating {} with name {}, description {}, fields {}, modified {}, userID {}",
                dataBox.getId(), dataBox.getName(), dataBox.getDescription(), dataBox.getFields(), dataBox.getModified(), dataBox.getUserID());

        var updated = template.updateFirst(query, update, DataBox.class);
        log.info("Update result: {}", updated);
        return updated.getModifiedCount() > 0;
    }


    @Override
    public DataBox getDataBoxByID(String id) {
        log.info("Getting datastore whose id is {}", id);
        DataBox foundDs = template.findOne(Query.query(Criteria.where(DbConst.DocumentFields.CommonFields.ID).is(id)), DataBox.class);
        log.info("Found dataBox : {}", foundDs);
        return foundDs;
    }

    @Override
    public List<DataBox> getDataStoresByUserID(String userID) {
        log.info("Getting all data stores of user : {}", userID);
        var foundDs = template.find(Query.query(Criteria.where(DbConst.DocumentFields.DataBox.USER_ID).is(userID)), DataBox.class);
        log.info("found dataBox : {}", foundDs);
        return foundDs;
    }

    @Override
    public boolean deleteDataBox(String id) {
        log.info("Deleting DataBox : {}", id);
        DeleteResult deleteResult = template.remove(Query.query(Criteria.where(DbConst.DocumentFields.CommonFields.ID).is(id)), DataBox.class);
        return deleteResult.getDeletedCount() > 0;
    }

    @Override
    public List<DataBox> getAllDatastore(int skip, int limit) {
        log.info("Getting all databoxes with skip: {} and limit: {}", skip, limit);

        Query query = new Query()
                .skip(skip)
                .limit(limit);

        return template.find(query, DataBox.class);
    }

}
