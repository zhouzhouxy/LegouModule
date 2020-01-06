//控制层
app.controller('goodsController' ,function($scope,$controller,$location,
					goodsService,uploadService,itemCatService,typeTemplateService){

	$controller('baseController',{$scope:$scope});//继承

	//读取列表数据绑定到表单中
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}
		);
	}

	//分页
	$scope.findPage=function(page,rows){
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	}

	//查询实体
	$scope.findOne=function(){
		var id=$location.search()['id'];//获取参数值
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//向富文本编辑器添加商品介绍
				editor.html($scope.entity.goodsDesc.introduction);
				//显示图片列表
				$scope.entity.goodsDesc.itemImages=
					JSON.parse($scope.entity.goodsDesc.itemImages);
				//显示扩展属性
				$scope.entity.goodsDesc.customAttributeItems=
					JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//规格
				$scope.entity.goodsDesc.specificationItems=
					JSON.parse($scope.entity.goodsDesc.specificationItems);
				//SKU列表规格列转换
				for(var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec=
						JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);
	}

	//保存
	$scope.save=function(){
		//提取文本编辑器的值
		$scope.entity.goodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象
		console.log($scope.entity.goods.id)
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加
		}
		serviceObject.success(
			function(response){
				if(response.success){
					alert("保存成功");
					//跳转到商品列表页
					location.href="goods.html";
					//$scope.entity={};
					//edit.html("");
					//重新查询
					//$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}
		);
	}

	//增加商品
	/*$scope.add=function(){
		$scope.entity.goodsDesc.introduction=editor.html();

		goodsService.add( $scope.entity  ).success(
			function(response){
				if(response.success){
					alert("新增成功");
					$scope.entity={};
					editor.html("");//清空富文本编辑器
				}else{
					alert(response.message);
				}
			}
		);
	}*/

	//批量删除
	$scope.dele=function(){
		//获取选中的复选框
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}
			}
		);
	}

	$scope.searchEntity={};//定义搜索对象

	//搜索
	$scope.search=function(page,rows){
		console.log($scope.searchEntity)
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	}

	//上传图片
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(
			function(response){
				if(response.success){
					$scope.image_entity.url= response.message;
				}else{
					alert(response.message);
				}
			}
		);


	}

	$scope.entity={ goodsDesc:{itemImages:[],specificationItems:[]}  };

	//将当前上传的图片实体存入图片列表
	$scope.add_image_entity=function(){
		//如果去掉if判断 会报itemImages push of null 具体原因还不知道
		if($scope.entity.goodsDesc.itemImages==null){
			$scope.entity.goodsDesc.itemImages=[];
		}
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}

	//移除图片
	$scope.remove_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}

	//查询一级商品分类列表
	$scope.selectItemCat1List=function(){

		itemCatService.findByParentId(0).success(
			function(response){
				$scope.itemCat1List=response;
			}
		);

	}

	//查询二级商品分类列表
	$scope.$watch('entity.goods.category1Id',function(newValue,oldValue){
		if(newValue==null){
			return;
		}
		itemCatService.findByParentId(newValue).success(
			function(response){
				$scope.itemCat2List=response;
			}
		);

	});

	//查询三级商品分类列表
	$scope.$watch('entity.goods.category2Id',function(newValue,oldValue){
		if(newValue==null){
			return;
		}
		itemCatService.findByParentId(newValue).success(
			function(response){
				$scope.itemCat3List=response;
			}
		);

	});


	//读取模板ID
	$scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
		if(newValue==null){
			return;
		}
		itemCatService.findOne(newValue).success(
			function(response){
				$scope.entity.goods.typeTemplateId=response.typeId;
			}
		);
	});


	//读取模板ID后，读取品牌列表 扩展属性  规格列表
	$scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
		if(newValue==null){
			return;
		}

		//读取品牌列表和扩展属性
		typeTemplateService.findOne(newValue).success(
			function(response){
				$scope.typeTemplate=response;// 模板对象

				$scope.typeTemplate.brandIds= JSON.parse($scope.typeTemplate.brandIds);//品牌列表类型转换
				//如果没有ID，则加载模块中的扩展数据
				if($location.search()['id']==null){
					$scope.entity.goodsDesc.customAttributeItems=
						JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性
				}
			}
		);
		//读取规格
		typeTemplateService.findSpecList(newValue).success(
			function(response){
				$scope.specList=response;
			}
		);

	});

	$scope.updateSpecAttribute=function($event,name,value){
		var object=null;
		if($scope.entity.goodsDesc.specificationItems!=null){
			object= $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems ,'attributeName', name);
		}

		if(object!=null){
			if($event.target.checked ){
				object.attributeValue.push(value);
			}else{//取消勾选
				object.attributeValue.splice( object.attributeValue.indexOf(value ) ,1);//移除选项
				//如果选项都取消了，将此条记录移除
				if(object.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice(
						$scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}

			}
		}else{
			//如果去掉if判断 会报错 Cannot read porperty 'push' of null
			if($scope.entity.goodsDesc.specificationItems==null){
				$scope.entity.goodsDesc.specificationItems=[];
			}
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
	}

	//创建SKU列表
	$scope.createItemList=function(){

		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'} ];//列表初始化

		var items= $scope.entity.goodsDesc.specificationItems;

		for(var i=0;i<items.length;i++){
			$scope.entity.itemList= addColumn( $scope.entity.itemList, items[i].attributeName,items[i].attributeValue );
		}

	}

	addColumn=function(list,columnName,columnValues){

		var newList=[];
		for(var i=0;i< list.length;i++){
			var oldRow=  list[i];
			for(var j=0;j<columnValues.length;j++){
				var newRow=  JSON.parse( JSON.stringify(oldRow)  );//深克隆
				newRow.spec[columnName]=columnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}
	//显示状态
	$scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态

	$scope.itemCatList=[];		//商品分类列表
	//加载商品分类列表
	$scope.findItemCatList=function(){
		itemCatService.findAll().success(
			function(response){
				for(var i=0;i<response.length;i++){
					$scope.itemCatList[response[i].id]=response[i].name;
				}
			}
		)
	}

	//根据规格名称和选项名称返回是否被勾选
	$scope.checkAttributeValue=function(specName,optionName){
		var items=$scope.entity.goodsDesc.specificationItems;
		var object=null;
		if(items!=null){
			object=$scope.searchObjectByKey(items,'attributeName',specName);
		}

		if(object==null){
			return false;
		}else{
			if(object.attributeValue.indexOf(optionName)>0){
				return true;
			}else{
				return false;
			}
		}
	}
});
