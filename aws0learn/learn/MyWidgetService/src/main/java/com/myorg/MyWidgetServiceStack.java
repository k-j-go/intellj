package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.IBucket;
import software.amazon.awscdk.services.ses.actions.S3;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class MyWidgetServiceStack extends Stack {
    public MyWidgetServiceStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public MyWidgetServiceStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here

        // example resource
         final Queue queue = Queue.Builder.create(this, "MyWidgetServiceQueue")
                 .visibilityTimeout(Duration.seconds(300))
                 .build();
        final Bucket otherBucket = Bucket.Builder.create(this, "Other").build();
    }
}
