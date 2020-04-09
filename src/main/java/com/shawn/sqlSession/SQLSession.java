package com.shawn.sqlSession;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface SQLSession {

    // 查询所有
    <E> List<E> selectList(String statementId, Object... params) throws IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, SQLException, InvocationTargetException, ClassNotFoundException;

    // 查询单个
    <T> T selectOne(String statementId, Object... params) throws IllegalAccessException, ClassNotFoundException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException;

    // 为Dao接口生成代理实现类
    <T> T getMapper(Class<?> mapperClass);

    int insert(String statementId, Object... params) throws Exception;

    int update(String statementId, Object... params) throws Exception;

    int delete(String statementId, Object... params) throws Exception;
}
