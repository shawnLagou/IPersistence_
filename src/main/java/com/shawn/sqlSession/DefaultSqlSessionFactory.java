package com.shawn.sqlSession;

import com.shawn.pojo.Configuration;

public class DefaultSqlSessionFactory implements SQLSessionFactory {

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    public SQLSession openSQLSession() {
        return new DefaultSqlSession(configuration);
    }
}
