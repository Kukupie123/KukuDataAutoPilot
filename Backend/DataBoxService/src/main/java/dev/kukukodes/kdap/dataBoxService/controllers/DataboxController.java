package dev.kukukodes.kdap.dataBoxService.controllers;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.model.ResponseModel;
import dev.kukukodes.kdap.dataBoxService.service.DataBoxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/authenticated/databox")
public class DataboxController {

    private final DataBoxService dataBoxService;

    public DataboxController(DataBoxService dataBoxService) {
        this.dataBoxService = dataBoxService;
    }

    /**
     * Get All databoxes or of specific user
     */
    @GetMapping("/")
    public ResponseEntity<ResponseModel<List<DataBox>>> getDataBoxes(@RequestParam(value = "user", required = false) String userID) {
        if (userID == null) {
            log.warn("Getting all databoxes. ADD LIMIT IN FUTURE");
            try {
                return ResponseModel.success("Success", dataBoxService.getAllDatabox());
            } catch (AccessDeniedException e) {
                return ResponseModel.buildResponse(e.getMessage(), null, 500);
            }
        }
        log.info("Getting databoxes of user {}", userID);
        try {
            var dbs = dataBoxService.getDataboxOfUser(userID);
            return ResponseModel.success("Success", dbs);
        } catch (Exception e) {
            return ResponseModel.buildResponse(e.getMessage(), null, 500);
        }
    }

    @PostMapping("/")
    public ResponseEntity<ResponseModel<DataBox>> createDataBox(@RequestBody DataBox dataBox) {
        log.info("Creating data box {}", dataBox);
        if (dataBox.getFields().isEmpty()) {
            return ResponseModel.buildResponse("No fields specified", null, 500);
        }
    }
}
