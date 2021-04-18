package app.tests.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class TestRunner implements Runnable {
    private final Logger logger;
    private final ArrayList<TestVoid> tests;
    private final ArrayList<Integer> exectutions;
    private final HashMap<TestVoid, Object> outputs;
    private final String identifier;

    public TestRunner(final String identifier) {
        this.tests = new ArrayList<TestVoid>();
        this.exectutions = new ArrayList<>();
        this.outputs = new HashMap<TestVoid, Object>();
        this.logger = new Logger();
        this.identifier = identifier;
    }

    @Override
    public void run() {
        System.out.println("Test runner starts");
        assert tests.size() == exectutions.size();
        for (int i = 0; i < tests.size(); ++i) {
            for (int j = 0; j < exectutions.get(i); ++j) {
                final TestVoid test = tests.get(i);
                final Object output = test.Test();

                System.out.println(String.format("\n%s output : ", identifier));

                if (output instanceof String) {
                    logger.print((String) output);
                } else if (output instanceof String[]) {
                    logger.print((String[]) output);
                } else {
                    System.out.println("Unhandled type: " + output);
                }

                outputs.put(test, output);
            }
        }
    }

    public void insertNewTest(final TestVoid test, final int count) {
        tests.add(test);
        exectutions.add(count);
    }

    public Object getOutput(final TestVoid test) {
        return outputs.get(test);
    }
}
