```javascript

package com.example.test.readygo.project.seckill;

import com.example.test.readygo.project.seckill.SecKill;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecKillTest {
    private static String key = "macbook";

    private static String num = "50";

    private static ExecutorService executorService = Executors.newFixedThreadPool(8);

    public static void init() {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("123456");
        String script = "redis.call('del',KEYS[1]);return redis.call('set',KEYS[1],ARGV[1])";
        jedis.eval(script, Collections.singletonList(key), Collections.singletonList(num));
        jedis.close();
    }

    public static void main(String[] args) {
        init();
        try {
            for (int i = 1; i <= 200; i++) {
                executorService.submit(new SecKillPro("user" + i, key));
            }
        } finally {
            executorService.shutdown();
        }
    }
}
```
