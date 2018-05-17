package com.silu.web.controller;

import java.io.IOException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.silu.web.entity.Main;
import com.silu.web.index.IndexEntity.IndexEntity;

@Controller
public class IndexController {
	private static Logger logger = Logger.getLogger(IndexController.class);
	@Autowired
	public RestTemplate temp;
	@Value("${app.encoding}")
	public String encoding;

	@RequestMapping(value = "/index")
	public String toIndex(Model model, HttpServletRequest req) {
		logger.debug("HelloWorld");
		String result = "";
		// 获取远端 数据
		result = temp.getForObject("http://provider-user/index/view", String.class);
		// 转换 为Java 对象
		IndexEntity entity = JSONObject.parseObject(result, IndexEntity.class);
		// 写入 Model
		model.addAttribute("index", entity);
		// model.addAttribute("host",req.getServletContext().getAttribute("host"));
		logger.info(req.getServletContext().getAttribute("host"));
		return "index";
	}

	@Autowired
	DiscoveryClient discoveryClient;

	// 201805 16 获取远端NODEjs 数据
	@RequestMapping(value = "/")
	public String toMian(Model model, HttpServletRequest req) throws JSONException {
		// 获取远端 数据 (NOde )
		String result = temp.postForObject("http://NODEJS-PROVIDER/main", null, String.class);
		Gson gson = new Gson();

		JSONArray jsons = new JSONArray(result);
		Main main = gson.fromJson(jsons.getJSONObject(0).toString(), Main.class);
		List<ServiceInstance> serviceInstances = discoveryClient.getInstances("NODEJS-PROVIDER");
		for (ServiceInstance serviceInstance : serviceInstances) {
			main.setM_bg_img(
					"http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/" + main.getM_bg_img());
			;
		}
		model.addAttribute("json", main);
		return "main";
	}

	@RequestMapping(value = "/settings")
	public String toSettings(Model model) {
		logger.debug("HelloWorld");
		// // 获取远端 数据
		// result = temp.getForObject("http://provider-user/index/view", String.class);
		// // 转换 为Java 对象
		// IndexEntity entity = JSONObject.parseObject(result, IndexEntity.class);
		// // 写入 Model
		// model.addAttribute("index", entity);
		return "settings";
	}

	@RequestMapping(value = "/infoService")
	public String toinfoService(Model model) {
		logger.debug("HelloWorld");
		// // 获取远端 数据
		// result = temp.getForObject("http://provider-user/index/view", String.class);
		// // 转换 为Java 对象
		// IndexEntity entity = JSONObject.parseObject(result, IndexEntity.class);
		// // 写入 Model
		// model.addAttribute("index", entity);
		return "infoService";
	}

	@RequestMapping(value = "/last")
	public String toLast(Model model) {
		logger.debug("HelloWorld");
		// String result = "";
		// // 获取远端 数据
		// result = temp.getForObject("http://provider-user/index/view", String.class);
		// // 转换 为Java 对象
		// IndexEntity entity = JSONObject.parseObject(result, IndexEntity.class);
		// // 写入 Model
		// model.addAttribute("index", entity);
		return "last";
	}

	// 导航条
	@RequestMapping(value = "/navigation")
	@ResponseBody
	public void navigation(HttpServletResponse res) throws IOException {
		logger.debug("HelloWorld");
		String result = "";
		// 获取远端 数据
		result = temp.getForObject("http://provider-user/getAllNavigations", String.class);
		// 写出去
		logger.debug(result);
		res.setCharacterEncoding("utf-8");
		res.setContentType("application/json");
		Writer w = res.getWriter();
		w.write(URLDecoder.decode(result, encoding));
		w.close();

	}
}
