package dev.kukukodes.kdap.datastoreService.repo.impl;

import dev.kukukodes.kdap.datastoreService.entity.dataStore.DataBox;
import dev.kukukodes.kdap.datastoreService.entity.dataStore.DataBoxField;
import dev.kukukodes.kdap.datastoreService.enums.DataStoreFieldType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataMongoTest
class DataBoxRepoMongoTest {

    private DataBoxRepoMongo repo;
    private DataBox testDs;
    private List<DataBoxField> testFields;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        repo = new DataBoxRepoMongo(mongoTemplate);
        testFields = new ArrayList<>();
        testFields.add(new DataBoxField("name", DataStoreFieldType.TEXT, true));
        testFields.add(new DataBoxField("age", DataStoreFieldType.INTEGER_NUMBER, true));
        testDs = new DataBox("randomID", "testUser", "testName", "desc", LocalDate.now(), LocalDate.now(), testFields);
    }

    @AfterEach
    void tearDown() {
        testFields.clear();
        testDs = null;
        mongoTemplate.remove(new Query(), DataBox.class); // Removes all records
    }

    @Test
    void addDataStore() {
        var addedUser = repo.addDataStore(testDs);
        assertThat(addedUser).isNotNull();
        assertThat(addedUser.getId()).isNotNull().isEqualTo(testDs.getId());
        assertThat(addedUser.getName()).isNotNull().isEqualTo(testDs.getName());
        assertThat(addedUser.getDescription()).isNotNull().isEqualTo(testDs.getDescription());
        assertThat(addedUser.getFields().size()).isEqualTo(testFields.size());
    }

    @Test
    void updateDataStore() {
        var updatedUser = repo.addDataStore(testDs);
        updatedUser.setName("updatedName");
        var updated = repo.updateDataStore(updatedUser);
        assertThat(updated).isNotNull().isEqualTo(true);

    }

    @Test
    void getDataStoreByID() {
        var addedUser = repo.addDataStore(testDs);
        var foundDS = repo.getDataStoreByID(testDs.getId());
        assertThat(foundDS).isNotNull();
        assertThat(foundDS.getId()).isEqualTo(testDs.getId());
        assertThat(foundDS.getName()).isEqualTo(testDs.getName());
        assertThat(foundDS.getDescription()).isEqualTo(testDs.getDescription());
        assertThat(foundDS.getFields().size()).isEqualTo(testFields.size());
    }

    @Test
    void getDataStoresByUserID() {
        var addedUser = repo.addDataStore(testDs);
        var foundDSs = repo.getDataStoresByUserID(testDs.getUserID());
        assertThat(foundDSs).isNotNull();
        assertThat(foundDSs.isEmpty()).isFalse();
        assertThat(foundDSs.size()).isEqualTo(1);
        var foundDS = foundDSs.get(0);
        assertThat(foundDS).isNotNull();
        assertThat(foundDS.getId()).isEqualTo(testDs.getId());
        assertThat(foundDS.getName()).isEqualTo(testDs.getName());
        assertThat(foundDS.getDescription()).isEqualTo(testDs.getDescription());
        assertThat(foundDS.getFields().size()).isEqualTo(testFields.size());
    }
}
