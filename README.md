# jmutils

[![Maven Central](https://maven-badges.sml.io/sonatype-central/tech.aliorpse/jmutils/badge.svg)](https://central.sonatype.com/artifact/tech.aliorpse/jmutils)

基于 kotlin 的 JMComic 库

使用 Web API, 对 IP 要求有点高, 不过可以自定义接口的

默认实现不改动任何配置的情况下, Tests 中那 194 页我自己机子上跑8秒就好了, 主打一个快

## 安装

```kotlin
repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}


dependencies {
    implementation("tech.aliorpse:jmutils:$version")
    implementation("io.ktor:ktor-client-cio:$version") // or the client you want
    implementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:$version") // your platform
}
```

## 功能

### 获取 Album

```kotlin
runBlocking {
    val albumId = 1216733
    
    val result = getAlbumInfo(albumId)
    val time = measureTimeMillis {
        result.download().exportPdf(
            "./albums/$albumId.pdf",
            albumId.toString()
        )
    }
    println("Finished, used ${time}ms")
}
```