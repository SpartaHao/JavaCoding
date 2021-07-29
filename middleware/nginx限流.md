##参考配置信息：  

```javascript
limit_req_zone $binary_remote_addr zone=rateLimiter:20m rate=1000r/m;

location /v1/test {
limit_req zone=rateLimiter burst=500 nodelay;
proxy_http_version 1.1;
proxy_set_header Connection "";
proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
client_body_buffer_size 1000k;
client_max_body_size 100m;
proxy_buffer_size 100k;
proxy_buffers 32 10k;
proxy_busy_buffers_size 300k;
proxy_connect_timeout 300s;
proxy_send_timeout 300s;
proxy_read_timeout 300s;
#proxy_set_header Host $http_host;
proxy_next_upstream off;
proxy_pass http://baidu.com;
}
```

###第一段配置参数：  
$binary_remote_addr ：表示通过remote_addr这个标识来做限制，“binary_”的目的是缩写内存占用量，是限制同一客户端ip地址  
zone=one:10m：表示生成一个大小为10M，名字为one的内存区域，用来存储访问的频次信息  
rate=1r/s：表示**允许相同标识的客户端的访问频次**，这里限制的是每秒1次，即每秒只处理一个请求，还可以有比如30r/m的，即限制每2秒访问一次，即每2秒才处理一个请求。  
 

###第二段配置参数：
zone=one ：设置使用哪个配置区域来做限制，与上面limit_req_zone 里的name对应    
burst=5：重点说明一下这个配置，burst爆发的意思，这个配置的意思是**设置一个大小为5的缓冲区**,当有大量请求（爆发）过来时，超过了访问频次限制的请求可以先放到这个缓冲区内等待，但是这个等待区里的位置只有5个，超过的请求会直接报503的错误然后返回。

nodelay：
**如果设置，会在瞬时提供处理(burst + rate)个请求的能力，请求超过（burst + rate）的时候就会直接返回503**，永远不存在请求需要等待的情况。（这里的rate的单位是：r/s）  
**如果没有设置，则所有请求会依次等待排队**
 
这里的burst参数主要采用了令牌桶算法。令牌桶算法是网络流量整形（Traffic Shaping）和速率限制（Rate Limiting）中最常用的一种算法。典型情况下，令牌桶算法用来控制发送到网络上的数据数目，并允许突发数据的发送。

###总结：
limit_req zone=req_zone;  
严格依照在limti_req_zone中配置的rate来处理请求,超过rate处理能力范围的，直接drop;表现为对收到的请求无延时

limit_req zone=req_zone burst=5;
依照在limti_req_zone中配置的rate来处理请求,同时设置了一个大小为5的缓冲队列，在缓冲队列中的请求会等待慢慢处理.  
**超过了burst缓冲队列长度和rate处理能力的请求被直接丢弃**,表现为对收到的请求有延时  

limit_req zone=req_zone burst=5 nodelay;  
依照在limti_req_zone中配置的rate来处理请求,同时设置了一个大小为5的缓冲队列，当请求到来时，会爆发出一个峰值处理能力，对于峰值处理数量之外的请求，直接丢弃;  
在完成峰值请求之后，缓冲队列不能再放入请求。如果rate＝10r/m，且这段时间内没有请求再到来，则每6 s 缓冲队列就能回复一个缓冲请求的能力，直到回复到能缓冲5个请求位置。

原文链接： https://blog.csdn.net/hellow__world/article/details/78658041
