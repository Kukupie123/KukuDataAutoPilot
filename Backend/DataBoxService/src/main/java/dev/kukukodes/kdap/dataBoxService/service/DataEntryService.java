package dev.kukukodes.kdap.dataBoxService.service;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.entity.dataEntry.DataEntry;
import dev.kukukodes.kdap.dataBoxService.helper.DataEntryHelper;
import dev.kukukodes.kdap.dataBoxService.helper.SecurityHelper;
import dev.kukukodes.kdap.dataBoxService.repo.IDataEntryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataEntryService {
    private final DataEntryHelper dataEntryHelper;
    private final IDataEntryRepo dataEntryRepo;
    private final DataBoxService dataBoxService;
    private final SecurityHelper securityHelper;
    private final CacheService cacheService;
    //TODO: Publishing

    public DataEntry addDataEntryForBox(DataEntry dataEntry) throws Exception {
        log.info("Adding  entry {} to box {}", dataEntry, dataEntry.getBoxID());
        DataBox dataBox = dataBoxService.getDatabox(dataEntry.getBoxID());
        //Validate
        securityHelper.validateAccess(dataBox.getUserID());
        boolean canAdd = dataEntryHelper.validateEntryForDataBox(dataBox.getFields(), dataEntry);
        //Set default fields
        dataEntry.setCreated(LocalDate.now());
        dataEntry.setModified(LocalDate.now());
        dataEntry.setId(UUID.randomUUID().toString());
        if (!canAdd) {
            log.info("Can't added entry {} to data box {}", dataEntry, dataEntry);
            return null;
        }
        return dataEntryRepo.addDateEntry(dataEntry);
    }

    public boolean updateDataEntryForBox(DataEntry dataEntry) throws Exception {
        DataBox dataBox = dataBoxService.getDatabox(dataEntry.getBoxID());
        securityHelper.validateAccess(dataBox.getUserID());
        boolean canUpdate = dataEntryHelper.validateEntryForDataBox(dataBox.getFields(), dataEntry);
        if (!canUpdate) {
            log.error("Can't updated entry {} to data box {}", dataEntry, dataEntry);
            return false;
        }
        DataEntry dbDe = dataEntryRepo.getDataEntry(dataEntry.getBoxID());
        if (dbDe == null) {
            throw new FileNotFoundException("Failed to find dataEntry with id " + dataEntry.getBoxID());
        }
        //Update fields
        dataEntry.setModified(LocalDate.now());
        dataEntry.setCreated(dbDe.getCreated());
        log.info("Updating  entry {} to box {}", dataEntry, dataEntry.getBoxID());
        if (dataEntryRepo.updateDateEntry(dataEntry)) {
            cacheService.getDataEntryCache().clearDataEntry(dataEntry.getBoxID());
            return true;
        }
        return false;
    }

    public boolean deleteDataEntryForBox(String entryID) throws AccessDeniedException, FileNotFoundException {
        //No need to validate user. This function does it.
        DataEntry dbDe = getDataEntry(entryID);
        if (dataEntryRepo.deleteDateEntry(entryID)) {
            cacheService.getDataEntryCache().clearDataEntry(entryID);
            return true;
        }
        return false;
    }

    public DataEntry getDataEntry(String id) throws FileNotFoundException, AccessDeniedException {
        var foundData = cacheService.getDataEntryCache().getDataEntry(id);
        if (foundData == null) {
            foundData = dataEntryRepo.getDataEntry(id);
            if (foundData == null) {
                throw new FileNotFoundException("Failed to find dataEntry with id " + id);
            }
        }
        log.info("Getting data entry {}", id);
        DataBox dataBox = dataBoxService.getDatabox(foundData.getBoxID());
        securityHelper.validateAccess(dataBox.getUserID());
        return dataEntryRepo.getDataEntry(id);
    }

    public List<DataEntry> getDataEntriesByBoxID(String boxID, int skip, int limit) throws AccessDeniedException, FileNotFoundException {
        log.info("Getting data entries of box {}", boxID);
        DataBox dataBox = dataBoxService.getDatabox(boxID);
        securityHelper.validateAccess(dataBox.getUserID());
        return dataEntryRepo.getDataEntriesByBoxID(boxID, skip, limit);
    }
}
