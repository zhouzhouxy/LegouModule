app.controller('contentController',function($scope,contentService){
	
	$scope.contentList=[];//广告列表
	
	$scope.findByCategoryId=function(categoryId){
		contentService.findByCategoryId(categoryId).success(
			function(response){
				$scope.contentList[categoryId]=response;
				console.log($scope.contentList[1])
			}
		);
	}
	//搜索跳转
	$scope.search=function(){
		location.href="http://localhost:9105/search.html#?keywords="+$scope.keywords;
	}
});