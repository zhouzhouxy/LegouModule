package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Goods implements Serializable {
    //商品SPU
    private TbGoods goods;
    //商品扩展
    private TbGoodsDesc goodsDesc;
    //商品SKU列表
    private List<TbItem> itemList;

}
