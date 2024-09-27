package dev.kukukodes.kdap.dataBoxService.repo.impl;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.enums.DataBoxFieldType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataMongoTest
class DataBoxRepoMongoTest {

    private DataBoxRepoMongo repo;
    private DataBox testDataBox;
    private List<DataBoxField> testFields;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        repo = new DataBoxRepoMongo(mongoTemplate);
        testFields = new ArrayList<>();
        testFields.add(new DataBoxField("name", DataBoxFieldType.TEXT, true));
        testFields.add(new DataBoxField("age", DataBoxFieldType.INTEGER_NUMBER, true));
        testDataBox = new DataBox("testUser", "testName", "desc", testFields);
    }

    @AfterEach
    void tearDown() {
        testFields.clear();
        testDataBox = null;
        mongoTemplate.remove(new Query(), DataBox.class); // Removes all records
    }

    @Test
    void addDataStore() {
        var addedUser = repo.addDataStore(testDataBox);
        assertThat(addedUser).isNotNull();
        assertThat(addedUser.getId()).isNotNull().isEqualTo(testDataBox.getId());
        assertThat(addedUser.getName()).isNotNull().isEqualTo(testDataBox.getName());
        assertThat(addedUser.getDescription()).isNotNull().isEqualTo(testDataBox.getDescription());
        assertThat(addedUser.getFields().size()).isEqualTo(testFields.size());
    }

    @Test
    void updateDataStore() {
        var updatedUser = repo.addDataStore(testDataBox);
        updatedUser.setName("updatedName");
        var updated = repo.updateDataStore(updatedUser);
        assertThat(updated).isNotNull().isEqualTo(true);

    }

    @Test
    void getDataBoxByID() {
        var addedUser = repo.addDataStore(testDataBox);
        var foundDS = repo.getDataBoxByID(testDataBox.getId());
        assertThat(foundDS).isNotNull();
        assertThat(foundDS.getId()).isEqualTo(testDataBox.getId());
        assertThat(foundDS.getName()).isEqualTo(testDataBox.getName());
        assertThat(foundDS.getDescription()).isEqualTo(testDataBox.getDescription());
        assertThat(foundDS.getFields().size()).isEqualTo(testFields.size());
    }

    @Test
    void getDataStoresByUserID() {
        var addedUser = repo.addDataStore(testDataBox);
        var foundDSs = repo.getDataStoresByUserID(testDataBox.getUserID());
        assertThat(foundDSs).isNotNull();
        assertThat(foundDSs.isEmpty()).isFalse();
        assertThat(foundDSs.size()).isEqualTo(1);
        var foundDS = foundDSs.get(0);
        assertThat(foundDS).isNotNull();
        assertThat(foundDS.getId()).isEqualTo(testDataBox.getId());
        assertThat(foundDS.getName()).isEqualTo(testDataBox.getName());
        assertThat(foundDS.getDescription()).isEqualTo(testDataBox.getDescription());
        assertThat(foundDS.getFields().size()).isEqualTo(testFields.size());
    }
}
