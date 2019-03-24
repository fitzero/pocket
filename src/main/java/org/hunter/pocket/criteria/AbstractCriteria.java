package org.hunter.pocket.criteria;

import org.hunter.pocket.annotation.Entity;
import org.hunter.pocket.config.DatabaseNodeConfig;
import org.hunter.pocket.utils.FieldTypeStrategy;
import org.hunter.pocket.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hunter.pocket.utils.ReflectUtils.FIND_CHILDREN;

/**
 * @author wujianchuan 2019/1/10
 */
abstract class AbstractCriteria {

    private final Logger logger = LoggerFactory.getLogger(AbstractCriteria.class);
    final ReflectUtils reflectUtils = ReflectUtils.getInstance();
    final FieldTypeStrategy fieldTypeStrategy = FieldTypeStrategy.getInstance();
    final SqlFactory sqlFactory = SqlFactory.getInstance();

    final Class clazz;
    final Connection connection;
    final DatabaseNodeConfig databaseConfig;
    final String tableName;

    final Field[] fields;
    final Field[] childrenFields;
    final Map<String, FieldMapper> fieldMapper;
    final List<Restrictions> restrictionsList = new ArrayList<>();
    final List<Modern> modernList = new ArrayList<>();
    final List<Sort> orderList = new ArrayList<>();
    StringBuilder completeSql = new StringBuilder();
    StringBuilder sqlRestriction = new StringBuilder();

    AbstractCriteria(Class clazz, Connection connection, DatabaseNodeConfig databaseConfig) {
        this.clazz = clazz;
        this.connection = connection;
        this.databaseConfig = databaseConfig;
        this.fields = reflectUtils.getMappingField(clazz);
        this.childrenFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(FIND_CHILDREN)
                .toArray(Field[]::new);
        this.fieldMapper = reflectUtils.getFieldMapperMap(clazz);
        this.tableName = ((Entity) this.clazz.getAnnotation(Entity.class)).table();
    }

    void before() {
        this.showSql();
    }

    void after() {
        this.clear();
    }

    private void showSql() {
        if (this.databaseConfig.getShowSql()) {
            this.logger.info("SQL: {}", this.completeSql);
        }
    }

    private void clear() {
        this.completeSql = new StringBuilder();
        this.sqlRestriction = new StringBuilder();
        this.modernList.clear();
        this.restrictionsList.clear();
    }
}
