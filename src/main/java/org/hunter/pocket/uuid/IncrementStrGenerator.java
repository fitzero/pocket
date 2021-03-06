package org.hunter.pocket.uuid;

import org.hunter.pocket.model.MapperFactory;
import org.hunter.pocket.session.Session;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * @author wujianchuan 2019/1/2
 */
@Component
public class IncrementStrGenerator extends AbstractUuidGenerator {
    private final static UuidGenerator INSTANCE = new IncrementStrGenerator();

    public static UuidGenerator getInstance() {
        return INSTANCE;
    }

    private IncrementStrGenerator() {
    }

    @Override
    public void setGeneratorId() {
        this.generatorId = "str_increment";
    }

    @Override
    public synchronized Serializable getUuid(Class clazz, Session session) throws SQLException {
        int tableId = MapperFactory.getTableId(clazz.getName());
        String mapKey = this.serverId + "_" + tableId;
        String leaderNum = "" + serverId + tableId;
        Long tailNumber = POOL.get(mapKey);
        if (tailNumber != null) {
            tailNumber++;
        } else {
            long maxUuid = session.getMaxUuid(serverId, clazz);
            if (maxUuid == 0 || !String.valueOf(maxUuid).startsWith(leaderNum)) {
                tailNumber = 0L;
            } else {
                tailNumber = Long.parseLong(String.valueOf(maxUuid).substring(leaderNum.length())) + 1;
            }
        }
        POOL.put(mapKey, tailNumber);
        return "" + leaderNum + tailNumber;
    }
}
