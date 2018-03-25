/*
 * MROZA - supporting system of behavioral therapy of people with autism
 *     Copyright (C) 2015-2016 autyzm-pg
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mroza.dao;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@ApplicationScoped
public class SqlSessionFactoryProvider {

    @Produces
    @ApplicationScoped
    public SqlSessionFactory produceFactory() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream reader = Resources.getResourceAsStream(resource);
        Properties dbConnectionProps = new Properties();
        Map<String, String> environmentVars =  System.getenv();
        dbConnectionProps.setProperty("DRIVER", environmentVars.getOrDefault("DRIVER", "org.postgresql.Driver"));
        dbConnectionProps.setProperty("URL", environmentVars.getOrDefault("URL", "jdbc:postgresql://localhost:5432/mrozadb"));
        dbConnectionProps.setProperty("USER", environmentVars.getOrDefault("USER", "mroza"));
        dbConnectionProps.setProperty("PASSWORD", environmentVars.getOrDefault("PASSWORD", "123456"));
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader, dbConnectionProps);
        return sqlSessionFactory;
    }
}
