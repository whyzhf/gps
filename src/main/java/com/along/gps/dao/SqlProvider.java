package com.along.gps.dao;


import com.along.gps.entity.OutboundRoadlog;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;


/**
 * @Auther: why
 * @Data:2019/5/24
 * @Deacription:
 */
public class SqlProvider extends SQL {

    //保存gps数据
    public String saveGpsData(Map map){
        List<OutboundRoadlog> dataList = (List<OutboundRoadlog>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO outbound_roadlog " +
                "(route_id, equipment_id, `type`, longitude, latitude, lot, lat, speed, direction, form, uptime,task_id) VALUES ");
        MessageFormat mf = new MessageFormat(
                "( #'{'list[{0}].routeId}, #'{'list[{0}].equipmentId}, #'{'list[{0}].type}, #'{'list[{0}].longitude}," +
                        "#'{'list[{0}].latitude}, #'{'list[{0}].lot}, #'{'list[{0}].lat}, #'{'list[{0}].speed}, #'{'list[{0}].direction}, #'{'list[{0}].form}, #'{'list[{0}].uptime}, #'{'list[{0}].taskId} )"
        );
        for (int i = 0; i < dataList.size(); i++) {
            sb.append(mf.format(new Object[] {i}));
            if (i < dataList.size() - 1)
                sb.append(",");
        }
        return sb.toString();
    }


}
