// 定义模块:
var app = angular.module("pinyougou",[]);
/*$sce服务写成过滤器*/
app.filter('trustHtml',['$sce',function($sce){
    //传入参数时被过滤的内容
    return function(data){
        //返回是过滤后的内容（信任html的转换)
        return $sce.trustAsHtml(data);
    }
}])