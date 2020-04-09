package com.shawn.sqlSession;

import com.shawn.pojo.Configuration;
import com.shawn.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface Executor {

    <E> List<E> query(MappedStatement mappedStatement, Object... params) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException;

    int update(MappedStatement mappedStatement, Object... params) throws SQLException, Exception;
}
