package dev.kukukodes.kdap.dataBoxService.service;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.entity.dataEntry.DataEntry;
import dev.kukukodes.kdap.dataBoxService.helper.DataEntryHelper;
import dev.kukukodes.kdap.dataBoxService.repo.IDataEntryRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DataEntryService {
    private final DataEntryHelper dataEntryHelper;
    private final IDataEntryRepo dataEntryRepo;
    private final DataBoxService dataBoxService;

    public DataEntryService(DataEntryHelper dataEntryHelper, IDataEntryRepo dataEntryRepo, DataBoxService dataBoxService) {
        this.dataEntryHelper = dataEntryHelper;
        this.dataEntryRepo = dataEntryRepo;
        this.dataBoxService = dataBoxService;
    }

    public DataEntry addDataEntryForBox(DataEntry dataEntry) {
        log.info("Adding  entry {} to box {}", dataEntry, dataEntry.getBoxID());
        DataBox dataBox = dataBoxService.getDatabox(dataEntry.getBoxID());
        boolean canAdd = dataEntryHelper.validateEntryForDataBox(dataBox.getFields(), dataEntry);
        if (!canAdd) {
            log.info("Can't added entry {} to data box {}", dataEntry, dataEntry);
            return null;
        }
        return dataEntryRepo.addDateEntry(dataEntry);
    }

    public boolean updateDataEntryForBox(DataEntry dataEntry) {
        log.info("Updating  entry {} to box {}", dataEntry, dataEntry.getBoxID());
        DataBox dataBox = dataBoxService.getDatabox(dataEntry.getBoxID());
        boolean canUpdate = dataEntryHelper.validateEntryForDataBox(dataBox.getFields(), dataEntry);
        if (!canUpdate) {
            log.error("Can't updated entry {} to data box {}", dataEntry, dataEntry);
            return false;
        }
        return dataEntryRepo.updateDateEntry(dataEntry);
    }

    public boolean deleteDataEntryForBox(DataEntry dataEntry) {
        log.info("Deleting  entry {} from box {}", dataEntry, dataEntry.getBoxID());
        return dataEntryRepo.deleteDateEntry(dataEntry);
    }

    public DataEntry getDataEntryForBox(String id) {
        log.info("Getting data entry {}", id);
        return dataEntryRepo.getDataEntry(id);
    }

    public List<DataEntry> getDataEntriesByBoxID(String boxID) {
        log.info("Getting data entries of box {}", boxID);
        return dataEntryRepo.getDataEntriesByBoxID(boxID);
    }

    //TODO: Events publishing
}
