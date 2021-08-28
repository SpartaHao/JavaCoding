import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 这种方式更好些，因为生产或消费的时候不是一直while true，执行完produce和consume方法就会让出锁对象，可以交替生产和消费
 */
public class ProducerAndConsumer2 {
    public static Queue<Integer> store = new LinkedList<>();
    public static final int MAX_SIZE = 5;
    public static AtomicInteger cnt = new AtomicInteger(0);

    public synchronized void produce() {
        while (store.size() == MAX_SIZE) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        store.offer(cnt.incrementAndGet());
        System.out.println("生产 " + cnt.get() + " 成功");
        this.notifyAll();
    }

    public synchronized void consume() {
        while (store.size() == 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("消费 " + store.poll() + " 成功");
        this.notifyAll();
    }

    static class Producer implements Runnable{
        ProducerAndConsumer2 pac;

        public Producer(ProducerAndConsumer2 pac) {
            this.pac = pac;
        }

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                pac.produce();
            }
        }
    }

    static class Consumer implements Runnable {
        ProducerAndConsumer2 pac;

        public Consumer(ProducerAndConsumer2 pac) {
            this.pac = pac;
        }

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                pac.consume();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ProducerAndConsumer2 pac = new ProducerAndConsumer2();
        new Thread(new Producer(pac)).start();
        new Thread(new Consumer(pac)).start();
        TimeUnit.SECONDS.sleep(2);
    }
}
