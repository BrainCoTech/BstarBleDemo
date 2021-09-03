# Bstar-Ble Android SDK

## Gradle引入

```groovy
repositories {
	maven { url "https://jitpack.io" }
  maven {
    credentials {
      username 'maven-read'
      password 'tBauTKVo6IqiKvHd'
    }
  	url = "https://nexus.ci.brainco.cn/repository/maven-public"
  }
}

dependencies {
    implementation 'tech.brainco:bstarblesdk:1.0.0'
}
```


## 使用

使用SDK前需确保APP有蓝牙权限，参考 [Bluetooth permissions](https://developer.android.google.cn/guide/topics/connectivity/bluetooth/permissions)

### 初始化

```java
BstarSDK.init(this, () -> {
    //完成初始化
});
```

### 获取设备列表

```java
BstarSDK.getDevices();
```

### 监听设备列表更新

```kotlin
BstarSDK.setBstarDevicesListener(devices -> {
    
});
```

### 扫描设备

```Java
BstarSDK.scanDevices(new Result<List<String>>() {
    @Override
    public void onResult(List<String> strings) {
        //扫描成功, result: 设备id列表
    }

    @Override
    public void onError(@NonNull Throwable throwable) {
        //扫描失败
    }
});
```

### 配置设备

```kotlin
BstarSDK.setHubConfig(list, new Result<List<String>>() {
    @Override
    public void onResult(List<String> strings) {
        //配置成功
    }

    @Override
    public void onError(@NonNull Throwable throwable) {
        //配置失败
    }
});
```

### 设备相关接口

以下代码出现的`device`是`BstarDevice`实例

#### 连接状态

```Java
//设备是否连接
device.getConnected();
//监听设备连接状态变化
device.setContactStateChangeListener(connected -> {
    
});
```

#### 佩戴状态

```Java
//设备是否佩戴
device.getContacted();
//监听设备佩戴状态变化
device.setContactStateChangeListener(contacted -> {
    
});
```

#### 专注力值

```Java
device.setAttentionListener(attention -> {

});
```

## Proguard

```
-keep class tech.brainco.** {*;}
-keep class com.sun.jna.** {*;}
```