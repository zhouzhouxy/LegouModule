网页静态化解决方案-Freemarker
对于电商网站的商品详细页来说，至少几百万个商品，每个商品又有大量的消息，这样
的情况也适用于使用网页静态化来解决

网页静态化技术和缓存技术的共同点都是为了减轻数据库的访问压力，但是具体的应用
场景不同，缓存比较适合小规模的数据，而网页静态化比较合适大规模且相对变化不太频繁
的数据，另外网页静态化还有利于SEO

另外我们如果将网页以纯静态化的形式展现，就可以使用Nginx这样的高性能的web服务器来部署。 Nginx可以承载5万的并发，而Tomcat只有几百


分布式文件服务器FastDFS
什么是FastDFS
什么是FastDFS使用C语言编写的一款开源的分布式文件系统。FastDFS为互联网量身定制
充分考虑了冗余备份、负载均衡、线性扩容等机制，并注重高可用、高性能等指标，
使用FastDFS很容易搭建一套高性能的文件服务器集群提供文件上传、下载等服务


什么是消息中间件
	消息中间利用高效可靠的消息传递机制进行平台无关的数据交流，并基于数据通信来进行分布式系统的集成，
	通过提供消息传递和消息排队模型，他可以在分布式环境下扩展进程间的通信，对于消息中间件，常用的
	角色大致也就有Producer（生产者）、Consumer（消费者）