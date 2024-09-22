package dev.kukukodes.kdap.datastoreService.entity.dataEntry;

import dev.kukukodes.kdap.datastoreService.constants.DbConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Document(DbConst.Collections.DATA_ENTRIES)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataEntry {
    @MongoId(FieldType.STRING)
    private String id;
    @Field(DbConst.DocumentFields.DataEntry.STORE_ID)
    private String storeID;
    @Field(DbConst.DocumentFields.CommonFields.CREATED)
    private LocalDate created;
    @Field(DbConst.DocumentFields.CommonFields.MODIFIED)
    private LocalDate modified;
    @Field(DbConst.DocumentFields.DataEntry.VALUE)
    private List<DataEntryValue> values;

    public DataEntry(String storeID, List<DataEntryValue> values){
        this.storeID = storeID;
        this.id = UUID.randomUUID().toString();
        this.created = LocalDate.now();
        this.modified = LocalDate.now();
        this.values = values;
    }
}
