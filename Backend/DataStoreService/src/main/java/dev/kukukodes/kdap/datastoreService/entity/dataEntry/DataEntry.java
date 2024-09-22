package dev.kukukodes.kdap.datastoreService.entity.dataEntry;

import dev.kukukodes.kdap.datastoreService.constants.DbConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.util.List;

@Document(DbConst.Collections.DATA_ENTRIES)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataEntry {
    @MongoId(FieldType.STRING)
    private String id;
    private String storeID;
    private LocalDate created;
    private LocalDate modified;
    private List<DataEntryValue> values;
}
