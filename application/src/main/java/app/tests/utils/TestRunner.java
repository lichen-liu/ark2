package app.tests.utils;

import java.util.ArrayList;

public class TestRunner implements Runnable {
    private final ArrayList<TestVoid> tests;
    private final ArrayList<Integer> exectutions;

    public TestRunner() {
        this.tests = new ArrayList<TestVoid>();
        this.exectutions = new ArrayList<>();
    }

    @Override
    public void run() {
        System.out.println("Test runner starts");
        assert tests.size() == exectutions.size();
        for (int i = 0; i < tests.size(); ++i) {
            for (int j = 0; j < exectutions.get(i); ++j) {
                tests.get(i).Test();
            }
        }
    }

    public void InsertNewTest(final TestVoid test, final int count) {
        tests.add(test);
        exectutions.add(count);
    }
}
