package common.protocol;

import java.util.concurrent.atomic.AtomicInteger;

public class SequenceIdGenerator {
    private static final AtomicInteger id = new AtomicInteger();


    public static Integer getId(){
        return id.incrementAndGet();
    }


}
