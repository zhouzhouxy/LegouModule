package com.pinyougou.service.impl;
import java.util.Date;
import java.util.List;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;
import com.pinyougou.seckill.service.SeckillOrderService;

import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult((int) page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult((int) page.getTotal(), page.getResult());
	}
		
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	
	@Autowired
	private IdWorker idWorker;

	@Override
	public void submitOrder(Long seckillId, String userId) {
		//从缓存中查询秒杀商品
		List<TbSeckillGoods> goods=(List<TbSeckillGoods>)redisTemplate.boundHashOps("seckillGoods").get(seckillId);
		TbSeckillGoods goods1 = goods.get(0);
		if(goods1==null){
			throw new RuntimeException("商品不存在");
		}
		if(goods1.getStockCount()<=0){
			throw new RuntimeException("商品已抢购一空");
		}
		//扣减(redis)库存
		goods1.setStockCount(goods1.getStockCount()-1);
		//放回缓存
		redisTemplate.boundHashOps("seckillGoods").put(seckillId,goods);
		if(goods1.getStockCount()==0){
			//同步到数据库中
			seckillGoodsMapper.updateByPrimaryKey(goods1);
			redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
		}
		//保存(redis)订单
		long orderId = idWorker.nextId();
		TbSeckillOrder seckillOrder = new TbSeckillOrder();
		seckillOrder.setId(orderId);
		seckillOrder.setCreateTime(new Date());
		//秒杀价格
		seckillOrder.setMoney(goods1.getCostPrice());
		seckillOrder.setSeckillId(seckillId);
		seckillOrder.setSellerId(goods1.getSellerId());
		//设置用户ID
		seckillOrder.setUserId(userId);
		//状态
		seckillOrder.setStatus("0");
		redisTemplate.boundHashOps("seckillOrder").put(userId,seckillOrder);
	}

	@Override
	public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
		
		return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
	}

	@Override
	public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
		
		//1.从缓存中提取订单数据
		TbSeckillOrder seckillOrder = searchOrderFromRedisByUserId(userId);
		if(seckillOrder==null){
			throw  new  RuntimeException("不存在订单");
		}
		if(seckillOrder.getId().longValue()!=orderId.longValue()){
			throw  new  RuntimeException("订单号不符");
		}
		
		//2.修改订单实体的属性
		seckillOrder.setPayTime(new Date());//支付日期
		seckillOrder.setStatus("1");//已支付 状态
		seckillOrder.setTransactionId(transactionId);
				
		//3.将订单存入数据库
		seckillOrderMapper.insert(seckillOrder);
		
		//4.清除缓存中的订单 
		redisTemplate.boundHashOps("seckillOrder").delete(userId);
		
	}

	@Override
	public void deleteOrderFromRedis(String userId, Long orderId) {
		
		//1.查询出缓存中的订单
		
		TbSeckillOrder seckillOrder = searchOrderFromRedisByUserId(userId);
		if(seckillOrder!=null){
			
			//2.删除缓存中的订单 
			redisTemplate.boundHashOps("seckillOrder").delete(userId);
						
			//3.库存回退
			TbSeckillGoods  seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
			if(seckillGoods!=null){ //如果不为空
				seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
				redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);
			}else{
				seckillGoods=new TbSeckillGoods();
				seckillGoods.setId(seckillOrder.getSeckillId());
				//属性要设置。。。。省略
				seckillGoods.setStockCount(1);//数量为1
				redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);
			}			
			
			System.out.println("订单取消："+orderId);
		}
		
		
		
		
	}
	
}
