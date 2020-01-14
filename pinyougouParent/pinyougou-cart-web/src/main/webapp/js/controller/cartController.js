app.controller('cartController',function($scope,cartService){
    //查询购物车列表
    $scope.findCartList=function(){
        cartService.findCartList().success(
            function(response){
                $scope.cartList=response;
                $scope.totalValue=cartService.sum($scope.cartList);
            }
        )
    }

    //添加商品到购物车
    $scope.addGoodsToCartList=function(itemId,num){
        cartService.addGoodsToCartList(itemId,num).success(
            function(response){
                if(response.success){
                    //如果添加成功刷新列表
                    $scope.findCartList();
                }else{
                    //弹出错误提示
                    alert(response.message);
                }
            }
        )
    }

    //获取地址列表
    $scope.findAddressList=function(){
        cartService.findAddressList().success(
            function(response){
                $scope.addressList=response;
                //设置默认地址
                for(var i=0;i<$scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }
            }
        )
    }

    //选择地址
    $scope.selectAddress=function(address){
        $scope.address=address;
    }

    //判断是否是当前选中的地址
    $scope.isSelectedAddress=function(address){
        if(address==$scope.address){
            return true;
        }else{
            return false;
        }
    }

    $scope.order={paymentType:'1'};
    //选择支付方式
    $scope.selectPayType=function(type){
        $scope.order.paymentType=type;
    }

    $scope.submitOrder=function(){
        //地址
        $scope.order.receiverAreaName=$scope.address.address;
        //手机
        $scope.order.receiverMobile=$scope.address.mobile;
        //联系人
        $scope.order.receiver=$scope.address.contact;
        cartService.submitOrder($scope.order).success(
            function(response){
                console.log(response)
                if(response.success){
                    if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
                       // location.href="pay.html";
                    }else{
                        //如果，跳转提示页面
                        //location.href="paysuccess.html";
                    }
                }else{
                    //也可以跳转到提示页面
                    alert(response.message);
                }
            }
        )
    }
});