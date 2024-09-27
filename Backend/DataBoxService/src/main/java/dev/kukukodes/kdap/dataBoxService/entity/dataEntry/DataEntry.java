package dev.kukukodes.kdap.dataBoxService.entity.dataEntry;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.kukukodes.kdap.dataBoxService.constants.DbConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Document(DbConst.Collections.DATA_ENTRIES)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataEntry implements Serializable {
    @MongoId(FieldType.STRING)
    private String id;
    @Field(DbConst.DocumentFields.DataEntry.STORE_ID)
    private String boxID; //Index this
    @Field(DbConst.DocumentFields.CommonFields.CREATED)
    private LocalDate created;
    @Field(DbConst.DocumentFields.CommonFields.MODIFIED)
    private LocalDate modified;
    @Field(DbConst.DocumentFields.DataEntry.VALUE)
    //FieldName, Value
    private Map<String, String> values;

    public DataEntry(String boxID, Map<String, String> values) {
        this.boxID = boxID;
        this.id = UUID.randomUUID().toString();
        this.created = LocalDate.now();
        this.modified = LocalDate.now();
        this.values = values;
    }
}
