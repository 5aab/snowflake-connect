package com.example.cqrs.app.domain;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.*;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class CustomRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements CustomRepository<T, ID> {

    private final EntityPath<T> path;
    private final PathBuilder<T> builder;
    private final Querydsl querydsl;
    private final CustomQueryDslPredicateExecutor<T> executor;

    public CustomRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.path = SimpleEntityPathResolver.INSTANCE.createPath(entityInformation.getJavaType());
        this.builder = new PathBuilder<T>(this.path.getType(), this.path.getMetadata());
        this.querydsl = new Querydsl(entityManager, this.builder);
        this.executor = new CustomQueryDslPredicateExecutor<>(entityInformation, entityManager, SimpleEntityPathResolver.INSTANCE, null);
    }

    @Override
    public <P> Optional<P> findOne(JPQLQuery<P> query) {
        return Optional.ofNullable(query.fetchFirst());
    }

    @Override
    public <P> List<P> findAll(JPQLQuery<P> query) {
        return query.fetch();
    }

    @Override
    public <P> List<P> findAll(JPQLQuery<P> query, Sort sort) {
        return findAll(query, sort);
    }

    @Override
    public <P> Page<P> findAll(JPQLQuery<P> query, Pageable pageable) {
        return findAll(query, pageable);
    }

    @Override
    public <P> List<P> findAll(FactoryExpression<P> factoryExpression, Predicate predicate) {
        JPQLQuery<P> query= createQuery(factoryExpression, predicate);
        return findAll(query);
    }

    @Override
    public <P> List<P> findAll(FactoryExpression<P> factoryExpression, Predicate predicate, Sort sort) {
        JPQLQuery<P> query= createQuery(factoryExpression, predicate);
        return findAll(query, sort);
    }

    @Override
    public <P> Page<P> findAll(FactoryExpression<P> factoryExpression, Predicate predicate, Pageable pageable) {
        JPQLQuery<P> query= createQuery(factoryExpression, predicate);
        JPQLQuery<?> countQuery = executor.createCountQuery(predicate);
        return getPage(query, countQuery, pageable);
    }

    private <P> JPQLQuery<P> createQuery(FactoryExpression<P> factoryExpression, Predicate predicate) {
        return executor.createQuery(predicate).select(factoryExpression);
    }

    @Override
    public Optional<T> findOne(Predicate predicate) {
        return executor.findOne(predicate);
    }

    @Override
    public Iterable<T> findAll(Predicate predicate) {
        return executor.findAll(predicate);
    }

    @Override
    public Iterable<T> findAll(Predicate predicate, Sort sort) {
        return executor.findAll(predicate, sort);
    }

    @Override
    public Iterable<T> findAll(Predicate predicate, OrderSpecifier<?>... orders) {
        return executor.findAll(predicate, orders);
    }

    @Override
    public Iterable<T> findAll(OrderSpecifier<?>... orders) {
        return executor.findAll(orders);
    }

    @Override
    public Page<T> findAll(Predicate predicate, Pageable pageable) {
        return executor.findAll(predicate,pageable);
    }

    @Override
    public long count(Predicate predicate) {
        return executor.count(predicate);
    }

    @Override
    public boolean exists(Predicate predicate) {
        return executor.exists(predicate);
    }


    private static class CustomQueryDslPredicateExecutor<T> extends QuerydslJpaPredicateExecutor<T> {
        CustomQueryDslPredicateExecutor(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager, EntityPathResolver resolver, CrudMethodMetadata metadata) {
            super(entityInformation, entityManager, resolver, metadata);
        }

        @NonNull
        public JPQLQuery<?> createCountQuery(Predicate predicate){
            return super.createCountQuery(predicate);
        }

        @NonNull
        public JPQLQuery<?> createQuery(Predicate predicate){
            return super.createQuery(predicate);
        }
    }

    private <P> Page<P> getPage(JPQLQuery<P> query, JPQLQuery<?> countQuery, Pageable pageable) {
        JPQLQuery<P> paginatedQuery = getPaginatedQuery(query, pageable);
        return PageableExecutionUtils.getPage(paginatedQuery.fetch(), pageable, countQuery::fetchCount);
    }

    private <P> JPQLQuery<P> getPaginatedQuery(JPQLQuery<P> query, Pageable pageable) {
        return querydsl.applyPagination(pageable, query);
    }

    public <P> List<P> executeSorted(JPQLQuery<P> query, Sort sort) {
        return querydsl.applySorting(sort, query).fetch();
    }
}
