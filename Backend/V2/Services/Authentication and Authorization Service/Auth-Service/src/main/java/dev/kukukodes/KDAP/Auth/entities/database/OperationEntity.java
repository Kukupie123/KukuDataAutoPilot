package dev.kukukodes.KDAP.Auth.entities.database;

import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Table(DbConstants.TableNames.Operations)
@Data
public class OperationEntity {

    @Id
    @Column(DbConstants.TableColumnNames.CommonColumns.id)
    Integer id;
    @Column(DbConstants.TableColumnNames.CommonColumns.name)
    String name;
    @Column(DbConstants.TableColumnNames.CommonColumns.description)
    String description;
    @Column(DbConstants.TableColumnNames.CommonColumns.created)
    Date created;
    @Column(DbConstants.TableColumnNames.CommonColumns.updated)
    Date updated;
    public OperationEntity(String name, String description, Date created, Date updated) {
        this.name = name;
        this.description = description;
        this.created = created;
        this.updated = updated;
        this.id = null;

    }

}
