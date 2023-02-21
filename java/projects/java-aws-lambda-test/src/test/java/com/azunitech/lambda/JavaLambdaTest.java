package com.azunitech.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JavaLambdaTest {
    private static Map<String, Object> input;

    @Before
    public void createInput() throws IOException {
        // TODO: set up your sample input object here.
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("employee_id", "99");
        hashMap.put("employee_name", "Jimmy");
        hashMap.put("expense_type", "travel");
        hashMap.put("amount", "465.98");
        input = hashMap ;
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("LambdaForm");

        return ctx;
    }

    @Test
    public void testLambdaFormFunctionHandler() throws IOException {
        createInput();
        JavaLambda handler = new JavaLambda();
        Context ctx = createContext();

        Object output = handler.handleRequest(input, ctx);

        // TODO: validate output here if needed.
        if (output != null) {
            System.out.println(output.toString());
        }
    }
}
