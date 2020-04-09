package com.shawn.sqlSession;

import com.shawn.config.XMLConfigBuilder;
import com.shawn.pojo.Configuration;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

public class SqlSessionFactoryBuilder {


    public SQLSessionFactory builder(InputStream inputStream) throws PropertyVetoException, DocumentException {
        // 第一：使用dom4j解析配置文件，将解析出来的内容封装到Configuration中
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parseConfig(inputStream);
        // 第二：创建SQLSessionFactory对象
        DefaultSqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        return defaultSqlSessionFactory;
    }
}
