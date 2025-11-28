/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.onsclient.v1_8;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.MessageExt;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import java.util.List;

final class OnsConsumerInstrumenter {

  private final Instrumenter<MessageExt, Void> singleProcessInstrumenter;
  private final Instrumenter<MessageExt, Void> batchProcessInstrumenter;
  private final Instrumenter<Void, Void> batchReceiveInstrumenter;

  OnsConsumerInstrumenter(
      Instrumenter<MessageExt, Void> singleProcessInstrumenter,
      Instrumenter<MessageExt, Void> batchProcessInstrumenter,
      Instrumenter<Void, Void> batchReceiveInstrumenter) {
    this.singleProcessInstrumenter = singleProcessInstrumenter;
    this.batchProcessInstrumenter = batchProcessInstrumenter;
    this.batchReceiveInstrumenter = batchReceiveInstrumenter;
  }

  Context start(Context parentContext, List<MessageExt> msgs) {
    if (msgs.size() == 1) {
      MessageExt msg = msgs.get(0);
      if (singleProcessInstrumenter.shouldStart(parentContext, msg)) {
        return singleProcessInstrumenter.start(parentContext, msg);
      }
      return parentContext;
    }

    if (!batchReceiveInstrumenter.shouldStart(parentContext, null)) {
      return parentContext;
    }
    Context rootContext = batchReceiveInstrumenter.start(parentContext, null);
    for (MessageExt msg : msgs) {
      if (batchProcessInstrumenter.shouldStart(rootContext, msg)) {
        Context child = batchProcessInstrumenter.start(rootContext, msg);
        batchProcessInstrumenter.end(child, msg, null, null);
      }
    }
    return rootContext;
  }

  void end(Context context, List<MessageExt> msgs) {
    if (msgs.size() == 1) {
      singleProcessInstrumenter.end(context, msgs.get(0), null, null);
    } else {
      batchReceiveInstrumenter.end(context, null, null, null);
    }
  }
}
