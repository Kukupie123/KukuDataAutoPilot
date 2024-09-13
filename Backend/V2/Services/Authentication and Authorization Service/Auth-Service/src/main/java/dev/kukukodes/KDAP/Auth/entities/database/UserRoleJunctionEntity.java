package dev.kukukodes.KDAP.Auth.entities.database;

import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(DbConstants.TableNames.UserRolesJunction)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleJunctionEntity {
    @Id
    private ID id;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ID {
        @Column(DbConstants.TableColumnNames.UserRolesJunctionTable.userID)
        private Integer userID;
        @Column(DbConstants.TableColumnNames.UserRolesJunctionTable.roleID)
        private Integer roleID;
    }
}
