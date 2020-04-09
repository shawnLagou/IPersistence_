package com.shawn.sqlSession;

import com.shawn.pojo.Configuration;
import com.shawn.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SQLSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    public <E> List<E> selectList(String statementId, Object... params) throws IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, SQLException, InvocationTargetException, ClassNotFoundException {

        // 将要完成对simpleExecutor里的query方法调用
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        Executor executor = new SimpleExecutor(configuration);
        List<Object> list = executor.query(mappedStatement, params);
        return (List<E>) list;
    }

    public <T> T selectOne(String statementId, Object... params) throws IllegalAccessException, ClassNotFoundException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException {
        List<Object> objects = selectList(statementId, params);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else {
            throw new RuntimeException("返回结果不唯一");
        }
    }


    @Override
    public int insert(String statementId, Object... params) throws Exception {
        return this.update(statementId, params);
    }

    @Override
    public int delete(String statementId, Object... params) throws Exception {
        return this.update(statementId, params);
    }

    @Override
    public int update(String statementId, Object... params) throws Exception {
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        Executor executor = new SimpleExecutor(configuration);
        return executor.update(mappedStatement, params);
    }

    public <T> T getMapper(Class<?> mapperClass) {
        // 使用JDK动态代理来为Dao接口生成代理对象并返回

        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 底层还是执行JDBC代码，根据不同情况来调用selectList或者selectOne
                // 准备参数 1：statementID：SQL语句的唯一标识：namespace.id = 接口全限定名.方法
                // 方法名：findAll
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + methodName;

                // 准备参数 2：params:args
                // 获取被调用方法的返回值类型
                Type genericReturnType = method.getGenericReturnType();

                // 判断是否进行了泛型类型参数化
                if (genericReturnType instanceof ParameterizedType) {
                    List<Object> objects = selectList(statementId, args);
                    return objects;
                }
                if (genericReturnType.getTypeName().equals("int")) {
                    return insert(statementId, args);
                }
                return selectOne(statementId, args);
            }
        });
        return (T) proxyInstance;
    }
}
