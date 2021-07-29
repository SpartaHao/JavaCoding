```javascript
package com.example.test.readygo.project.seckill;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * 在redis提供了incr命令进行递增操作，可以保证原子性。利用watch实现也可以实现递增递减的操作。
 * WATCH命令可以监控一个或多个键，一旦其中有一个键被修改（或删除），之后的事务就不会执行。监控一直持续到EXEC命令（事务中的命令是在EXEC之后才执行的，所以在MULTI命令后可以修改WATCH监控的键值）
 *
 * 链接：https://www.jianshu.com/p/93cd65d07b56
 */
public class SecKill implements Runnable{
    private Jedis jedis = new Jedis("127.0.0.1", 6379);

    private String customerName;

    private String key;

    public SecKill(String customerName, String key) {
        this.customerName = customerName;
        this.key = key;
    }

    @Override
    public void run() {
        jedis.auth("123456");
        boolean success = false;
        String data;
        int currentNum;
        while (!success) {//可重复抢购直到成功
            //通过watch实现redis的incr(原子递增操作)
            jedis.watch(key);
            data = jedis.get(key);
            currentNum = Integer.parseInt(data);
            if (currentNum > 0) {
                //开启事务
                Transaction transaction = jedis.multi();
                //设置新值,如果key的值被其它连接的客户端修改，那么当前连接的exec命令将执行失败
                currentNum--;
                transaction.set(key, String.valueOf(currentNum));
                List res = transaction.exec();
                if (res == null || res.size() == 0) {
                    System.out.println(customerName + " 抢购失败");
                    success = false;
                } else {
                    success = true;
                    System.out.println(customerName + " 抢购成功,[" + key + "]剩余：" + currentNum);
                }
            } else {
                System.out.println("商品售空,活动结束!");
                System.exit(0);
            }
        }
    }
}

```
