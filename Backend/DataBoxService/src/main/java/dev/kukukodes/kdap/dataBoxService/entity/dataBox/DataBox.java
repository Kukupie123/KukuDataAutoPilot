package dev.kukukodes.kdap.dataBoxService.entity.dataBox;


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
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(DbConst.Collections.DATA_BOX)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataBox  implements  Serializable{
    @Field(DbConst.DocumentFields.DataBox.FIELDS)
    List<DataBoxField> fields;
    @MongoId(FieldType.STRING)
    private String id;
    @Field(DbConst.DocumentFields.DataBox.USER_ID)
    private String userID; //Index this
    @Field(DbConst.DocumentFields.CommonFields.NAME)
    private String name;
    @Field(DbConst.DocumentFields.CommonFields.DESCRIPTION)
    private String description;
    @Field(DbConst.DocumentFields.CommonFields.CREATED)
    private LocalDate created;
    @Field(DbConst.DocumentFields.CommonFields.MODIFIED)
    private LocalDate modified;

    public DataBox(String userID, String name, String description, List<DataBoxField> fields) {
        this.userID = userID;
        this.name = name;
        this.description = description;
        this.fields = fields;
        this.created = LocalDate.now();
        this.modified = LocalDate.now();
        this.id = UUID.randomUUID().toString();
    }
}
