/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.onsclient.v1_8;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class OnsOrderConsumerInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("com.aliyun.openservices.ons.api.impl.rocketmq.OrderConsumerImpl");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod().and(named("start")).and(takesArguments(0)),
        OnsOrderConsumerInstrumentation.class.getName() + "$StartAdvice");
  }

  @SuppressWarnings("unused")
  public static class StartAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(
        @Advice.FieldValue("defaultMQPushConsumer") DefaultMQPushConsumer defaultMqPushConsumer) {
      DefaultMQPushConsumerImpl consumerImpl = defaultMqPushConsumer.getDefaultMQPushConsumerImpl();
      consumerImpl.registerConsumeMessageHook(OnsClientHooks.CONSUME_MESSAGE_HOOK);
    }
  }
}
