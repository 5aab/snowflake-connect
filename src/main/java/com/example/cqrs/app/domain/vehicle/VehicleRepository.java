package com.example.cqrs.app.domain.vehicle;

import com.example.cqrs.event.QDomainAggregateRoot;
import com.example.cqrs.persistence.CustomRepository;
import com.querydsl.core.types.Path;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.ExpressionProviderFactory;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public interface VehicleRepository extends CustomRepository<Vehicle, Integer>, QuerydslBinderCustomizer<QVehicle> {

    //dynamic projection
    <T> Collection<T> findByBrandName(String brandName, Class<T> type);

    <T> Collection<T> findBy(Class<T> type);

    @Override
    default void customize(QuerydslBindings bindings, QVehicle root) {
        Field[] declaredFields = root.getClass().getDeclaredFields();
        List<Path> paths = Arrays.stream(declaredFields).map(f -> {
                    try {
                        return (Path) f.get(root);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).filter(Objects::nonNull)
                .filter(p -> !(p.getClass().equals(QDomainAggregateRoot.class) || p.getClass().equals(root.getClass())))
                .collect(Collectors.toList());

        paths.forEach(rootPath -> createBinding(bindings, rootPath));

    }

    private void createBinding(QuerydslBindings bindings, Path rootPath) {
        bindings.bind(rootPath)
                .all((path, values) -> ExpressionProviderFactory.getPredicate(path, values));
    }

}
