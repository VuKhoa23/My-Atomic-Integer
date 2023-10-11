import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class Main {
    public static void main(String[] args) {
        MyAtomicInteger myInt = new MyAtomicInteger(0);

        Thread t1 = new Thread(()->{
            for (int i = 0; i < 1000; i++) {
                myInt.increase();
            }
        });
        Thread t2 = new Thread(()->{
            for (int i = 0; i < 1000; i++) {
                myInt.decrease();
            }
        });
        t1.setDaemon(true);
        t2.setDaemon(true);
        t1.start();
        t2.start();
        try {
            t1.join(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            t2.join(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Value after processing: " + myInt.getValue());
    }

    public static class MyAtomicInteger{
        private AtomicReference<Integer> ref;

        MyAtomicInteger(int a){
            Integer initValue = new Integer(a);
            ref = new AtomicReference<>(initValue);
        }

        public void increase(){
            Integer currentValue = ref.get();
            Integer newValue = new Integer(currentValue + 1);
            while(true){
                if(ref.compareAndSet(currentValue, newValue)){
                    break;
                }
                else{
                    // reassign the current value
                    currentValue = ref.get();
                    newValue = new Integer(currentValue + 1);
                }
            }
        }

        public void decrease(){
            Integer currentValue = ref.get();
            Integer newValue = new Integer(currentValue - 1);
            while(true){
                if(ref.compareAndSet(currentValue, newValue)){
                    break;
                }
                else{
                    // reassign the current value
                    currentValue = ref.get();
                    newValue = new Integer(currentValue - 1);
                }
            }
        }

        public int getValue(){
            return ref.get().intValue();
        }
    }
}