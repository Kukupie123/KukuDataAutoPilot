package dev.kukukodes.kdap.dataBoxService.controllers;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.model.KDAPAuthenticatedUser;
import dev.kukukodes.kdap.dataBoxService.repo.impl.DataBoxRepoMongo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/authenticated")
public class AuthenticatedController {

    private final DataBoxRepoMongo dataBoxRepo;

    public AuthenticatedController(DataBoxRepoMongo dataBoxRepo) {
        this.dataBoxRepo = dataBoxRepo;
    }

    @GetMapping("/databox")
    public ResponseEntity<List<DataBox>> getAllDataBoxes() {
        KDAPAuthenticatedUser user = KDAPAuthenticatedUser.GetSecurityUser();
        log.info("getting all data boxes for user : {}", user);
        return ResponseEntity.ok(dataBoxRepo.getDataStoresByUserID(user.getUser().getId()));
    }
}
