package com.hadluo.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hadluo.dao.TimuleixingDao;
import com.hadluo.entity.TimuleixingEntity;
import com.hadluo.entity.TokenEntity;
import com.hadluo.entity.UsersEntity;
import com.hadluo.entity.XueshengEntity;
import com.hadluo.entity.view.TimuleixingView;
import com.hadluo.entity.vo.TimuleixingMergedItem;
import com.hadluo.entity.vo.TopicTypeScope;
import com.hadluo.service.TimuleixingService;
import com.hadluo.service.TokenService;
import com.hadluo.service.UsersService;
import com.hadluo.service.XueshengService;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.Query;

@Service("timuleixingService")
public class TimuleixingServiceImpl extends ServiceImpl<TimuleixingDao, TimuleixingEntity> implements TimuleixingService {

    @Autowired
    private UsersService usersService;
    @Autowired
    private XueshengService xueshengService;
    @Autowired
    private TokenService tokenService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<TimuleixingEntity> page = this.selectPage(
                new Query<TimuleixingEntity>(params).getPage(),
                new EntityWrapper<TimuleixingEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<TimuleixingEntity> wrapper) {
        Page<TimuleixingView> page = new Query<TimuleixingView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        return new PageUtils(page);
    }

    @Override
    public List<TimuleixingView> selectListView(Wrapper<TimuleixingEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public TimuleixingView selectView(Wrapper<TimuleixingEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }

    @Override
    public PageUtils queryTopicTypePage(Map<String, Object> params, String keyword, TopicTypeScope scope) {
        List<TimuleixingMergedItem> items = buildTopicTypeItems(keyword, scope);
        int pageNum = parseIntParam(params.get("page"), 1);
        int pageSize = parseIntParam(params.get("limit"), 10);
        int total = items.size();
        int fromIndex = Math.max(0, (pageNum - 1) * pageSize);
        int toIndex = Math.min(total, fromIndex + pageSize);
        List<TimuleixingMergedItem> pageList = fromIndex >= total
                ? Collections.emptyList()
                : items.subList(fromIndex, toIndex);
        return new PageUtils(pageList, total, pageSize, pageNum);
    }

    @Override
    public List<String> listTypeNames(TopicTypeScope scope) {
        List<TimuleixingMergedItem> items = buildTopicTypeItems(null, scope);
        List<String> names = new ArrayList<>();
        for (TimuleixingMergedItem item : items) {
            if (item != null && StringUtils.isNotBlank(item.getTimuleixing())) {
                names.add(item.getTimuleixing());
            }
        }
        return names;
    }

    @Override
    public List<String> listTypeNames(HttpServletRequest request) {
        return listTypeNames(resolveScope(request));
    }

    @Override
    public TopicTypeScope resolveScope(HttpServletRequest request) {
        TopicTypeScope scope = new TopicTypeScope();
        if (request == null) {
            return scope;
        }

        String token = request.getHeader("Token");
        if (StringUtils.isNotBlank(token)) {
            TokenEntity tokenEntity = tokenService.getTokenEntity(token);
            if (tokenEntity != null && "xuesheng".equals(tokenEntity.getTablename())) {
                String xuehao = tokenEntity.getUsername();
                if (StringUtils.isNotBlank(xuehao)) {
                    XueshengEntity student = xueshengService.selectOne(
                            new EntityWrapper<XueshengEntity>().eq("xuehao", xuehao));
                    if (student != null && StringUtils.isNotBlank(student.getZhuanye())) {
                        scope.setZhuanye(student.getZhuanye().trim());
                    }
                }
                return scope;
            }
        }

        String tableName = String.valueOf(request.getSession().getAttribute("tableName"));
        if ("jiaoshi".equals(tableName)) {
            String teacherNo = (String) request.getSession().getAttribute("username");
            if (StringUtils.isNotBlank(teacherNo)) {
                scope.setJiaoshigonghao(teacherNo.trim());
            }
            return scope;
        }
        if ("users".equals(tableName)) {
            Object userIdObj = request.getSession().getAttribute("userId");
            if (userIdObj instanceof Long) {
                UsersEntity admin = usersService.selectById((Long) userIdObj);
                if (admin != null && StringUtils.isNotBlank(admin.getZhuanye())) {
                    scope.setZhuanye(admin.getZhuanye().trim());
                }
            }
        }
        return scope;
    }

    private List<TimuleixingMergedItem> buildTopicTypeItems(String keyword, TopicTypeScope scope) {
        String jiaoshigonghao = scope != null ? StringUtils.trimToNull(scope.getJiaoshigonghao()) : null;
        String zhuanye = scope != null ? StringUtils.trimToNull(scope.getZhuanye()) : null;
        List<Map<String, Object>> topicStats = baseMapper.selectTopicTypeStats(jiaoshigonghao, zhuanye);

        String search = StringUtils.trimToEmpty(keyword);
        List<TimuleixingMergedItem> items = new ArrayList<>();
        if (topicStats == null) {
            return items;
        }
        for (Map<String, Object> stat : topicStats) {
            if (stat == null) {
                continue;
            }
            String name = extractName(stat);
            if (name.isEmpty()) {
                continue;
            }
            if (StringUtils.isNotBlank(search) && !StringUtils.contains(name, search)) {
                continue;
            }
            TimuleixingMergedItem item = new TimuleixingMergedItem();
            item.setTimuleixing(name);
            item.setTopicCount(extractCount(stat));
            item.setSource("topic");
            item.setId((long) name.hashCode());
            items.add(item);
        }

        items.sort(Comparator
                .comparing(TimuleixingMergedItem::getTopicCount, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(TimuleixingMergedItem::getTimuleixing, Comparator.nullsLast(String::compareTo)));
        return items;
    }

    private static String extractName(Map<String, Object> stat) {
        Object nameObj = stat.get("timuleixing");
        if (nameObj == null) {
            nameObj = stat.get("TIMULEIXING");
        }
        return nameObj == null ? "" : String.valueOf(nameObj).trim();
    }

    private static int extractCount(Map<String, Object> stat) {
        int count = toInt(stat.get("topicCount"));
        if (count == 0) {
            count = toInt(stat.get("TOPICCOUNT"));
        }
        return count;
    }

    private static int parseIntParam(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private static int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
