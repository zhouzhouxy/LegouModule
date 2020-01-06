app.controller('brandController',function($scope,$controller,brandService){
    $controller('baseController',{$scope:$scope}) ;      //继承

    //读取列表数据绑定到表单中
    $scope.findAll=function(){
        brandService.findAll().success(
            function(response){
                console.log(response)
                $scope.list=response.list;
            }
        );
    }
    //分页
    $scope.findPage=function(page,rows){
        brandService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总页数
            })
    }



    //保存
    $scope.save=function(){
        var methodName='add';	//方法名称
        var object=null;
        if($scope.entity.id!=null){	//如果有ID
            //methodName='update';	//则执行修改方法
            object=brandService.update($scope.entity);
        }else{
            object=brandService.add($scope.entity);
        }
        object.success(
            function(response){
                if(response.success){
                    //重新查询
                    $scope.reloadList();//重新加载
                }else{
                    alert(response.message);
                }
            }
        )
    }
    //查询实体
    $scope.findOne=function(id){
        brandService.findOne(id).success(

            function(response){
                console.log(response)
                $scope.entity=response;
            }
        )
    }


    //批量删除
    $scope.dele=function(){
        //获取选中的复选框
        brandService.dele($scope.selectIds).success(
            function(response){
                if(response.success){
                    $scope.reloadList();	//刷新列表
                }else{
                    alert($scope.message)
                }
            }
        )
    }

    $scope.searchEntity={};//定义搜索对象
    //条件查询
    $scope.search=function(page,rows){
        brandService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.paginationConf.totalItems=response.total; //总记录数
                $scope.list=response.rows;      //给列表变量赋值
            }
        )
    }
})