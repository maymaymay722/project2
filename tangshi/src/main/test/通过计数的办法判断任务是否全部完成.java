import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class 通过计数的办法判断任务是否全部完成 {
    private static int COUNT=10;

    //private static int successCount=0;
    //private static int failureCount=0;

    private static AtomicInteger successCount=new AtomicInteger(0);
    private static AtomicInteger failureCount=new AtomicInteger(0);

    private static class Job implements Runnable{
        private void work() throws IOException, InterruptedException {
            Random random=new Random();
            int n=random.nextInt(5);
            if(n<2){
                throw  new IOException();
            }
            Thread.sleep(5);
        }

        @Override
        public void run(){
            try{
                work();
                successCount.getAndIncrement();
                //successCount++;
            }catch (IOException | InterruptedException e){
                failureCount.getAndIncrement();
                //failureCount++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < COUNT; i++) {
            Thread thread=new Thread(new Job());
            thread.start();
        }

        while(successCount.get()+failureCount.get() != COUNT){
            Thread.sleep(1000);
            System.out.println("任务没有全部完成");
        }
        System.out.println("任务全部完成");
    }
}
