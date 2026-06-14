package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
    private final LabelRepository repository;

    /**
     * Возвращает список лейблов.
     *
     * @return список список лейблов
     */
    public List<Label> getLabels() {
        return repository.findAll();
    }

    /**
     * Возвращает лейбл по ID.
     *
     * @param id идентификатор лейбла
     * @return DTO лейбла
     * @throws ResourceNotFoundException если лейбл не найден
     */
    public Label getLabelById(Long id) {

        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("not found label by id: " + id));
    }

    /**
     * Создает новый лейбл.
     *
     * @param dto данные для создания лейбла
     * @return DTO созданной лейбла
     */
    @Transactional
    public Label createLabel(LabelDto dto) {
        Label label = new Label();
        label.setName(dto.getName());
        label.setCreatedAt(LocalDate.now());
        return repository.save(label);
    }

    /**
     * Обновляет существующий лейбл.
     *
     * @param id  идентификатор обновляемого лейбла
     * @param dto новые данные лейбла
     * @return DTO обновленного лейбла
     * @throws ResourceNotFoundException если лейбл не найден
     */
    @Transactional
    public Label updateLabel(Long id, LabelDto dto) {
        var fromDb = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("not found label by id: " + id));
        fromDb.setName(dto.getName());
        return repository.save(fromDb);
    }

    /**
     * Удаляет лейбл по ID.
     *
     * @param id идентификатор удаляемого лейбла
     */
    @Transactional
    public void deleteLabel(Long id) {
        repository.deleteById(id);
    }
}
