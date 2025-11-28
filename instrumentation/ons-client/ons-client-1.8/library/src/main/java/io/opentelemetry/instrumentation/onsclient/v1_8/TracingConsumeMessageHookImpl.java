/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.onsclient.v1_8;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.hook.ConsumeMessageContext;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.hook.ConsumeMessageHook;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.util.VirtualField;

final class TracingConsumeMessageHookImpl implements ConsumeMessageHook {

  private static final VirtualField<ConsumeMessageContext, ContextAndScope> contextAndScopeField =
      VirtualField.find(ConsumeMessageContext.class, ContextAndScope.class);

  private final OnsConsumerInstrumenter instrumenter;

  TracingConsumeMessageHookImpl(OnsConsumerInstrumenter instrumenter) {
    this.instrumenter = instrumenter;
  }

  @Override
  public String hookName() {
    return "OpenTelemetryOnsConsumeMessageTraceHook";
  }

  @Override
  public void consumeMessageBefore(ConsumeMessageContext context) {
    if (context == null || context.getMsgList() == null || context.getMsgList().isEmpty()) {
      return;
    }
    Context parentContext = Context.current();
    Context newContext = instrumenter.start(parentContext, context.getMsgList());

    // it's safe to store the scope in the ons message context, both before() and after()
    // methods are always called from the same thread; see:
    // - ConsumeMessageConcurrentlyService$ConsumeRequest#run()
    // - ConsumeMessageOrderlyService$ConsumeRequest#run()
    if (newContext != parentContext) {
      contextAndScopeField.set(
          context, ContextAndScope.create(newContext, newContext.makeCurrent()));
    }
  }

  @Override
  public void consumeMessageAfter(ConsumeMessageContext context) {
    if (context == null || context.getMsgList() == null || context.getMsgList().isEmpty()) {
      return;
    }
    ContextAndScope contextAndScope = contextAndScopeField.get(context);
    if (contextAndScope != null) {
      contextAndScope.close();
      instrumenter.end(contextAndScope.getContext(), context.getMsgList());
    }
  }
}
