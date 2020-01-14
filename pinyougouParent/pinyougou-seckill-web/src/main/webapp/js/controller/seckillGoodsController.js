//控制层
app.controller('seckillGoodsController' ,function($scope,$location,seckillGoodsService,$interval){
	//读取列表数据绑定到表单中
	$scope.findList=function(){
		seckillGoodsService.findList().success(
			function(response){
				$scope.list=response[0];
				console.log($scope.list)
			}
		);
	}
	//查询商品
	$scope.findOne=function(){
		seckillGoodsService.findOne($location.search()['id']).success(
			function(response){
				$scope.entity=response;
				//倒计时开始
				//总秒数
				allsecond=Math.floor((new Date($scope.entity.endTime).getTime()-
					(new Date().getTime()))/1000);
				time=$interval(function(){

					if(allsecond>0){
						allsecond=allsecond-1;
						//转换时间字符串
						$scope.timeString=convertTimeString(allsecond);
					}else{
						$interval.cancel(time);
						alert("秒杀服务已结束")
					}
				},1000)
			}
		)
	}
	
	//转换秒为 天小时分钟秒格式 xxx天 10:22:33
	convertTimeString=function(allsecond){
		//天数
		var days=Math.floor(allsecond/(60*60*24));
		//小时数
		var hours=Math.floor((allsecond-days*60*60*24)/(60*60));
		//分钟数
		var minutes=Math.floor((allsecond-days*24*60*60-hours*60*60)/(60));
		//秒
		var seconds=allsecond-days*60*60*24-hours*60*60-minutes*60;
		var timeString="";
		if(days>0){
			timeString=days+"天";
		}
		return timeString+hours+":"+minutes+":"+seconds;
	}
	
	//提交订单
	$scope.submitOrder=function(){
		seckillGoodsService.submitOrder($scope.entity.id).success(
			function(response){
				if(response.success) {
					alert("下单成功，请在一分钟内完成支付");
					location.href="pay.html";
				}else{
					alert(response.message);
				}
			}
		)
	}
	
});