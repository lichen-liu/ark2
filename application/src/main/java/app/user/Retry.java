package app.user;

import java.util.function.Supplier;

public class Retry<T> {
    private int maxRetries;
    private int retryCount = 0;
    private Class<?> expectedExceptionType;

    public Retry(int maxRetries, Class<?> expectedExceptionType ){

        assert Exception.class.isAssignableFrom(expectedExceptionType) : new Exception("The type is not an Exception type"); 

        this.maxRetries = maxRetries;
        this.expectedExceptionType = expectedExceptionType;
    }

    public T run(Supplier<T> action){
        try {
            return action.get();
        } catch(Exception e) {
            if(isExpectedExceptionType(e)){
                return retry(action);
            }
            throw new RuntimeException("UNEXPECTED EXCEPTION thrown, stack trace: " + e.getStackTrace());
        }
    }

    private T retry(Supplier<T> action){
        System.out.println("FAILED, will be retried " + maxRetries + " times.");
        while (retryCount < maxRetries) {
            try {
                return action.get();
            } catch (Exception e) {

                if(!isExpectedExceptionType(e)){
                    throw new RuntimeException("UNEXPECTED EXCEPTION thrown, stack trace: " + e.getStackTrace());
                }

                ++retryCount;
                System.out.println("FAILED on retry " + retryCount + " of " + maxRetries + " error: " + e );
                if (retryCount >= maxRetries) {
                    System.out.println("Max retries reached");
                    break;
                }
            }
        }

        throw new RuntimeException("Command failed on all of " + maxRetries + " retries");
    }

    private Boolean isExpectedExceptionType(Exception e){
        return e.getClass() == expectedExceptionType;
    }
}
