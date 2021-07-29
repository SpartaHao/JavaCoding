```javascript
package com.example.test.readygo.project.seckill;

import redis.clients.jedis.Jedis;

import java.util.Arrays;

/**
 * 通过lua脚本的原子性解决超卖问题
 * 但是依然会有库存遗留问题和同一个用户多次购买问题
 *
 * Lua脚本是类似Redis事务，有一定的原子性，不会被其他命令插队，可以完成一些Redis事务性的操作。
 */
public class SecKillByLua implements Runnable{
    private Jedis jedis = new Jedis("127.0.0.1", 6379);

    private String customerName;

    private String key;

    public SecKillByLua(String customerName, String key) {
        this.customerName = customerName;
        this.key = key;
    }

    @Override
    public void run() {
        jedis.auth("123456");
        String script = "if redis.call('GET',KEYS[1]) == ARGV[1] then " +
                "redis.call('decr',KEYS[1]) " +
                "return 'ok'" +
                "else " +
                "return nil " +
                "end";
        String data;
        int currentNum;
        data = jedis.get(key);
        currentNum = Integer.parseInt(data);
        if (currentNum > 0) {
            // 和watch命令加事务的原理是一样的，如果减的时候，发现被别人减了，直接返回失败
            // 如果并发很高，会导致大部分线程取的值是一样的，更新失败，导致库存遗留问题
            Object res = jedis.eval(script, Arrays.asList(key), Arrays.asList(String.valueOf(currentNum)));
            if (res == null) {
                System.out.println(customerName + " 抢购失败");
            } else {
                System.out.println(customerName + " 抢购成功,[" + key + "]剩余：" + (currentNum -1));
            }
        } else {
            System.out.println("商品售空,活动结束!");
            System.exit(0);
        }
    }
}

```
