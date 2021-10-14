package com.example.cqrs.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.Properties;

public abstract class AbstractJacksonClobMapper implements UserType, ParameterizedType, Serializable {
    private static final String CLASS_TYPE = "classType";
    private static final String VIEW_TYPE = "viewType";
    private static final long serialVersionUID = 123456789L;
    private Class<?> classType;
    private Class<?> viewType;

    @Override
    public void setParameterValues(Properties prop) {
        String classType = prop.getProperty(CLASS_TYPE);
        try {
            this.classType = ReflectHelper.classForName(classType, this.getClass());
        } catch (ClassNotFoundException e) {
            throw new HibernateException("ClassType not found : " + classType, e);
        }

        String viewType = prop.getProperty(VIEW_TYPE);
        try {
            this.viewType = Class.forName(viewType);
        } catch (ClassNotFoundException e) {
            throw new HibernateException("View Type not found : " + viewType, e);
        }

    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.CLOB};
    }

    @Override
    public Class returnedClass() {
        return this.classType;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        try {
            return Objects.equals(toJson(x), toJson(y));
        } catch (JsonProcessingException e) {
            throw new HibernateException(e);
        }
    }

    private String toJson(Object x) throws JsonProcessingException {
        return getObjectMapper().writerWithView(viewType).writeValueAsString(x);
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        try {
            return Objects.hashCode(toJson(o));
        } catch (JsonProcessingException e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException, SQLException {
        try {
            String rawXml = resultSet.getString(strings[0]);

            if (rawXml != null) {
                return getObjectMapper().readValue(rawXml, this.classType);
            }
        } catch (JsonProcessingException e) {
            throw new HibernateException(e);
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int i, SharedSessionContractImplementor sharedSessionContractImplementor) throws HibernateException, SQLException {
        if (o == null) {
            preparedStatement.setString(i, null);
        } else {
            try {
                preparedStatement.setString(i, toJson(o));
            } catch (JsonProcessingException e) {
                throw new HibernateException(e);
            }
        }
    }

    @Override
    public Object deepCopy(Object o) throws HibernateException {
        if (o != null) {
            try {
                String s = toJson(o);
                return getObjectMapper().readValue(s, classType);
            } catch (JsonProcessingException e) {
                throw new HibernateException(e);
            }
        }
        return null;
    }

    public abstract ObjectMapper getObjectMapper();

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        try {
            return toJson(o);
        } catch (JsonProcessingException e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public Object assemble(Serializable serializable, Object owner) throws HibernateException {
        return this.deepCopy(serializable);
    }

    @Override
    public Object replace(Object original, Object o1, Object o2) throws HibernateException {
        return this.deepCopy(original);
    }
}
