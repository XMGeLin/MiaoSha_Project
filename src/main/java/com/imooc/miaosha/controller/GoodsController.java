package com.imooc.miaosha.controller;

import java.util.List;

import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.vo.GoodsDetailVo;
import com.imooc.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.service.MiaoshaUserService;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;

	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;

	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;

	@Autowired
	ApplicationContext applicationContext;

	/**
	 * requestMapping中的produces的作用和使用方式：
	 *     它的作用是指定返回值类型和返回值编码。例如： application/json,text/html.
	 *     1、produce的第一种使用，返回json数据，可以省略produces属性，因为已经使用了注解@ResponseBody
	 *     就是返回的json数据。
	 *
	 *
	 */
    @RequestMapping(value = "/to_list",produces = "text/html")
	@ResponseBody
    public String list(HttpServletResponse response, HttpServletRequest request,Model model, MiaoshaUser user) {
    	model.addAttribute("user", user);
//    	List<GoodsVo> goodsList=goodsService.listGoodsVo();
//    	model.addAttribute("goodsList",goodsList);
//        return "goods_list";
		//取缓存
		String html=redisService.get(GoodsKey.getGoodsList,"",String.class);
		if(!StringUtils.isEmpty(html)){
			return html;
		}

		//查询商品列表,如果缓存为空，先去数据库查到数据库，然后使用thymeleafViewResolver进行
		//手动渲染，将渲染的结果放进html中，然后放进缓存，再返回给浏览器解析。

		//注意： 缓存的有效时间不宜过长，用户可以看到60前的页面。
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList",goodsList);

		SpringWebContext ctx=new SpringWebContext(request,response,request.getServletContext()
				,request.getLocale(),model.asMap(),applicationContext);
		//手动渲染
		html =thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
		if(!StringUtils.isEmpty(html)){
			redisService.set(GoodsKey.getGoodsList,"",html);
		}
		return html;
    }

    @ResponseBody   //页面存的是HTML，数据通过接口从服务端返回
	@RequestMapping("/detail/{goodsId}")
	public Result<GoodsDetailVo> detail(Model model, MiaoshaUser user
	          , @PathVariable("goodsId")long goodsId) {

		GoodsVo goods=goodsService.getGoodVoByGoodId(goodsId);

		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();

		int miaoshaStatus = 0;
		int remainSeconds = 0;
		if(now < startAt ) {//秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int)((startAt - now )/1000);
		}else  if(now > endAt){//秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		}else {//秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
		GoodsDetailVo vo=new GoodsDetailVo();
		vo.setGoods(goods);
		vo.setUser(user);
		vo.setRemainSeconds(remainSeconds);
		vo.setMiaoshaStatus(miaoshaStatus);
		return Result.success(vo);
	}


	@RequestMapping("/to_detail2/{goodsId}")
	public String detail2(Model model,MiaoshaUser user
			,@PathVariable("goodsId")long goodsId) {
		model.addAttribute("user", user);
		GoodsVo goods=goodsService.getGoodVoByGoodId(goodsId);
		model.addAttribute("goods",goods);
		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();

		int miaoshaStatus = 0;
		int remainSeconds = 0;
		if(now < startAt ) {//秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int)((startAt - now )/1000);
		}else  if(now > endAt){//秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		}else {//秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
		model.addAttribute("miaoshaStatus", miaoshaStatus);
		model.addAttribute("remainSeconds", remainSeconds);
		return "goods_detail";
	}
}
