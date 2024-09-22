package dev.kukukodes.kdap.dataBoxService.repo;

import dev.kukukodes.kdap.dataBoxService.entity.dataEntry.DataEntry;

import java.util.List;

public interface IDataEntryRepo {
    DataEntry addDateEntry(DataEntry dataEntry);
    boolean updateDateEntry(DataEntry dataEntry);
    boolean deleteDateEntry(DataEntry dataEntry);
    DataEntry getDataEntry(String id);
    List<DataEntry> getDataEntriesByBoxID(String boxID);
}
