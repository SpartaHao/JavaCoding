import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 使用Object wait()和notifyAll实现生产者和消费者
 * 这种写法有个问题就是只有当共享队列满了才能消费或者当共享队列空了才能生产
 */
public class ProducerAndConsumer1 {
    static Random random = new Random();
    static Queue<Integer> shareQueue = new LinkedList<>();
    static int max = 5;

    public static void main(String[] args) throws InterruptedException{
        Thread producerThread = new Producer(shareQueue, max , "PRODUCER");
        Thread consumerThread = new Consumer(shareQueue, max , "CONSUMER");
        producerThread.start();
        consumerThread.start();
        TimeUnit.SECONDS.sleep(2);
    }

    static class Producer extends Thread {
        Queue<Integer> queue;
        int maxSize;
        String name;

        public Producer(Queue<Integer> queue, int maxSize, String name1) {
            this.queue = queue;
            this.maxSize = maxSize;
            this.name = name1;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (queue) {
                    while (queue.size() == maxSize) {
                        System.out.println("The queue is full, waiting for consumer to work.");
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    int num = random.nextInt(100);
                    queue.offer(num);
                    System.out.println("producer add " + num + " into queue.");
                    // 如果另外的一个线程调用了相同对象的notifyAll()方法，那么处于该对象的等待池中的线程就会全部进入该对象的锁池中，
                    // 准备争夺锁的拥有权。本线程不会唤醒本线程
                    queue.notifyAll();
                }
            }
        }
    }

    static class Consumer extends Thread {
        Queue<Integer> queue;
        int maxSize;
        String name;

        public Consumer(Queue<Integer> queue, int maxSize, String name1) {
            this.queue = queue;
            this.maxSize = maxSize;
            this.name = name1;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        System.out.println("The queue is empty, waiting for producer to work.");
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("consumer poll " + queue.poll() + " from the queue.");
                    /**
                     * 当有线程调用了对象的 notifyAll()方法（唤醒所有 wait 线程）或 notify()方法（只随机唤醒一个 wait 线程），被唤醒的的线程便会进入该对象的锁池中，锁池中的线程会去竞争该对象锁。也就是说，调用了notify后只要一个线程会由等待池进入锁池，而notifyAll会将该对象等待池内的所有线程移动到锁池中，等待锁竞争
                     * https://blog.csdn.net/djzhao/article/details/79410229
                     */
                    queue.notifyAll();
                }
            }
        }
    }
}
