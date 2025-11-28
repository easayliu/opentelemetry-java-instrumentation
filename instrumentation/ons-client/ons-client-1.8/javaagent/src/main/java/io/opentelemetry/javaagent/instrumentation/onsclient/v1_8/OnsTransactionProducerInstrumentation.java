/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.onsclient.v1_8;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.producer.TransactionMQProducer;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class OnsTransactionProducerInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("com.aliyun.openservices.ons.api.impl.rocketmq.TransactionProducerImpl");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod().and(named("start")).and(takesArguments(0)),
        OnsTransactionProducerInstrumentation.class.getName() + "$StartAdvice");
  }

  @SuppressWarnings("unused")
  public static class StartAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(
        @Advice.FieldValue("transactionMQProducer") TransactionMQProducer transactionMqProducer) {
      DefaultMQProducerImpl producerImpl = transactionMqProducer.getDefaultMQProducerImpl();
      producerImpl.registerSendMessageHook(OnsClientHooks.SEND_MESSAGE_HOOK);
    }
  }
}
