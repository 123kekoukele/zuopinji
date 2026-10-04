package com.hadluo.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import com.hadluo.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hadluo.annotation.IgnoreAuth;

import com.hadluo.entity.XuantishenqingEntity;
import com.hadluo.entity.TimuxinxiEntity;
import com.hadluo.entity.view.XuantishenqingView;

import com.hadluo.service.XuantishenqingService;
import com.hadluo.service.TimuxinxiService;
import com.hadluo.service.XueshengService;
import com.hadluo.service.TokenService;
import com.hadluo.service.StudentWorkflowCleanupService;
import com.hadluo.entity.TokenEntity;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.R;
import com.hadluo.utils.MPUtil;
import com.hadluo.utils.CommonUtil;
import com.hadluo.utils.XuantishenqingStatusUtil;
import com.hadluo.utils.TopicDeletedMarkerUtil;
import java.io.IOException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 选题申请
 * 后端接口
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
@RestController
@RequestMapping("/xuantishenqing")
public class XuantishenqingController {
    @Autowired
    private XuantishenqingService xuantishenqingService;
    @Autowired
    private TimuxinxiService timuxinxiService;

    @Autowired
    private XueshengService xueshengService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private StudentWorkflowCleanupService studentWorkflowCleanupService;

    @Autowired
    private TopicDeletedMarkerUtil topicDeletedMarkerUtil;


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,XuantishenqingEntity xuantishenqing,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("jiaoshi")) {
			xuantishenqing.setJiaoshigonghao((String)request.getSession().getAttribute("username"));
		}
		if(tableName.equals("xuesheng")) {
			xuantishenqing.setXuehao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<XuantishenqingEntity> ew = new EntityWrapper<XuantishenqingEntity>();
        applyApplicationStatusFilter(ew, params, xuantishenqing);

		PageUtils page = xuantishenqingService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, xuantishenqing), params), params));
		topicDeletedMarkerUtil.markPage(page, request);

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,XuantishenqingEntity xuantishenqing, 
		HttpServletRequest request){
        EntityWrapper<XuantishenqingEntity> ew = new EntityWrapper<XuantishenqingEntity>();

        // 此接口标了 @IgnoreAuth，需根据 Token 为已登录学生/教师过滤数据
        String token = request.getHeader("Token");
        if (StringUtils.isNotBlank(token)) {
            TokenEntity tokenEntity = tokenService.getTokenEntity(token);
            if (tokenEntity != null) {
                if ("xuesheng".equals(tokenEntity.getTablename())) {
                    xuantishenqing.setXuehao(tokenEntity.getUsername());
                } else if ("jiaoshi".equals(tokenEntity.getTablename())) {
                    xuantishenqing.setJiaoshigonghao(tokenEntity.getUsername());
                }
            }
        }
        applyApplicationStatusFilter(ew, params, xuantishenqing);

		PageUtils page = xuantishenqingService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, xuantishenqing), params), params));
		topicDeletedMarkerUtil.markPage(page, request);
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( XuantishenqingEntity xuantishenqing){
       	EntityWrapper<XuantishenqingEntity> ew = new EntityWrapper<XuantishenqingEntity>();
      	ew.allEq(MPUtil.allEQMapPre( xuantishenqing, "xuantishenqing")); 
        return R.ok().put("data", xuantishenqingService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(XuantishenqingEntity xuantishenqing){
        EntityWrapper< XuantishenqingEntity> ew = new EntityWrapper< XuantishenqingEntity>();
 		ew.allEq(MPUtil.allEQMapPre( xuantishenqing, "xuantishenqing")); 
		XuantishenqingView xuantishenqingView =  xuantishenqingService.selectView(ew);
		return R.ok("查询选题申请成功").put("data", xuantishenqingView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        XuantishenqingEntity xuantishenqing = xuantishenqingService.selectById(id);
		xuantishenqing = xuantishenqingService.selectView(new EntityWrapper<XuantishenqingEntity>().eq("id", id));
        return R.ok().put("data", xuantishenqing);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        XuantishenqingEntity xuantishenqing = xuantishenqingService.selectById(id);
		xuantishenqing = xuantishenqingService.selectView(new EntityWrapper<XuantishenqingEntity>().eq("id", id));
        return R.ok().put("data", xuantishenqing);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody XuantishenqingEntity xuantishenqing, HttpServletRequest request){
        R operateError = assertTeacherCanOperate(request);
        if (operateError != null) {
            return operateError;
        }
    	xuantishenqing.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(xuantishenqing);
        xuantishenqingService.insert(xuantishenqing);
        return R.ok();
    }
    
    /**
     * 前端保存（学生申请选题）
     * 使用事务 + 题目行锁（FOR UPDATE）保证并发下同一题目只会被一人申请成功，数据原子性。
     */
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/add")
    public R add(@RequestBody XuantishenqingEntity xuantishenqing, HttpServletRequest request){
	// 仅允许学生账号申请选题（管理员/教师等即使调用接口也拒绝）
	Object tableNameObj = request.getSession().getAttribute("tableName");
	if (tableNameObj == null || !"xuesheng".equals(String.valueOf(tableNameObj))) {
		return R.error("仅学生可以申请选题");
	}
	String xuehao = (String) request.getSession().getAttribute("username");
	if (xuehao == null || xuehao.trim().isEmpty()) {
		return R.error("无法获取学生学号，请重新登录");
	}
	// 学生端：每个学生同一时间只能对一个课题发起有效申请；
        // 同一个课题，同一时间只能有一条有效的选题申请；并发时通过锁定题目行保证原子性。
                String timubianhao = xuantishenqing.getTimubianhao() != null ? xuantishenqing.getTimubianhao().trim() : "";
                if (!"".equals(timubianhao)) {
                    // 先对题目行加锁，再校验+插入，保证“检查并占用”在同一事务内原子完成
                    timuxinxiService.selectByTimubianhaoForUpdate(timubianhao);
                }
                TimuxinxiEntity topic = timuxinxiService.selectOne(new EntityWrapper<TimuxinxiEntity>().eq("timubianhao", timubianhao));
                if (topic == null) {
                    return R.error("题目不存在，无法申请");
                }
                com.hadluo.entity.XueshengEntity stu = xueshengService.selectOne(new EntityWrapper<com.hadluo.entity.XueshengEntity>().eq("xuehao", xuehao));
                if (stu == null) {
                    return R.error("学生信息不存在，请重新登录");
                }
                String stuMajor = stu.getZhuanye() == null ? "" : stu.getZhuanye().trim();
                String topicMajor = topic.getZhuanye() == null ? "" : topic.getZhuanye().trim();
                if (stuMajor.isEmpty() || topicMajor.isEmpty() || !stuMajor.equals(topicMajor)) {
                    return R.error("仅可申请与本人专业一致的教师题目");
                }
                // 1）当前学生是否已有“有效”的选题申请（排除被拒绝的）
                EntityWrapper<XuantishenqingEntity> stuWrapper = new EntityWrapper<XuantishenqingEntity>();
                stuWrapper.eq("xuehao", xuehao)
                          .in("shenhezhuangtai", XuantishenqingStatusUtil.getActiveStatuses());
                int stuCount = xuantishenqingService.selectCount(stuWrapper);
                if (stuCount > 0) {
                    return R.error("每个学生只能选择一个课题，您已有其他选题申请记录");
                }
                // 2）当前课题是否已被其他学生申请或锁定（在锁内再次检查，防止并发）
                if (!"".equals(timubianhao)) {
                    EntityWrapper<XuantishenqingEntity> topicWrapper = new EntityWrapper<XuantishenqingEntity>();
                    topicWrapper.eq("timubianhao", timubianhao)
                                .in("shenhezhuangtai", XuantishenqingStatusUtil.getActiveStatuses());
                    int topicCount = xuantishenqingService.selectCount(topicWrapper);
                    if (topicCount > 0) {
                        return R.error("该题目已被其他学生申请或锁定，不能重复申请");
                    }
                }
	xuantishenqing.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        xuantishenqing.setTimubianhao(timubianhao);
        xuantishenqing.setXuehao(xuehao);
        xuantishenqing.setXueshengxingming(stu.getXueshengxingming());
        xuantishenqing.setZhuanye(topicMajor);
        xuantishenqing.setJiaoshigonghao(topic.getJiaoshigonghao());
        xuantishenqing.setJiaoshixingming(topic.getJiaoshixingming());
        xuantishenqing.setKetimingcheng(topic.getKetimingcheng());
        xuantishenqing.setTimuleixing(topic.getTimuleixing());
        xuantishenqing.setKetixingzhi(topic.getKetixingzhi());
        if (topic.getXuantishijian() != null && !topic.getXuantishijian().trim().isEmpty()) {
            xuantishenqing.setXuantishijian(topic.getXuantishijian());
        }
        xuantishenqing.setShenqingshijian(new Date());
        if (xuantishenqing.getShenhezhuangtai() == null || xuantishenqing.getShenhezhuangtai().trim().isEmpty()) {
            xuantishenqing.setShenhezhuangtai(XuantishenqingStatusUtil.STATUS_PENDING);
        }
        xuantishenqing.setShenhejilu(buildSubmitAuditLog(xuantishenqing.getShenqingshijian()));
    	//ValidatorUtils.validateEntity(xuantishenqing);
        xuantishenqingService.insert(xuantishenqing);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody XuantishenqingEntity xuantishenqing, HttpServletRequest request){
        XuantishenqingEntity dbEntity = xuantishenqingService.selectById(xuantishenqing.getId());
        R operateError = assertTeacherOwnsApplication(request, dbEntity);
        if (operateError != null) {
            return operateError;
        }

        // 当审核状态从非“已审核”改为“已审核”时，需要保证：
        // 1）同一学生不会有多个已审核的选题（在 /add 已控制）
        // 2）同一题目不会被多个学生同时选中（同一 timubianhao 只能有一条已审核记录）

        if (dbEntity != null) {
            String oldStatus = dbEntity.getShenhezhuangtai();
            String newStatus = xuantishenqing.getShenhezhuangtai();
            if (XuantishenqingStatusUtil.isAppliedStatus(newStatus) && !XuantishenqingStatusUtil.isAppliedStatus(oldStatus)) {
                // 检查是否已有其他学生对同一题目审核通过
                EntityWrapper<XuantishenqingEntity> ew = new EntityWrapper<XuantishenqingEntity>();
                ew.eq("timubianhao", dbEntity.getTimubianhao())
                  .in("shenhezhuangtai", XuantishenqingStatusUtil.getAppliedStatuses())
                  .ne("id", dbEntity.getId());
                int count = xuantishenqingService.selectCount(ew);
                if (count > 0) {
                    return R.error("该题目已经有其他学生审核通过，不能再重复审核通过此申请");
                }
            }
            String auditLog = dbEntity.getShenhejilu();
            if (StringUtils.isBlank(auditLog) && dbEntity.getShenqingshijian() != null) {
                auditLog = buildSubmitAuditLog(dbEntity.getShenqingshijian());
            }
            if (newStatus != null && !newStatus.equals(oldStatus)) {
                Date auditTime = new Date();
                xuantishenqing.setShenheshijian(auditTime);
                if (XuantishenqingStatusUtil.isAppliedStatus(newStatus) && !XuantishenqingStatusUtil.isAppliedStatus(oldStatus)) {
                    String reason = extractPassReason(xuantishenqing.getShenqingyuanyin());
                    auditLog = appendAuditRecord(auditLog, "pass", "导师确认申请", reason, auditTime);
                } else if (XuantishenqingStatusUtil.isRejectedStatus(newStatus) && !XuantishenqingStatusUtil.isRejectedStatus(oldStatus)) {
                    String reason = extractRejectReason(xuantishenqing.getShenqingyuanyin());
                    if (StringUtils.isBlank(reason) && XuantishenqingStatusUtil.isAppliedStatus(oldStatus)) {
                        reason = "导师撤销已通过申请";
                    }
                    auditLog = appendAuditRecord(auditLog, "reject", "导师驳回申请", reason, auditTime);
                    if (XuantishenqingStatusUtil.isAppliedStatus(oldStatus) && StringUtils.isNotBlank(dbEntity.getXuehao())) {
                        studentWorkflowCleanupService.clearByXuehao(dbEntity.getXuehao());
                    }
                }
                xuantishenqing.setShenhejilu(auditLog);
            } else if (xuantishenqing.getShenhejilu() == null) {
                xuantishenqing.setShenhejilu(auditLog);
            }
            // 已通过申请被驳回后，状态变为“否”，题目与学生选题占用会在后续查询中自动释放
        }
        //ValidatorUtils.validateEntity(xuantishenqing);
        xuantishenqingService.updateById(xuantishenqing);//全部更新
        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request){
        if (ids == null || ids.length == 0) {
            return R.error("请选择要删除的记录");
        }

        R operateError = assertTeacherCanOperate(request);
        if (operateError != null) {
            return operateError;
        }

        String jiaoshigonghao = (String) request.getSession().getAttribute("username");
        for (Long id : ids) {
            XuantishenqingEntity entity = xuantishenqingService.selectById(id);
            if (entity == null) {
                continue;
            }
            if (!jiaoshigonghao.equals(entity.getJiaoshigonghao())) {
                return R.error("只能删除属于当前教师的选题申请");
            }
        }

        xuantishenqingService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 教师查看某个题目被哪些同学选了（包含已审核/未审核/不通过）
     * 接口会校验该题目属于当前教师
     *
     * 返回示例：
     * data: XuantishenqingEntity[]（按申请时间倒序）
     * total: 总申请数
     * approvedCount: 已审核通过数
     */
    @RequestMapping("/studentsByTopic")
    public R studentsByTopic(String timubianhao, HttpServletRequest request) {
        if (timubianhao == null || timubianhao.trim().isEmpty()) {
            return R.error("题目编号不能为空");
        }
        String topicCode = timubianhao.trim();

        Object tableNameObj = request.getSession().getAttribute("tableName");
        String tableName = tableNameObj != null ? String.valueOf(tableNameObj) : "";
        if (!"jiaoshi".equals(tableName)) {
            return R.error("仅教师可查看");
        }

        String jiaoshigonghao = (String) request.getSession().getAttribute("username");
        if (jiaoshigonghao == null || jiaoshigonghao.trim().isEmpty()) {
            return R.error("教师身份信息缺失");
        }

        // 1）校验题目属于当前教师
        TimuxinxiEntity topic = timuxinxiService.selectOne(
                new EntityWrapper<TimuxinxiEntity>()
                        .eq("timubianhao", topicCode)
                        .eq("jiaoshigonghao", jiaoshigonghao)
        );
        if (topic == null) {
            return R.error("该题目不存在或不属于当前教师");
        }

        // 2）查询该题目的所有选题申请记录
        List<XuantishenqingEntity> list = xuantishenqingService.selectList(
                new EntityWrapper<XuantishenqingEntity>()
                        .eq("timubianhao", topicCode)
                        .orderBy("shenqingshijian", false)
        );

        int approvedCount = 0;
        if (list != null) {
            for (XuantishenqingEntity x : list) {
                if (x != null && XuantishenqingStatusUtil.isAppliedStatus(x.getShenhezhuangtai())) {
                    approvedCount++;
                }
            }
        } else {
            list = new ArrayList<>();
        }

        return R.ok()
                .put("data", list)
                .put("total", list.size())
                .put("approvedCount", approvedCount);
    }

    /**
     * 管理端审核状态筛选：未审核 / 通过 / 驳回
     */
    private void applyApplicationStatusFilter(EntityWrapper<XuantishenqingEntity> ew, Map<String, Object> params, XuantishenqingEntity xuantishenqing) {
        if (params == null || params.get("shenhezhuangtai") == null) {
            return;
        }
        String status = String.valueOf(params.get("shenhezhuangtai")).trim();
        if (!XuantishenqingStatusUtil.isValidFilterStatus(status)) {
            return;
        }
        xuantishenqing.setShenhezhuangtai(null);
        params.remove("shenhezhuangtai");
        if (XuantishenqingStatusUtil.STATUS_PENDING.equals(status)) {
            ew.in("shenhezhuangtai", XuantishenqingStatusUtil.getPendingStatuses());
        } else if (XuantishenqingStatusUtil.STATUS_APPROVED.equals(status)) {
            ew.in("shenhezhuangtai", XuantishenqingStatusUtil.getAppliedStatuses());
        } else if (XuantishenqingStatusUtil.STATUS_REJECTED.equals(status)) {
            ew.in("shenhezhuangtai", XuantishenqingStatusUtil.getRejectedStatuses());
        }
    }

    private static final SimpleDateFormat AUDIT_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static String formatAuditTime(Date time) {
        return time == null ? AUDIT_TIME_FORMAT.format(new Date()) : AUDIT_TIME_FORMAT.format(time);
    }

    private static JSONArray parseAuditLog(String log) {
        if (StringUtils.isBlank(log)) {
            return new JSONArray();
        }
        try {
            return JSONArray.parseArray(log);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private static String buildSubmitAuditLog(Date submitTime) {
        JSONObject submit = new JSONObject();
        submit.put("time", formatAuditTime(submitTime));
        submit.put("action", "submit");
        submit.put("text", "学生提交选题申请");
        JSONArray arr = new JSONArray();
        arr.add(submit);
        return arr.toJSONString();
    }

    private static String appendAuditRecord(String existingLog, String action, String text, String reason, Date time) {
        JSONArray arr = parseAuditLog(existingLog);
        JSONObject item = new JSONObject();
        item.put("time", formatAuditTime(time));
        item.put("action", action);
        item.put("text", text);
        if (StringUtils.isNotBlank(reason)) {
            item.put("reason", reason.trim());
        }
        arr.add(item);
        return arr.toJSONString();
    }

    private static String extractRejectReason(String shenqingyuanyin) {
        if (StringUtils.isBlank(shenqingyuanyin)) {
            return "";
        }
        String marker = "[驳回原因]:";
        int index = shenqingyuanyin.lastIndexOf(marker);
        if (index < 0) {
            return "";
        }
        return shenqingyuanyin.substring(index + marker.length()).trim();
    }

    private static String extractPassReason(String shenqingyuanyin) {
        if (StringUtils.isBlank(shenqingyuanyin)) {
            return "";
        }
        String marker = "[通过原因]:";
        int index = shenqingyuanyin.lastIndexOf(marker);
        if (index < 0) {
            return "";
        }
        return shenqingyuanyin.substring(index + marker.length()).trim();
    }

    private R assertTeacherCanOperate(HttpServletRequest request) {
        Object tableNameObj = request.getSession().getAttribute("tableName");
        String tableName = tableNameObj != null ? String.valueOf(tableNameObj) : "";
        if (!"jiaoshi".equals(tableName)) {
            return R.error("仅教师可以操作选题申请");
        }
        String jiaoshigonghao = (String) request.getSession().getAttribute("username");
        if (jiaoshigonghao == null || jiaoshigonghao.trim().isEmpty()) {
            return R.error("教师身份信息缺失");
        }
        return null;
    }

    private R assertTeacherOwnsApplication(HttpServletRequest request, XuantishenqingEntity entity) {
        R operateError = assertTeacherCanOperate(request);
        if (operateError != null) {
            return operateError;
        }
        if (entity == null) {
            return R.error("记录不存在");
        }
        String jiaoshigonghao = (String) request.getSession().getAttribute("username");
        if (!jiaoshigonghao.equals(entity.getJiaoshigonghao())) {
            return R.error("只能操作属于当前教师的选题申请");
        }
        return null;
    }
}
