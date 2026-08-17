# 环境安装

## 一、JDK安装

1、什么是JDK？

JDK（Java Development Kit）是 Java 开发工具包，它是进行 Java 后端开发的核心基础。

2、Windows中如何安装JDK？

① JDK下载

在JDK官网 [JDK下载官网](https://www.oracle.com/java/technologies/downloads/) 下载即可，优先推荐下载解压缩版本，然后解压在一个目录

② 配置环境变量（系统变量）

需要配置JAVA_HOME以及Path

![image-20260811183454066](./assets/image-20260811183454066.png) 

Path配置的值为：

```tex
%JAVA_HOME%\bin
```

③ 验证JDK是否安装成功,配置环境变量之后，进如cmd命令行输入 java -version 命令查看是否安装成功

![image-20260811183735106](./assets/image-20260811183735106.png) 

当输入指令后出现以上界面时表示JDK安装成功。



3、Windows如何切换不同版本JDK

① 已经下载不同版本的JDK压缩包并解压在相应的目录下面

② 使用下面的cmd程序进行切换

```bash
@echo off
cls
echo ------------------------------------------------
echo current Java version is:
java -version
echo ------------------------------------------------
echo input need java version option:
echo options   content
echo 8      switch to jdk8
echo 17     switch to jdk17
echo 21     switch to jdk21
echo ------------------------------------------------
set /P choose=please input option:
IF "%choose%"=="8" (
    setx JAVA_HOME "C:\Program Files\Java\jdk1.8.0_201" /M
    echo has switch to JDK8
) ELSE IF "%choose%"=="17" (
    setx JAVA_HOME "C:\Program Files\Java\jdk-17.0.12" /M
    echo has switch to JDK17
) ELSE IF "%choose%"=="21" (
    setx JAVA_HOME "C:\Program Files\Java\jdk-21" /M
    echo has switch to JDK21
) ELSE (
    echo input error！！！
)
pause
```

其中JAVA_HOME就是我们先前建立的环境变量，简而言之就是修改环境变量的值从而达到切换JDK的效果。
