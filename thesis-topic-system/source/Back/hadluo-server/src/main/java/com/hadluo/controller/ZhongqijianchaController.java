package com.hadluo.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hadluo.annotation.IgnoreAuth;
import com.hadluo.entity.XuantishenqingEntity;
import com.hadluo.entity.ZhongqijianchaEntity;
import com.hadluo.entity.view.ZhongqijianchaView;
import com.hadluo.service.XuantishenqingService;
import com.hadluo.service.ZhongqijianchaService;
import com.hadluo.utils.MPUtil;
import com.hadluo.utils.TopicDeletedMarkerUtil;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.R;
import com.hadluo.utils.XuantishenqingStatusUtil;

@RestController
@RequestMapping("/zhongqijiancha")
@SuppressWarnings({"unchecked", "rawtypes"})
public class ZhongqijianchaController {
	@Autowired
	private ZhongqijianchaService zhongqijianchaService;
	@Autowired
	private XuantishenqingService xuantishenqingService;
	@Autowired
	private TopicDeletedMarkerUtil topicDeletedMarkerUtil;

	@RequestMapping("/page")
	public R page(@RequestParam Map<String, Object> params, ZhongqijianchaEntity zhongqijiancha,
			HttpServletRequest request) {
		Object tableNameObj = request.getSession().getAttribute("tableName");
		if (tableNameObj != null) {
			String tableName = tableNameObj.toString();
			if ("jiaoshi".equals(tableName)) {
				zhongqijiancha.setJiaoshigonghao((String) request.getSession().getAttribute("username"));
			}
			if ("xuesheng".equals(tableName)) {
				zhongqijiancha.setXuehao((String) request.getSession().getAttribute("username"));
			}
		}
		EntityWrapper<ZhongqijianchaEntity> ew = new EntityWrapper<ZhongqijianchaEntity>();
		PageUtils page = zhongqijianchaService.queryPage(params,
				MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, zhongqijiancha), params), params));
		topicDeletedMarkerUtil.markPage(page, request);
		return R.ok().put("data", page);
	}

	@IgnoreAuth
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params, ZhongqijianchaEntity zhongqijiancha,
			HttpServletRequest request) {
		EntityWrapper<ZhongqijianchaEntity> ew = new EntityWrapper<ZhongqijianchaEntity>();
		PageUtils page = zhongqijianchaService.queryPage(params,
				MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, zhongqijiancha), params), params));
		return R.ok().put("data", page);
	}

	@RequestMapping("/lists")
	public R lists(ZhongqijianchaEntity zhongqijiancha) {
		EntityWrapper<ZhongqijianchaEntity> ew = new EntityWrapper<ZhongqijianchaEntity>();
		ew.allEq(MPUtil.allEQMapPre(zhongqijiancha, "zhongqijiancha"));
		return R.ok().put("data", zhongqijianchaService.selectListView(ew));
	}

	@RequestMapping("/query")
	public R query(ZhongqijianchaEntity zhongqijiancha) {
		EntityWrapper<ZhongqijianchaEntity> ew = new EntityWrapper<ZhongqijianchaEntity>();
		ew.allEq(MPUtil.allEQMapPre(zhongqijiancha, "zhongqijiancha"));
		ZhongqijianchaView view = zhongqijianchaService.selectView(ew);
		return R.ok("查询中期检查成功").put("data", view);
	}

	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		ZhongqijianchaEntity entity = zhongqijianchaService.selectById(id);
		entity = zhongqijianchaService.selectView(new EntityWrapper<ZhongqijianchaEntity>().eq("id", id));
		return R.ok().put("data", entity);
	}

	@IgnoreAuth
	@RequestMapping("/detail/{id}")
	public R detail(@PathVariable("id") Long id) {
		ZhongqijianchaEntity entity = zhongqijianchaService.selectById(id);
		entity = zhongqijianchaService.selectView(new EntityWrapper<ZhongqijianchaEntity>().eq("id", id));
		return R.ok().put("data", entity);
	}

	@RequestMapping("/save")
	public R save(@RequestBody ZhongqijianchaEntity zhongqijiancha, HttpServletRequest request) {
		zhongqijiancha.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
		com.hadluo.utils.WorkflowAuditUtil.applyOnInsert(zhongqijiancha.getShenhezhuangtai(), zhongqijiancha::setShenhezhuangtai, request);
		zhongqijianchaService.insert(zhongqijiancha);
		return R.ok();
	}

	@RequestMapping("/add")
	public R add(@RequestBody ZhongqijianchaEntity zhongqijiancha, HttpServletRequest request) {
		Object tableNameObj = request.getSession().getAttribute("tableName");
		if (tableNameObj != null && "xuesheng".equals(tableNameObj.toString())) {
			String xuehao = (String) request.getSession().getAttribute("username");
			if (StringUtils.isBlank(xuehao)) {
				return R.error("未获取到学生学号，请重新登录后再试");
			}
			EntityWrapper<XuantishenqingEntity> ew = new EntityWrapper<XuantishenqingEntity>();
			ew.eq("xuehao", xuehao).in("shenhezhuangtai", XuantishenqingStatusUtil.getAppliedStatuses())
					.orderBy("shenqingshijian", false);
			List<XuantishenqingEntity> list = xuantishenqingService.selectList(ew);
			if (list == null || list.isEmpty()) {
				return R.error("您还没有审核通过的选题，无法提交中期检查");
			}
			XuantishenqingEntity selected = list.get(0);
			zhongqijiancha.setTimubianhao(selected.getTimubianhao());
			zhongqijiancha.setKetimingcheng(selected.getKetimingcheng());
			zhongqijiancha.setTimuleixing(selected.getTimuleixing());
			zhongqijiancha.setZhuanye(selected.getZhuanye());
			zhongqijiancha.setKetixingzhi(selected.getKetixingzhi());
			zhongqijiancha.setJiaoshigonghao(selected.getJiaoshigonghao());
			zhongqijiancha.setJiaoshixingming(selected.getJiaoshixingming());
			zhongqijiancha.setXuehao(selected.getXuehao());
			zhongqijiancha.setXueshengxingming(selected.getXueshengxingming());
		}
		com.hadluo.utils.WorkflowAuditUtil.applyOnInsert(zhongqijiancha.getShenhezhuangtai(), zhongqijiancha::setShenhezhuangtai, request);
		zhongqijiancha.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
		zhongqijianchaService.insert(zhongqijiancha);
		return R.ok();
	}

	@RequestMapping("/update")
	@Transactional
	public R update(@RequestBody ZhongqijianchaEntity zhongqijiancha, HttpServletRequest request) {
		com.hadluo.utils.WorkflowAuditUtil.clearAuditReasonOnStudentResubmit(request, zhongqijiancha::setShenheyuanyin);
		com.hadluo.utils.WorkflowAuditUtil.applyOnUpdate(zhongqijiancha.getShenhezhuangtai(), zhongqijiancha::setShenhezhuangtai, request);
		zhongqijianchaService.updateById(zhongqijiancha);
		return R.ok();
	}

	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids) {
		zhongqijianchaService.deleteBatchIds(Arrays.asList(ids));
		return R.ok();
	}
}
