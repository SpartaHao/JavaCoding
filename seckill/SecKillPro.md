```javascript
package com.example.test.readygo.project.seckill;

import redis.clients.jedis.Jedis;

/**
 * lua脚本实现最终版本：可同时解决超卖、库存遗留、同一用户多次购买问题
 */
public class SecKillPro implements Runnable{
    private Jedis jedis = new Jedis("127.0.0.1", 6379);

    public static final String USER_SET_KEY = "luckyUsers3";

    private String customerName;

    private String key;

    public SecKillPro(String customerName, String key) {
        this.customerName = customerName;
        this.key = key;
    }

    @Override
    public void run() {
        jedis.auth("123456");
        // 要保证存放奇数的key和set的key在同一个slot，否则批量执行会失败
        // 本来是想把userid eval执行脚本时，当做key传递的。此时已经保证USER_SET_KEY和key在同一个slot上了，因为是set，userid作为
        // set中的一个元素是不会进行hash校验的，但是实际上传入的key都会校验，所以才有这种写法
        String script = "local userid = '" + customerName + "';" +
                "local userSetKey = '"  + USER_SET_KEY + "';" +
                "local userExists = redis.call('sismember', userSetKey, userid);" +
                "if tonumber(userExists) == 1 then " +
                "return 2; " +
                "end; " +
                "local cnt = redis.call('get', KEYS[1]);" +
                "if tonumber(cnt) <= 0 then " +
                "return 1; " +
                "else " +
                "redis.call('decr', KEYS[1]);" +
                "redis.call('sadd', userSetKey, userid);" +
                "end; " +
                "return 0";

        Object res = jedis.eval(script, 1, key);
        String resStr = String.valueOf(res);
        if ("1".equalsIgnoreCase(resStr)) {
            System.out.println("商品售空,活动结束!");
            System.exit(0);
        } else if ("2".equalsIgnoreCase(resStr)) {
            System.out.println(customerName + " 已经抢过");
        } else if ("0".equalsIgnoreCase(resStr)){
            System.out.println(customerName + " 抢购成功");
        } else {
            System.out.println("抢购异常！");
        }
    }
}

```
