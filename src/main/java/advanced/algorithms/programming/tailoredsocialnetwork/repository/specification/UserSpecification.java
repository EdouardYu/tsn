package advanced.algorithms.programming.tailoredsocialnetwork.repository.specification;

import advanced.algorithms.programming.tailoredsocialnetwork.dto.SearchCriteria;
import advanced.algorithms.programming.tailoredsocialnetwork.entity.User;
import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@AllArgsConstructor
public class UserSpecification implements Specification<User> {
    private final SearchCriteria criteria;

    @Override
    public Predicate toPredicate
        (@Nonnull Root<User> root, @Nonnull CriteriaQuery<?> query, @Nonnull CriteriaBuilder builder) {
        if(criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
                root.get(criteria.getKey()), criteria.getValue().toString());
        } else if(criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
                root.get(criteria.getKey()), criteria.getValue().toString());
        } else if(criteria.getOperation().equalsIgnoreCase(":")) {
            if(root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                    root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        } else if(criteria.getOperation().equalsIgnoreCase("!:")) {
            if(root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.not(
                    builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%"));
            } else {
                return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
