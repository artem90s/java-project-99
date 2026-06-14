package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;

import java.util.List;

public interface LabelService {
    List<Label> getLabels();

    Label getLabelById(Long id);

    Label createLabel(LabelDto dto);

    Label updateLabel(Long id, LabelDto dto);

    void deleteLabel(Long id);
}

