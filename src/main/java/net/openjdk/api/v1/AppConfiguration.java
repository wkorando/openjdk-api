package net.openjdk.api.v1;

import net.openjdk.api.datasource.DataSourceInterface;
import net.openjdk.api.datasource.DataStub;
import net.openjdk.api.datasource.JDKNet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfiguration {

    @Value("${application.datasource.implementation}")
    private String dataSourceClass;

    @Bean
    public DataSourceInterface dataSource() throws Exception {
        var impl = Class.forName(dataSourceClass);
        System.out.println(dataSourceClass);
        if (impl.isAssignableFrom(DataStub.class)) {
            System.out.println("data stub provisioned");
            return new DataStub();
        }
        if (impl.isAssignableFrom(JDKNet.class)) {
            var jdkXML = new JDKNet();
            System.out.println("jdk.java.net instance provisioned");
            return jdkXML;
        }

        throw new Exception("Datasource implementation not found");
    }

}
