package dev.kukukodes.kdap.datastoreService.repo;

import dev.kukukodes.kdap.datastoreService.entity.dataEntry.DataEntry;

public interface IDataEntryRepo {

    DataEntry getDataEntry(String id);
}
