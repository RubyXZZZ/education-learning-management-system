package io.rubyxzzz.lms.backend.controller;

import io.rubyxzzz.lms.backend.dto.request.CreateModuleReq;
import io.rubyxzzz.lms.backend.dto.request.UpdateModuleReq;
import io.rubyxzzz.lms.backend.dto.response.ModuleRes;
import io.rubyxzzz.lms.backend.service.ModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;

    /**
     * Create a new module
     * POST /api/modules
     */
    @PreAuthorize("hasAuthority('MODULES_CREATE')")
    @PostMapping
    public ResponseEntity<ModuleRes> createModule(
            @Valid @RequestBody CreateModuleReq request
    ) {
        ModuleRes response = moduleService.createModule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get module by ID
     * GET /api/modules/{id}
     */
    @PreAuthorize("hasAnyAuthority('MODULES_VIEW_ALL', 'MODULES_VIEW_PUBLISHED')")
    @GetMapping("/{id}")
    public ResponseEntity<ModuleRes> getModule(@PathVariable String id) {
        ModuleRes response = moduleService.getModule(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update module
     * PUT /api/modules/{id}
     */
    @PreAuthorize("hasAuthority('MODULES_EDIT')")
    @PutMapping("/{id}")
    public ResponseEntity<ModuleRes> updateModule(
            @PathVariable String id,
            @Valid @RequestBody UpdateModuleReq request
    ) {
        ModuleRes response = moduleService.updateModule(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete module
     * DELETE /api/modules/{id}
     */
    @PreAuthorize("hasAuthority('MODULES_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(@PathVariable String id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get modules by section (instructor view - all modules)
     * GET /api/sections/{sectionId}/modules
     */
    @PreAuthorize("hasAuthority('MODULES_VIEW_ALL')")
    @GetMapping("/sections/{sectionId}")
    public ResponseEntity<List<ModuleRes>> getModulesBySection(
            @PathVariable String sectionId
    ) {
        List<ModuleRes> response = moduleService.getModulesBySection(sectionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get published modules by section (student view)
     * GET /api/sections/{sectionId}/modules/published
     */
    @PreAuthorize("hasAuthority('MODULES_VIEW_PUBLISHED')")
    @GetMapping("/sections/{sectionId}/published")
    public ResponseEntity<List<ModuleRes>> getPublishedModulesBySection(
            @PathVariable String sectionId
    ) {
        List<ModuleRes> response = moduleService.getPublishedModulesBySection(sectionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Publish module
     * POST /api/modules/{id}/publish
     */
    @PreAuthorize("hasAuthority('MODULES_EDIT')")
    @PostMapping("/{id}/publish")
    public ResponseEntity<ModuleRes> publishModule(@PathVariable String id) {
        ModuleRes response = moduleService.publishModule(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Unpublish module
     * POST /api/modules/{id}/unpublish
     */
    @PreAuthorize("hasAuthority('MODULES_EDIT')")
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<ModuleRes> unpublishModule(@PathVariable String id) {
        ModuleRes response = moduleService.unpublishModule(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Reorder module
     * PUT /api/modules/{id}/reorder
     * Body: { "orderNum": 3 }
     */
    // TODO: REORDER FEATURE
    @PreAuthorize("hasAuthority('MODULES_EDIT')")
    @PutMapping("/{id}/reorder")
    public ResponseEntity<ModuleRes> reorderModule(
            @PathVariable String id,
            @RequestParam Integer orderNum
    ) {
        ModuleRes response = moduleService.reorderModule(id, orderNum);
        return ResponseEntity.ok(response);
    }
}
