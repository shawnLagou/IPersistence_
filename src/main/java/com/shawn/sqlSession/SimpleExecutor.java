package com.shawn.sqlSession;

import com.shawn.config.BoundSql;
import com.shawn.pojo.Configuration;
import com.shawn.pojo.MappedStatement;
import com.shawn.utils.GenericTokenParser;
import com.shawn.utils.ParameterMapping;
import com.shawn.utils.ParameterMappingTokenHandler;
import com.shawn.utils.TokenHandler;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor implements Executor{

    private Configuration configuration;



    public SimpleExecutor(Configuration configuration) {
        this.configuration = configuration;
    }

    public <E> List<E> query(MappedStatement mappedStatement, Object... params) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException {

        // 1、注册驱动、获取连接
        Connection connection = configuration.getDataSource().getConnection();

        // 2、获取SQL语句
        String sql = mappedStatement.getSql();
        // 转换SQL语句并赋值
        BoundSql boundSql = getBoundSql(sql);

        // 3、获取预处理对象：PreparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        // 4、设置参数
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        // 获取到参数的全路径
        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass = getClassType(parameterType);

        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();

            // 反射获取属性
            Field declaredField = parameterTypeClass.getDeclaredField(content);
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);

            preparedStatement.setObject(i + 1, o);
        }

        // 5、执行SQL
        ResultSet resultSet = preparedStatement.executeQuery();
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);

        List<Object> objects = new ArrayList<Object>();

        // 6、封装返回结果集
        while (resultSet.next()) {
            Object o = resultTypeClass.newInstance();
            // 元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
                // 字段名
                String columnName = metaData.getColumnName(i);
                // 字段的值
                Object value = resultSet.getObject(columnName);

                // 使用反射或者内省，根据数据库表和实体的对应关系完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o, value);
            }
            objects.add(o);
        }
        return (List<E>) objects;
    }

    @Override
    public int update(MappedStatement mappedStatement, Object... params) throws Exception {
        // 1、注册驱动、获取连接
        Connection connection = configuration.getDataSource().getConnection();

        // 2、获取SQL语句
        String sql = mappedStatement.getSql();
        // 转换SQL语句并赋值
        BoundSql boundSql = getBoundSql(sql);

        // 3、获取预处理对象：PreparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        // 4、设置参数
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        // 获取到参数的全路径
        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass = getClassType(parameterType);

        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();

            // 反射获取属性
            Field declaredField = parameterTypeClass.getDeclaredField(content);
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);

            preparedStatement.setObject(i + 1, o);
        }

        // 5、执行SQL
        int result = preparedStatement.executeUpdate();
        connection.close();
        return result;

    }

    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if (parameterType != null) {
            Class<?> aClass = Class.forName(parameterType);
            return aClass;
        }
        return null;
    }

    /**
     * 完成对#{}的解析工作：1、将#{}使用?进行代替 2、解析出#{}里面的值进行存储
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        //标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        // 解析出来的SQL
        String parseSql = genericTokenParser.parse(sql);
        // #{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql, parameterMappings);
        return boundSql;
    }
}
