package hexlet.code.util;

import hexlet.code.dto.TaskParams;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {
    public static Specification<Task> filterTaskList(TaskParams params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (params.getLabelId() != null) {
                Join<Task, Label> labels = root.join("labels");
                predicates.add(cb.equal(
                        labels.get("id"),
                        params.getLabelId()));
                query.distinct(true);
            }
            if (params.getAssigneeId() != null) {
                predicates.add(cb.equal(
                        root.get("assignee").get("id"),
                        params.getAssigneeId()
                ));
            }
            if (params.getStatus() != null) {
                predicates.add(cb.equal(
                        root.get("taskStatus").get("slug"),
                        params.getStatus()
                ));
            }
            if (params.getTitleCont() != null) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + params.getTitleCont().toLowerCase() + "%"
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
