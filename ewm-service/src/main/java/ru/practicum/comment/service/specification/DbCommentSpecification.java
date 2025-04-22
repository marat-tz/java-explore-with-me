package ru.practicum.comment.service.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class DbCommentSpecification {

    public Specification<Comment> getSpecificationAdmin(List<Integer> users, List<Integer> events,
                                                        LocalDateTime rangeStart, LocalDateTime rangeEnd) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (users != null && !users.isEmpty()) {
                predicates.add(root.get("user").get("id").in(users));
            }

            if (events != null && !events.isEmpty()) {
                predicates.add(root.get("event").get("id").in(events));
            }

            if (rangeStart != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("created"), rangeStart));
            }

            if (rangeEnd != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("created"), rangeEnd));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }
}
