package com.test.solr;

import com.test.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
public class TestTemplate {

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void testAdd(){
        TbItem tbItem = new TbItem();
        tbItem.setId(1L);
        tbItem.setBrand("华为");
        tbItem.setCategory("手机");
        tbItem.setGoodsId(1L);
        tbItem.setSeller("华为2号专卖店");
        tbItem.setTitle("华为Mate9");
        tbItem.setPrice(new BigDecimal(2000));
        solrTemplate.saveBean(tbItem);
        solrTemplate.commit();
    }

    @Test
    public void testFindOne(){
        TbItem byId = solrTemplate.getById(1, TbItem.class);
        System.out.println(byId);
    }

    /**
     *
     *添加一百条数据
     */
    @Test
    public void testAddList(){
        List<TbItem> list = new ArrayList<>();
        for(int i=2;i<100;i++){
            TbItem tbItem = new TbItem();
            tbItem.setId((long) i);
            tbItem.setBrand("小米");
            tbItem.setCategory("手机");
            tbItem.setGoodsId(1L);
            tbItem.setSeller("小米"+i+"号专卖店");
            tbItem.setTitle("小米"+i);
            tbItem.setPrice(new BigDecimal(2000));

            list.add(tbItem);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     *
     * 分页查询
     */
    @Test
    public void testPageQuery(){
        //查询条件
        Query query = new SimpleQuery("*:*");
        //开始索引(默认0)
        query.setOffset(20);
        //每页记录数(默认10)
        query.setRows(20);

        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数"+tbItems.getTotalElements());
        List<TbItem> content = tbItems.getContent();

        showList(content);
    }

    //显示记录数据
    private void showList(List<TbItem> content) {
        for(TbItem item:content){
            System.out.println(item);
        }
    }

    /**
     * 条件查询
     */
    @Test
    public void testPageQueryMutil(){
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_title").contains("2");
        criteria=criteria.and("item_title").contains("5");
        query.addCriteria(criteria);

        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总页数"+page.getTotalElements());
        List<TbItem> list = page.getContent();
        showList(list);
    }

    /**
     *
     * 删除全部数据
     */

    @Test
    public void testDeleteAll(){
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
