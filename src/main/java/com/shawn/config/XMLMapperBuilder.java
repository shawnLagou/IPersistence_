package com.shawn.config;

import com.shawn.pojo.Configuration;
import com.shawn.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream resourceAsStream) throws DocumentException {

        Document document = new SAXReader().read(resourceAsStream);
        Element rootElement = document.getRootElement();
        String nameSpace = rootElement.attributeValue("namespace");


        List<Element> list = rootElement.selectNodes("//select|insert|delete|update");
        for (Element element : list) {
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String paramterType = element.attributeValue("paramterType");
            String sqlText = element.getTextTrim();
            String key = nameSpace + "." + id;
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            mappedStatement.setParameterType(paramterType);
            mappedStatement.setResultType(resultType);
            mappedStatement.setSql(sqlText);
            configuration.getMappedStatementMap().put(key, mappedStatement);
        }

    }
}
