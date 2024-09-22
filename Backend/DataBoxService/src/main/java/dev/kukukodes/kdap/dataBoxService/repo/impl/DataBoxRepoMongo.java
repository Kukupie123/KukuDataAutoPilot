package dev.kukukodes.kdap.dataBoxService.repo.impl;

import dev.kukukodes.kdap.dataBoxService.constants.DbConst;
import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.repo.IDataBoxRepo;
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
    public DataBox addDataStore(DataBox dataBox) {
        log.info("Adding datastore to database : {}", dataBox);
        DataBox addedDs = template.insert(dataBox);
        log.info("Added datastore to database : {}", addedDs);
        return addedDs;

    }

    @Override
    public boolean updateDataStore(DataBox dataBox) {
        Query query = Query.query(Criteria.where(DbConst.DocumentFields.CommonFields.ID).is(dataBox.getId()));
        Update update = new Update();

        if (dataBox.getName() != null) {
            update.set(DbConst.DocumentFields.CommonFields.NAME, dataBox.getName());
        }

        if (dataBox.getDescription() != null) {
            update.set(DbConst.DocumentFields.CommonFields.DESCRIPTION, dataBox.getDescription());
        }

        if (dataBox.getFields() != null) {
            update.set(DbConst.DocumentFields.DataBox.FIELDS, dataBox.getFields());
        }

        var modifiedTime = LocalDate.now();
        update.set(DbConst.DocumentFields.CommonFields.MODIFIED, modifiedTime);

        if (dataBox.getUserID() != null) {
            update.set(DbConst.DocumentFields.DataBox.USER_ID, dataBox.getUserID());
        }

        log.info("Updating name {}, description {}, fields {}, modified {}, userID {}",
                dataBox.getName(), dataBox.getDescription(), dataBox.getFields(), modifiedTime, dataBox.getUserID());

        var updated = template.updateFirst(query, update, DataBox.class);
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
}
