package com.example.cqrs.app.domain;

import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface CustomRepository<T, I extends Serializable> extends JpaRepository<T, I>, JpaSpecificationExecutor<T>, QuerydslPredicateExecutor<T> {
    <P> Optional<P> findOne(@NonNull JPQLQuery<P> query);

    <P> List<P> findAll(@NonNull JPQLQuery<P> query);

    <P> List<P> findAll(@NonNull JPQLQuery<P> query, @NonNull Sort sort);

    <P> Page<P> findAll(@NonNull JPQLQuery<P> query, @NonNull Pageable pageable);

    <P> List<P> findAll(@NonNull FactoryExpression<P> factoryExpression, @NonNull Predicate predicate);

    <P> List<P> findAll(@NonNull FactoryExpression<P> factoryExpression, @NonNull Predicate predicate, @NonNull Sort sort);

    <P> Page<P> findAll(@NonNull FactoryExpression<P> factoryExpression, @NonNull Predicate predicate, @NonNull Pageable pageable);

}
