# Resolving Flyway and EntityManagerFactory Circular Dependency

## Problem Description

The application was encountering a circular dependency error between Flyway and EntityManagerFactory:

```
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'flyway' defined in class path resource [pl/programodawca/drivergear/config/DatabaseConfig.class]: Circular depends-on relationship between 'flyway' and 'entityManagerFactory'
```

This occurs because:
1. Flyway needs to run migrations before EntityManagerFactory is created
2. EntityManagerFactory depends on the database schema being ready
3. Spring detects a circular reference where each bean depends on the other

## Solution Implemented

### 1. Created a Custom DatabaseConfig Class

A new `DatabaseConfig` class was created to explicitly manage the initialization order:

```java
@Configuration
@EnableConfigurationProperties({FlywayProperties.class, JpaProperties.class, HibernateProperties.class})
public class DatabaseConfig {
    // Custom Flyway configuration
    @Primary
    @Bean(name = "flyway")
    public Flyway flyway(DataSource dataSource, FlywayProperties flywayProperties) {
        // Configure and run Flyway migrations explicitly
        // ...
    }

    // JPA and Hibernate beans with explicit dependency on Flyway
    @Bean
    @DependsOn("flyway")
    @Primary
    public JpaProperties jpaProperties(JpaProperties properties) {
        return properties;
    }

    @Bean
    @DependsOn("flyway")
    @Primary
    public HibernateProperties hibernateProperties(HibernateProperties properties) {
        return properties;
    }
}
```

### 2. Adjusted Hibernate Configuration

Changed Hibernate's schema generation strategy to be compatible with Flyway:

```properties
# Before
spring.jpa.hibernate.ddl-auto=update

# After
spring.jpa.hibernate.ddl-auto=validate
```

This ensures that:
- Flyway is responsible for all schema changes through migrations
- Hibernate only validates that the schema matches the entity definitions
- No conflicts occur between Hibernate's schema updates and Flyway migrations

## Why Flyway Is Necessary

Flyway is essential for this application because:

1. It manages specific schema modifications that Hibernate can't handle well, such as adding default values to columns
2. The project has a critical migration (`V1__add_default_value_to_compensation_amount.sql`) that fixes the "Field 'compensation_amount' doesn't have a default value" error
3. It provides a reliable way to track and apply database changes across environments

## Maintaining This Configuration

When making database schema changes:

1. **DO NOT** rely on Hibernate's auto-schema generation (it's set to "validate" only)
2. **DO** create new Flyway migration scripts in `src/main/resources/db/migration` following the naming convention `V{version}__{description}.sql`
3. **DO** increment the version number for each new migration
4. **DO** test migrations thoroughly before deploying to production

If you need to modify entity classes:
1. Create corresponding Flyway migration scripts for any schema changes
2. Ensure the entity definitions match the database schema after migrations

## References

- [Spring Boot Flyway Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)
- [Flyway Documentation](https://flywaydb.org/documentation/)