package dev.kukukodes.kdap.dataBoxService.controllers;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.model.ResponseModel;
import dev.kukukodes.kdap.dataBoxService.service.DataBoxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * Endpoints:
 * <p>
 * GET '/'
 * - Get all databoxes (Admin only).
 * <p>
 * GET '/?user={userId}'
 * - Get all databoxes for the specified user.
 * - Accessing another user's databoxes is forbidden unless the requester is an Admin.
 * <p>
 * GET '/{databoxId}'
 * - Get details of the specified databox by its ID.
 * - Accessing another user's databox is forbidden unless the requester is an Admin.
 * <p>
 * POST '/{userId}'
 * - Payload: {@link DataBox}
 * - Add a new databox for the specified user.
 * - Creating databoxes for other users is forbidden unless the requester is an Admin.
 * <p>
 * DELETE '/{databoxId}'
 * - Delete the specified databox by its ID.
 * - Deletion of another user's databox is forbidden unless the requester is an Admin.
 * <p>
 * UPDATE '/'
 * - Payload: {@link DataBox}
 * - Update an existing databox.
 * - Updating another user's databox is forbidden unless the requester is an Admin.
 */
@Slf4j
@RestController
@RequestMapping("/api/authenticated/databox")
public class DataboxController {

    private final DataBoxService dataBoxService;

    public DataboxController(DataBoxService dataBoxService) {
        this.dataBoxService = dataBoxService;
    }

    /**
     * Get All databoxes
     */
    @GetMapping("/")
    public ResponseEntity<ResponseModel<List<DataBox>>> getDataBoxes() {
        log.warn("Getting all databoxes. ADD LIMIT IN FUTURE");
        try {
            return ResponseModel.success("Success", dataBoxService.getAllDatabox());
        } catch (AccessDeniedException e) {
            return ResponseModel.buildResponse(e.getMessage(), null, 500);
        }
    }
}
