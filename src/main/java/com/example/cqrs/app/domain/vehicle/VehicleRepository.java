package com.example.cqrs.app.domain.vehicle;

import com.example.cqrs.persistence.CustomRepository;
import com.querydsl.core.types.Path;
import org.bitbucket.gt_tech.spring.data.querydsl.value.operators.ExpressionProviderFactory;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface VehicleRepository extends CustomRepository<Vehicle, Integer>, QuerydslBinderCustomizer<QVehicle> {

    //dynamic projection
    <T> Collection<T> findByBrandName(String brandName, Class<T> type);

    <T> Collection<T> findBy(Class<T> type);

    @Override
    default void customize(QuerydslBindings bindings, QVehicle root) {

        createBinding(bindings, root.id);
        createBinding(bindings, root.soundSystem);
        createBinding(bindings, root.brandName);
    }

    private void createBinding(QuerydslBindings bindings, Path rootPath) {
        bindings.bind(rootPath)
                .all((path, values) -> ExpressionProviderFactory.getPredicate(path, values));
    }

}
