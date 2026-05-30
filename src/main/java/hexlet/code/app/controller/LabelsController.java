package hexlet.code.app.controller;

import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.dto.LabelResponse;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public final class LabelsController {
    private final LabelService labelService;
    private final LabelMapper mapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<LabelResponse>> getLabels() {
        List<Label> labels = labelService.getLabels();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labels.stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponse getLabelById(@PathVariable Long id) {
        return mapper.toResponse(labelService.getLabelById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponse createLabel(@RequestBody @Valid LabelDto dto) {
        return mapper.toResponse(labelService.createLabel(dto));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelResponse updateLabel(@PathVariable Long id, @RequestBody @Valid LabelDto dto) {
        return mapper.toResponse(labelService.updateLabel(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLabel(@PathVariable Long id) {

        labelService.deleteLabel(id);
    }

}
