package dev.kukukodes.kdap.dataBoxService.repo.impl;

import dev.kukukodes.kdap.dataBoxService.constants.DbConst;
import dev.kukukodes.kdap.dataBoxService.entity.dataEntry.DataEntry;
import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.helper.DataEntryHelper;
import dev.kukukodes.kdap.dataBoxService.repo.IDataBoxRepo;
import dev.kukukodes.kdap.dataBoxService.repo.IDataEntryRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class DataEntryRepoMongo implements IDataEntryRepo {
    private final MongoTemplate mongoTemplate;
    private final DataEntryHelper dataEntryHelper;
    private final IDataBoxRepo dataBoxRepo;

    public DataEntryRepoMongo(MongoTemplate mongoTemplate, DataEntryHelper dataEntryHelper, IDataBoxRepo dataBoxRepo) {
        this.mongoTemplate = mongoTemplate;
        this.dataEntryHelper = dataEntryHelper;
        this.dataBoxRepo = dataBoxRepo;
    }


    @Override
    public DataEntry addDateEntry(DataEntry dataEntry) {
        log.info("Adding entry {} to data-box {}", dataEntry, dataEntry.getBoxID());
        DataBox dataBox = dataBoxRepo.getDataBoxByID(dataEntry.getBoxID());
        //validate if all required fields are present
        boolean canAdd = dataEntryHelper.validateEntryForDataBox(dataBox, dataEntry);
        if (!canAdd) {
            log.info("Can't added entry {} to data box {}", dataEntry, dataEntry);
            return null;
        }
        return mongoTemplate.insert(dataEntry);
    }

    @Override
    public boolean updateDateEntry(DataEntry dataEntry) {
        log.info("updating data entry : {} for boxID {}", dataEntry, dataEntry.getBoxID());
        DataBox dataBox = dataBoxRepo.getDataBoxByID(dataEntry.getBoxID());
        Query query = Query.query(Criteria.where(DbConst.DocumentFields.CommonFields.ID).is(dataEntry.getId()));
        if (!dataEntryHelper.validateEntryForDataBox(dataBox, dataEntry)) {
            log.error("Failed to update entry {} for boxID {}", dataEntry, dataBox);
        }
        return mongoTemplate.replace(query, dataEntry).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteDateEntry(DataEntry dataEntry) {
        log.info("deleting entry {} for boxID {}", dataEntry, dataEntry.getBoxID());
        return mongoTemplate.remove(dataEntry).getDeletedCount() > 0;
    }

    @Override
    public DataEntry getDataEntry(String id) {
        log.info("getting data entry for id {}", id);
        return mongoTemplate.findOne(Query.query(Criteria.where(DbConst.DocumentFields.CommonFields.ID).is(id)), DataEntry.class);
    }

    @Override
    public List<DataEntry> getDataEntriesByBoxID(String boxID) {
        log.info("getting data entries for boxID {}", boxID);
        return mongoTemplate.find(Query.query(Criteria.where(DbConst.DocumentFields.DataEntry.STORE_ID).is(boxID)), DataEntry.class);
    }
}
