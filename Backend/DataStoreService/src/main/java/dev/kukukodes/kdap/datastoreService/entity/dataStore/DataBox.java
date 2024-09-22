package dev.kukukodes.kdap.datastoreService.entity.dataStore;


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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(DbConst.Collections.DATA_BOX)
public class DataBox {
    @MongoId(FieldType.STRING)
    private String id;
    @Field(DbConst.DocumentFields.DataStore.USER_ID)
    private String userID; //Needs to be indexed
    @Field(DbConst.DocumentFields.CommonFields.NAME)
    private String name;
    @Field(DbConst.DocumentFields.CommonFields.DESCRIPTION)
    private String description;
    @Field(DbConst.DocumentFields.CommonFields.CREATED)
    private LocalDate created;
    @Field(DbConst.DocumentFields.CommonFields.MODIFIED)

    private LocalDate modified;
    @Field(DbConst.DocumentFields.DataStore.FIELDS)
    List<DataBoxField> fields;
}
