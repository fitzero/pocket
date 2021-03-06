package org.hunter.pocket.criteria;

import org.hunter.pocket.config.DatabaseNodeConfig;
import org.hunter.pocket.utils.FieldTypeStrategy;
import org.hunter.pocket.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
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

    final Class clazz;
    final Connection connection;
    final DatabaseNodeConfig databaseConfig;

    final Field[] childrenFields;
    List<Restrictions> restrictionsList = new LinkedList<>();
    List<Restrictions> sortedRestrictionsList = new LinkedList<>();
    List<Modern> modernList = new LinkedList<>();
    List<Sort> orderList = new LinkedList<>();
    Map<String, Object> parameterMap = new HashMap<>();
    List<ParameterTranslator> parameters = new LinkedList<>();
    private Integer start;
    private Integer limit;
    StringBuilder completeSql = new StringBuilder();

    AbstractCriteria(Class clazz, Connection connection, DatabaseNodeConfig databaseConfig) {
        this.clazz = clazz;
        this.connection = connection;
        this.databaseConfig = databaseConfig;
        this.childrenFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(FIND_CHILDREN)
                .toArray(Field[]::new);
    }

    void before() {
        this.showSql();
    }

    void cleanAll() {
        this.cleanWithoutRestrictions();
        this.cleanRestrictions();
    }

    void cleanWithoutRestrictions() {
        modernList = new LinkedList<>();
        orderList = new LinkedList<>();
        parameterMap = new HashMap<>(16);
        parameters = new LinkedList<>();
        start = null;
        limit = null;
        completeSql = new StringBuilder();
    }

    void cleanRestrictions() {
        sortedRestrictionsList = new LinkedList<>();
        this.restrictionsList = new LinkedList<>();
    }

    private void showSql() {
        if (this.databaseConfig.getShowSql()) {
            this.logger.info("SQL: {}", this.completeSql);
        }
    }

    void setLimit(int start, int limit) {
        this.start = start;
        this.limit = limit;
    }

    boolean limited() {
        return this.start != null && this.limit != null;
    }

    Integer getStart() {
        return start;
    }

    Integer getLimit() {
        return limit;
    }
}
