plugins {
  id("otel.javaagent-instrumentation")
}

muzzle {
  pass {
    group.set("com.aliyun.openservices")
    module.set("ons-client")
    versions.set("[1.8.0.Final,)")
    assertInverse.set(true)
  }
  pass {
    group.set("com.aliyun.openservices")
    module.set("ons-client-ext")
    versions.set("[1.8.0.Final,)")
    assertInverse.set(true)
  }
}

dependencies {
  compileOnly("com.aliyun.openservices:ons-client:1.8.4.Final")

  implementation(project(":instrumentation:ons-client:ons-client-1.8:library"))
}

tasks.withType<Test>().configureEach {
  jvmArgs("-Dotel.instrumentation.ons-client.experimental-span-attributes=true")
}
