import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 采用阻塞队列实现生产者消费者模式
 * 阻塞队列实现生产者消费者模式超级简单，它提供开箱即用支持阻塞的方法put()和take()，开发者不需要写困惑的wait-nofity代码去实现通信。
 */
public class ProducerAndConsumerByBlockingQueue {
    public static AtomicInteger cnt = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue store = new LinkedBlockingQueue<>(5);
        new Thread(new CustomProducer(store)).start();
        new Thread(new CustomConsumer(store)).start();
        TimeUnit.SECONDS.sleep(2);
    }

    static class CustomProducer implements Runnable {
        BlockingQueue queue;

        public CustomProducer(BlockingQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                try {
                    queue.put(cnt.incrementAndGet());
                    System.out.println("生产 " + cnt.get() + " 成功");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class CustomConsumer implements Runnable {
        BlockingQueue queue;

        public CustomConsumer(BlockingQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                try {
                    System.out.println("消费 " + queue.take() + " 成功");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
